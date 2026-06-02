package org.javashop.service;

import org.javashop.Exceptions.ProductNotFoundException;
import org.javashop.enums.Colour;
import org.javashop.enums.pc.CPU;
import org.javashop.enums.pc.GPU;
import org.javashop.enums.pc.RAM;
import org.javashop.enums.phone.BATTERY;
import org.javashop.models.Computer;
import org.javashop.models.Electronics;
import org.javashop.models.SmartPhone;
import org.javashop.repo.InMemoryProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductManagerTest {
    @Mock
    InMemoryProductRepository productRepository;
    @InjectMocks
    ProductManager productManager;

    SmartPhone samsungGalaxy;
    Computer alienWereAurora;

    @BeforeEach
    void setUp(){
        samsungGalaxy = new SmartPhone("SGS24","Samsung galaxy S24",new BigDecimal(2000),5, BATTERY.mAh_5000, Colour.BLACK);
        alienWereAurora = new Computer("AWAR9","Alienwere Aurora R9",new BigDecimal(11_000),2, CPU.INTEL, GPU.NVIDIA, RAM.GB64);
    }
    @Test
    void shouldAddProductSuccessfully() {
        //Act
        productManager.addProduct(samsungGalaxy);
        productManager.addProduct(alienWereAurora);
        //Assert
        verify(productRepository).save(samsungGalaxy);
        verify(productRepository).save(alienWereAurora);

    }
    @Test
    void shouldThrowProductNotFoundException() {
        //Arrange
        when(productRepository.findById("DoNotExist")).thenReturn(Optional.empty());
        //Act + Assert
        ProductNotFoundException ex = assertThrows(ProductNotFoundException.class,() -> productManager.modify("DoNotExist",samsungGalaxy));
        assertEquals("Product not found with ID: DoNotExist",ex.getMessage());
    }
    @Test
    void ShouldReturnTrueWhenProductIsDeleted(){
        when(productRepository.delete("SGS24")).thenReturn(true);
        //Act + Assert
        assertThat(productManager.delete("SGS24")).isTrue();
    }
    @Nested
    class ProductTest{
        @Test
        void shouldThrowWhenNameIsBlank() {
            //Arrange
            String name = "";
            //Act + assert
            assertThrows(IllegalArgumentException.class,() -> new Electronics("id1",name,new BigDecimal(123),1));
        }

        @Test
        void shouldThrowWhenNameIsNull() {
            //Act + assert
            assertThrows(NullPointerException.class,() -> new Electronics("id1",null,new BigDecimal(123),1));
        }

        @Test
        void shouldThrowWhenIdIsBlank() {
            //Arrange
            String id = "";
            //Act + assert
            assertThrows(IllegalArgumentException.class,() -> new Electronics(id,"name",new BigDecimal(123),1));
        }

        @Test
        void shouldThrowWhenIdIsNull() {
            //Act + assert
                    assertThrows(NullPointerException.class,() -> new Electronics(null,"name",new BigDecimal(123),1));
        }

        @Test
        void shouldThrowWhenPriceIsZero() {
            //Arrange
            BigDecimal price = new BigDecimal(0);
            //Act + assert
            assertThrows(IllegalArgumentException.class,() -> new Electronics("id","name",price,1));
        }

        @Test
        void shouldThrowWhenPriceIsNegative() {
            //Arrange
            BigDecimal price = new BigDecimal(-10);
            //Act + assert
            assertThrows(IllegalArgumentException.class,() -> new Electronics("id","name",price,1));
        }

        @Test
        void shouldThrowWhenQuantityIsZero() {
            //Arrange
            int quant = 0;
            //Act + assert
            assertThrows(IllegalArgumentException.class,() -> new Electronics("id","name",new BigDecimal(123),quant));
        }

        @Test
        void shouldThrowWhenQuantityIsNegative() {
            //Arrange
            int quant = -10;
            //Act + assert
            assertThrows(IllegalArgumentException.class,() -> new Electronics("id","name",new BigDecimal(123),quant));
        }

        @Test
        void shouldCreateElectronicsSuccessfully() {
            Electronics e = assertDoesNotThrow(() -> new Electronics("id1","Samsung",new BigDecimal(123),1));
            assertAll(
                    () -> assertEquals("id1",e.getId()),
                    () -> assertEquals("Samsung",e.getName()),
                    () -> assertThat(new BigDecimal(123)).isEqualByComparingTo(e.getPrice()),
                    () -> assertEquals(1,e.getQuantity())
            );

        }
    }
    @Test
    void delete() {
    }
}