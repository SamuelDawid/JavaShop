package org.javashop.models;

import lombok.Getter;
import lombok.NonNull;
import org.javashop.Exceptions.EmptyCartException;
import org.javashop.Exceptions.RemoveUnavailableProducts;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Getter
public class Cart {
    private List<Electronics> cart;

    public Cart() {
        this.cart = new LinkedList<>();
    }

    public boolean addToCart(@NonNull Electronics product){
       return cart.add(product);
    }
    public boolean removeFromCart(@NonNull Electronics product){
       return cart.remove(product);
    }
    public BigDecimal getTotal(){
//        BigDecimal result = BigDecimal.ZERO;
//        for(Electronics product : cart) result = result.add(product.getPrice());
        return cart.stream().map(Electronics::getPrice).reduce(BigDecimal.ZERO,BigDecimal::add).setScale(2,RoundingMode.HALF_UP);
    }
    public Order checkout(){
        if(cart.isEmpty()) throw new EmptyCartException();
        for (Electronics product : cart){
            if(!product.isAvailable()) {
                throw new RemoveUnavailableProducts(product.getName());
            }
        }
        Order newOrder = new Order(UUID.randomUUID(),List.copyOf(cart), LocalDateTime.now(),getTotal());
        cart.clear();
        return newOrder ;
    }
}


