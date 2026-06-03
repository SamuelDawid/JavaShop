package org.javashop.service;

import org.javashop.Exceptions.UnavailableProducts;
import org.javashop.domain.User.Account;
import org.javashop.domain.resources.Computer;
import org.javashop.domain.resources.Electronics;
import org.javashop.domain.resources.SmartPhone;
import org.javashop.enums.Colour;
import org.javashop.enums.OrderStatus;
import org.javashop.enums.pc.CPU;
import org.javashop.enums.pc.GPU;
import org.javashop.enums.pc.RAM;
import org.javashop.enums.phone.BATTERY;
import org.javashop.models.Cart;
import org.javashop.models.Invoice;
import org.javashop.models.Order;
import org.javashop.repo.InMemoryProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderProcessorTest {
    @Mock ProductManager productManager;
    @InjectMocks OrderProcessor orderProcessor;

    Order testOrder;
    Cart testCart;
    Computer gaming,office;
    @BeforeEach
    void setUp(){
         gaming = new Computer("PC-1", "Gaming Beast", new BigDecimal("3999.99"), 5,
                CPU.AMD, GPU.NVIDIA, RAM.GB32);
         office = new Computer("PC-2", "Office Pro", new BigDecimal("1499.99"), 10,
                CPU.INTEL, GPU.INTEL, RAM.GB16);
        Account testAccount = new Account("123-123","Test Subject");
        testCart = new Cart(testAccount);

    }
    @Test
    void shouldReturnInvoiceSuccessfully(){
        //arrange
        testCart.addToCart(gaming,2);
        testCart.addToCart(office,3);
        testOrder = testCart.checkout();
        //act
        Invoice result = orderProcessor.processOrder(testOrder);
        //assert
        assertAll(
                () ->  assertThat(testCart.getTotal()).isEqualByComparingTo(result.total()),
                () -> assertThat(result.invoiceNumber()).isEqualTo("INV-20260603-1"),
                () -> assertThat(result.issueDate()).isEqualTo(LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE)),
                () -> assertThat(result.listOfProductsWithAdjustedQuantity()).hasSize(2),
                () -> assertThat(result.userInformation().getAccountNumber()).isEqualTo("123-123")
        );
    }
    @Test
    void shouldReturnInvoiceWithAdjustedTotal(){
        when(productManager.decreaseStock(gaming.getId(),7)).thenReturn(5);
        testCart.addToCart(gaming,7);
        testOrder = testCart.checkout();
        //act
        Invoice result = orderProcessor.processOrder(testOrder);
        // assert
        assertThat(result.total()).isEqualByComparingTo(new BigDecimal("19999.95"));
    }
    @Test
    void shouldThrowIllegalArgumentExceptionWhenQtyStockIsNegative(){
        when(productManager.decreaseStock(gaming.getId(),7)).thenReturn(-5);
        testCart.addToCart(gaming,7);
        testOrder = testCart.checkout();
        //act
        IllegalArgumentException ex =assertThrows(IllegalArgumentException.class,() -> orderProcessor.processOrder(testOrder));
        // assert
        assertThat("Shipped Quantity can't be negative").isEqualTo(ex.getMessage());
    }
}