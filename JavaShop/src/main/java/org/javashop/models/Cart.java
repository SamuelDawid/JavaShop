package org.javashop.models;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.javashop.Exceptions.EmptyCartException;
import org.javashop.Exceptions.UnavailableProducts;
import org.javashop.domain.User.Account;
import org.javashop.domain.resources.Electronics;
import org.javashop.enums.OrderStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@ToString
@Getter
public class Cart {
    private final List<Electronics> cart;
    private final Account customerAccount;

    public Cart(Account customerAccount) {
        this.cart = new LinkedList<>();
        this.customerAccount = customerAccount;
    }
    public boolean addToCart(@NonNull Electronics product,int howMany){
        if(!product.isAvailable()) throw new UnavailableProducts(product.getName());
        product.setQuantity(howMany);
       return cart.add(product);
    }
    public boolean removeFromCart(@NonNull Electronics product)
    {
       return cart.remove(product);
    }
    public BigDecimal getTotal(){
        return cart.stream().map(electronics -> (electronics.getPrice().multiply(BigDecimal.valueOf(electronics.getQuantity())))).reduce(BigDecimal.ZERO,BigDecimal::add).setScale(2,RoundingMode.HALF_UP);
    }
    public Order checkout(){
        if(cart.isEmpty()) throw new EmptyCartException();
        Order newOrder = new Order(customerAccount,UUID.randomUUID(),List.copyOf(cart), LocalDateTime.now(),getTotal(), OrderStatus.PENDING);
        cart.clear();
        return newOrder;
    }
}


