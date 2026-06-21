package org.javashop.models;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.javashop.Exceptions.EmptyCartException;
import org.javashop.Exceptions.InvalidQuantityException;
import org.javashop.Exceptions.ProductNotFoundException;
import org.javashop.Exceptions.UnavailableProducts;
import org.javashop.discount.DiscountPolicyFactory;
import org.javashop.domain.User.Account;
import org.javashop.domain.resources.Electronics;
import org.javashop.enums.OrderStatus;
import org.javashop.interfaces.DiscountPolicy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
//SRP
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
     * @throws UnavailableProducts      if the product is not available
     * @throws InvalidQuantityException if the quantity is zero or negative
     */
    public void addToCart(@NonNull Electronics product, int howMany) {
        if (!product.isAvailable()) throw new UnavailableProducts(product.getName());
        if (howMany <= 0) throw new InvalidQuantityException();
        cart.add(new CartItem(product, howMany));
        cartTotal = calculateTotal();
    }

    /**
     * Removes a product from the cart.
     *
     * @param product the product
     * @throws ProductNotFoundException if the product is not in the cart
     */
    public void removeFromCart(@NonNull Electronics product) {
        CartItem itemToFind = cart.stream().filter(cartItem -> cartItem.product().equals(product)).findAny().orElseThrow(() -> new ProductNotFoundException(product.getId()));
        cart.remove(itemToFind);
        cartTotal = calculateTotal();
    }

    /**
     * Calculates the total price of all items currently in the cart.
     *
     * @return the total price rounded to 2 decimal places
     */
    public BigDecimal calculateTotal() {
        return cart.stream().map(cartItem -> cartItem.product().getPrice()
                        .multiply(BigDecimal.valueOf(cartItem.qty())))
                .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

    }

    /**
     * Creates an Order from the current cart and clears it.
     * Preserves the original subtotal and the potentially discounted total.
     *
     * @return the created order with PENDING status
     * @throws EmptyCartException if the cart is empty
     */
    public Order checkout(DiscountPolicy policy) {
        if (cart.isEmpty()) throw new EmptyCartException();
        BigDecimal subTotal = calculateTotal();
        cartTotal = policy.apply(this,customerAccount);
        Order newOrder = new Order(
                customerAccount,
                UUID.randomUUID(),
                List.copyOf(cart),
                ZonedDateTime.now(ZoneId.systemDefault()),
                subTotal,
                cartTotal,
                OrderStatus.PENDING);
        cart.clear();
        cartTotal = BigDecimal.ZERO;
        return newOrder;
    }

}


