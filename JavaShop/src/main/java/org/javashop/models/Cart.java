package org.javashop.models;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.javashop.Exceptions.EmptyCartException;
import org.javashop.Exceptions.InvalidQuantityException;
import org.javashop.Exceptions.ProductNotFoundException;
import org.javashop.Exceptions.UnavailableProducts;
import org.javashop.domain.User.Account;
import org.javashop.domain.resources.Electronics;
import org.javashop.enums.OrderStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * The type Cart.
 */
@ToString
@Getter
public class Cart {
    private final List<CartItem> cart;
    private final Account customerAccount;

    /**
     * Instantiates a new Cart.
     *
     * @param customerAccount the customer account
     */
    public Cart(Account customerAccount) {
        this.cart = new LinkedList<>();
        this.customerAccount = customerAccount;
    }

    /**
     * Add to cart boolean.
     *
     * @param product the product
     * @param howMany the how many
     * @return the boolean
     */
    public boolean addToCart(@NonNull Electronics product,int howMany){
        if(!product.isAvailable()) throw new UnavailableProducts(product.getName());
        if(howMany <= 0) throw new InvalidQuantityException();
       return cart.add(new CartItem(product,howMany));
    }

    /**
     * Remove from cart boolean.
     *
     * @param product the product
     * @return the boolean
     */
    public boolean removeFromCart(@NonNull Electronics product)
    {
        CartItem itemToFind = cart.stream().filter( cartItem -> cartItem.product().equals(product)).findAny().orElseThrow(() -> new ProductNotFoundException(product.getId()));
       return cart.remove(itemToFind);
    }

    /**
     * Get total big decimal.
     *
     * @return the big decimal
     */
    public BigDecimal getTotal(){
        return cart.stream().map( cartItem -> cartItem.product().getPrice()
                .multiply(BigDecimal.valueOf(cartItem.qty())))
                .reduce(BigDecimal.ZERO,BigDecimal::add).setScale(2,RoundingMode.HALF_UP);

    }

    /**
     * Checkout order.
     *
     * @return the order
     */
    public Order checkout(){
        if(cart.isEmpty()) throw new EmptyCartException();
        Order newOrder = new Order(customerAccount,UUID.randomUUID(),List.copyOf(cart), ZonedDateTime.now(ZoneId.systemDefault()),getTotal(), OrderStatus.PENDING);
        cart.clear();
        return newOrder;
    }
}


