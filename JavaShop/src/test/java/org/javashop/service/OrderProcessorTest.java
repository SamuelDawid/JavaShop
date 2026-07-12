package org.javashop.service;

import org.javashop.domain.User.Account;
import org.javashop.domain.resources.Computer;
import org.javashop.domain.resources.Electronics;
import org.javashop.enums.AccountType;
import org.javashop.enums.pc.CPU;
import org.javashop.enums.pc.GPU;
import org.javashop.enums.pc.RAM;
import org.javashop.interfaces.DiscountPolicy;
import org.javashop.models.Cart;
import org.javashop.models.Invoice;
import org.javashop.models.Order;
import org.javashop.repo.ProductsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderProcessorTest {
    @Mock
    DiscountPolicy policy;
    @Mock
    ProductManager productManager;
    @InjectMocks
    OrderProcessor orderProcessor;

    Order testOrder;
    Cart testCart;
    Computer gaming, office;

    @BeforeEach
    void setUp() {
        gaming = new Computer("PC-1", "Gaming Beast", new BigDecimal("3999.99"), 5,
                CPU.AMD, GPU.NVIDIA, RAM.GB32);
        office = new Computer("PC-2", "Office Pro", new BigDecimal("1499.99"), 10,
                CPU.INTEL, GPU.INTEL, RAM.GB16);
        Account testAccount = new Account("123-123", "Test Subject", AccountType.NORMAL);
        testCart = new Cart(testAccount);

    }

//    @Nested
//    class ProductRepositoryTest {
//        ProductsRepository productRepository = new ProductsRepository() {
//        };
//
//        @Test
//        void shouldReturnTrueIfProductSaved() {
//            assertThat(productRepository.save(gaming)).isTrue();
//            assertThat(productRepository.findAll()).hasSize(1);
//        }
//
//        @Test
//        void shouldReturnProductFromID() {
//            productRepository.save(gaming);
//            Electronics result = productRepository.findById("PC-1").orElseThrow(() -> new AssertionError("Product not found after update"));
//            assertThat(result).isEqualTo(gaming);
//        }
//
//        @Test
//        void shouldThrowNoSuchElementException() {
//            NoSuchElementException ex = assertThrows(NoSuchElementException.class, () -> productRepository.findById("notExisting").get());
//            assertThat(ex.getMessage()).isEqualTo("No value present");
//        }
//
//        @Test
//        void shouldUpdateProduct() {
//            productRepository.save(gaming);
//            Electronics gamingUpdated = new Electronics("PC-1", "Even Better", new BigDecimal("5000"), 15);
//            productRepository.update(gaming.getId(), gamingUpdated);
//            Electronics result = productRepository.findById(gaming.getId()).orElseThrow(() -> new AssertionError("Product not found after update"));
//            assertAll(
//                    () -> assertThat(result.getId()).isEqualTo("PC-1"),
//                    () -> assertThat(result.getName()).isEqualTo("Even Better"),
//                    () -> assertThat(result.getPrice()).isEqualByComparingTo(new BigDecimal("5000")),
//                    () -> assertThat(result.getQuantity()).isEqualTo(15)
//            );
//        }
//
//        @Test
//        void shouldNotAddNewProductWhenUpdatingNonExistent() {
//            Electronics ghost = new Electronics("GHOST-1", "Ghost", new BigDecimal("100"), 1);
//            productRepository.update("GHOST-1", ghost);
//            assertThat(productRepository.findById("GHOST-1")).isEmpty();
//        }
//
//        @Test
//        void shouldDeleteProductFromRepository() {
//            productRepository.save(gaming);
//            assertThat(productRepository.delete(gaming.getId())).isTrue();
//        }
//
//        @Test
//        void shouldNotDeleteProductFromRepository() {
//            assertThat(productRepository.delete("lol")).isFalse();
//        }
//    }

    @Nested
    class FileHandlerTest {
        @Test
        void shouldSaveFileSuccessfully(@TempDir Path tempDir) throws IOException {
            when(policy.apply(any(),any())).thenReturn(new BigDecimal("4299.99"));
            testCart.addToCart(gaming, 2);
            testCart.addToCart(office, 3);
            testOrder = testCart.checkout(policy);
            //act
            FilesHandler.saveToFile(testOrder, tempDir);
            Path savedFile = tempDir.resolve(testOrder.fileName());
            String content = Files.readString(savedFile);
            //assert
            assertThat(savedFile).exists();
            assertThat(content).contains(testOrder.orderID().toString());
            assertThat(content).contains("123-123");
        }
    }

    @Test
    void shouldReturnInvoiceSuccessfully() {
        //arrange
        when(productManager.decreaseStock(gaming.getId(), 2)).thenReturn(2);
        when(productManager.decreaseStock(office.getId(), 3)).thenReturn(3);
        when(policy.apply(any(),any())).thenReturn(new BigDecimal("1999.99"));
        testCart.addToCart(gaming, 2);
        testCart.addToCart(office, 3);
        testOrder = testCart.checkout(policy);
        //act
        Invoice result = orderProcessor.processOrder(testOrder);
        String expectedDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        //assert
        assertAll(
                () -> assertThat(result.total()).isEqualByComparingTo(testOrder.total()),
                () -> assertThat(result.invoiceNumber()).startsWith("INV-" + expectedDate),
                () -> assertThat(result.issueDate()).isNotNull(),
                () -> assertThat(result.listOfProductsWithAdjustedQuantity()).hasSize(2),
                () -> assertThat(result.userInformation().getAccountNumber()).isEqualTo("123-123")
        );
    }

    @Test
    void shouldReturnInvoiceWithAdjustedTotal() {
        when(productManager.decreaseStock(gaming.getId(), 7)).thenReturn(5);
        when(policy.apply(any(),any())).thenReturn(new BigDecimal("19999.95"));

        testCart.addToCart(gaming, 7);
        testOrder = testCart.checkout(policy);
        //act
        Invoice result = orderProcessor.processOrder(testOrder);
        // assert
        assertThat(result.total()).isEqualByComparingTo(new BigDecimal("14285.96"));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenQtyStockIsNegative() {
        when(productManager.decreaseStock(gaming.getId(), 7)).thenReturn(-5);
        when(policy.apply(any(),any())).thenReturn(new BigDecimal("4299.99"));

        testCart.addToCart(gaming, 7);
        testOrder = testCart.checkout(policy);
        //act
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> orderProcessor.processOrder(testOrder));
        // assert
        assertThat("Shipped Quantity can't be negative").isEqualTo(ex.getMessage());
    }
}