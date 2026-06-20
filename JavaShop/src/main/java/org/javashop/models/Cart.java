package org.javashop.models;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import org.javashop.Exceptions.EmptyCartException;
import org.javashop.Exceptions.InvalidQuantityException;
import org.javashop.Exceptions.ProductNotFoundException;
import org.javashop.Exceptions.UnavailableProducts;
import org.javashop.domain.User.Account;
import org.javashop.domain.resources.Electronics;
import org.javashop.enums.OrderStatus;
import org.javashop.service.DiscountService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a shopping cart for a customer account.
 * Supports adding/removing products, calculating totals, and checking out.
 */
@ToString
@Getter
public class Cart {
    private final List<CartItem> cart;
    private final Account customerAccount;
    private BigDecimal cartTotal = BigDecimal.ZERO;
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
     * Adds a product to the cart.
     *
     * @param product the product to add
     * @param howMany the quantity to add (must be greater than 0)
     * @throws UnavailableProducts  if the product is not available
     * @throws  InvalidQuantityException if the quantity is zero or negative
     */
    public void addToCart(@NonNull Electronics product,int howMany){
        if(!product.isAvailable()) throw new UnavailableProducts(product.getName());
        if(howMany <= 0) throw new InvalidQuantityException();
        cart.add(new CartItem(product,howMany));
        setCartTotal(calculateTotal());
    }

    /**
     * Removes a product from the cart.
     *
     * @param product the product
     * @return true if the product was successfully removed
     * @throws ProductNotFoundException if the product is not in the cart
     */
    public boolean removeFromCart(@NonNull Electronics product)
    {
        CartItem itemToFind = cart.stream().filter( cartItem -> cartItem.product().equals(product)).findAny().orElseThrow(() -> new ProductNotFoundException(product.getId()));
       setCartTotal(calculateTotal());
        return cart.remove(itemToFind);
    }

    /**
     * Calculates the total price of all items currently in the cart.
     *
     * @return the total price rounded to 2 decimal places
     */
    public BigDecimal calculateTotal(){
        return cart.stream().map( cartItem -> cartItem.product().getPrice()
                .multiply(BigDecimal.valueOf(cartItem.qty())))
                .reduce(BigDecimal.ZERO,BigDecimal::add).setScale(2,RoundingMode.HALF_UP);

    } /**
     * Sets the cart total to the given value.
     * Used to apply discounts to the cart total.
     *
     * @param total the new total to set
     * @return the updated cart total
     */

    public BigDecimal setCartTotal(BigDecimal total){
        this.cartTotal = total;
        return cartTotal;
    }
    /**
     * Creates an Order from the current cart and clears it.
     * Preserves the original subtotal and the potentially discounted total.
     *
     * @return the created order with PENDING status
     * @throws EmptyCartException if the cart is empty
     */
    public Order checkout(){
        if(cart.isEmpty()) throw new EmptyCartException();
        BigDecimal subTotal = calculateTotal();
        Order newOrder = new Order(
                customerAccount,
                UUID.randomUUID(),
                List.copyOf(cart),
                ZonedDateTime.now(ZoneId.systemDefault()),
                subTotal,
                cartTotal,
                OrderStatus.PENDING);
        cart.clear();
        return newOrder;
    }

}


