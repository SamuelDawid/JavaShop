package org.javashop.models;

import org.javashop.Exceptions.ProductNotFoundException;
import org.javashop.Exceptions.UnavailableProducts;
import org.javashop.domain.User.Account;
import org.javashop.domain.resources.Computer;
import org.javashop.domain.resources.SmartPhone;
import org.javashop.enums.AccountType;
import org.javashop.enums.Colour;
import org.javashop.enums.OrderStatus;
import org.javashop.enums.pc.CPU;
import org.javashop.enums.pc.GPU;
import org.javashop.enums.pc.RAM;
import org.javashop.enums.phone.BATTERY;
import org.javashop.repo.InMemoryProductRepository;
import org.javashop.service.ProductManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartTest {
    @Mock
    InMemoryProductRepository productRepository;
    @InjectMocks
    ProductManager productManager;
    Cart cart;
    //Computers
    Computer gaming,office;
    SmartPhone iPhone,samsung,budget;
    @BeforeEach
    void setUp() {
        // Computers
        gaming = new Computer("PC-1", "Gaming Beast", new BigDecimal("3999.99"), 5,
                CPU.AMD, GPU.NVIDIA, RAM.GB32);
        office = new Computer("PC-2", "Office Pro", new BigDecimal("1499.99"), 10,
                CPU.INTEL, GPU.INTEL, RAM.GB16);

        // SmartPhones
        iPhone = new SmartPhone("PH-1", "iPhone 15", new BigDecimal("4299.99"), 8,
                BATTERY.mAh_4000, Colour.BLACK);
        samsung = new SmartPhone("PH-2", "Samsung S24", new BigDecimal("3199.99"), 3,
                BATTERY.mAh_5000, Colour.GRAY);
        budget = new SmartPhone("PH-3", "Xiaomi 13", new BigDecimal("999.99"), 15,
                BATTERY.mAh_5000, Colour.GREEN);
        // Account
        Account testAccount = new Account("123-123","Test Subject", AccountType.NORMAL);
        cart = new Cart(testAccount);
    }

    @Test
    void shouldReturnTrueWhenAddingProducts(){
        //Asset + act
        assertThat(cart.addToCart(iPhone,1)).isTrue();
        assertThat(cart.getCart()).hasSize(1);
    }
    @Test
    void shouldReturnTrueWhenMaxQty(){
        //Asset + act
        assertThat(cart.addToCart(iPhone,8)).isTrue();
    }
    @Test
    void shouldThrowProductUnavailable(){
        iPhone.setQuantity(0);
        UnavailableProducts ex = assertThrows(UnavailableProducts.class, () ->  cart.addToCart(iPhone,5));
        assertThat(ex.getMessage()).isEqualTo("Unavailable Product: iPhone 15");
    }
    @Test
    void shouldRemoveFromCartSuccessfully(){
        cart.addToCart(iPhone,7);
        // Act + assert
        assertThat(cart.removeFromCart(iPhone)).isTrue();
    }
    @Test
    void shouldThrowWhenProductDoesNotExist(){
        ProductNotFoundException ex = assertThrows(ProductNotFoundException.class, () -> cart.removeFromCart(iPhone));
        assertThat("Product not found with ID: PH-1").isEqualTo(ex.getMessage());
    }
    @Test
    void shouldReturnPriceForOneItem(){
        cart.addToCart(iPhone,1);
        BigDecimal total = new BigDecimal("4299.99");
        Order result = cart.checkout();
        assertAll(
                () -> assertThat(result.productsList()).hasSize(1),
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.status()).isEqualTo(OrderStatus.PENDING),
                () -> assertThat(result.orderID()).isNotNull(),
                () -> assertThat(total).isEqualByComparingTo(result.total())
        );
    }
    @Test
    void shouldReturnTotalWithManyItems(){
        cart.addToCart(iPhone,2);
        cart.addToCart(samsung,1);
        cart.addToCart(budget,1);
        cart.addToCart(gaming,10);
        cart.addToCart(office,11);
        BigDecimal total = new BigDecimal("69299.75");
        assertThat(cart.getTotal()).isEqualByComparingTo(total);
    }
}

