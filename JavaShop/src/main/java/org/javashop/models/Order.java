package org.javashop.models;

import org.apache.commons.lang3.Validate;
import org.javashop.domain.User.Account;
import org.javashop.enums.OrderStatus;
import org.javashop.interfaces.Savable;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * The type Order.
 */
public record Order(Account account,
                    UUID orderID,
                    List<CartItem> productsList,
                    ZonedDateTime dateTime,
                    BigDecimal subTotal,
                    BigDecimal total,
                    OrderStatus status) implements Savable {
    /**
     * Instantiates a new Order.
     *
     * @param account      the account
     * @param orderID      the order id
     * @param productsList the products list
     * @param dateTime     the date time
     * @param total        the total
     * @param status       the status
     */
    public Order {
        Validate.notNull(account, "Account not known");
        Validate.notNull(orderID, "Must contain order ID");
        Validate.notNull(dateTime, "Must contain date and time");
        Validate.notNull(subTotal, "Subtotal cannot be null");
        Validate.notNull(total, "total cannot be null");
        Validate.notEmpty(productsList, "Products list is empty");
        Validate.isTrue(total.signum() > 0, "negative Total");
        Validate.isTrue(total.compareTo(subTotal) <= 0, "Total cannot be greater than subtotal");
    }

    @Override
    public String toString() {
        return "Order{" + orderID + "}" +
                "account=" + account + "\n" +
                ", productsList=" + productsList + "\n" +
                ", dateTime=" + dateTime + "\n" +
                ", SubTotal=" + subTotal + "\n" +
                ", total=" + total + "\n" +
                ", status=" + status + "\n";
    }

    @Override
    public String content() {
        return this.toString();
    }

    @Override
    public String fileName() {
        return "Order" + this.orderID + ".txt";
    }
}
