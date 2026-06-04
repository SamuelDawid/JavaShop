package org.javashop.models;

import org.apache.commons.lang3.Validate;
import org.javashop.domain.User.Account;
import org.javashop.domain.resources.Electronics;
import org.javashop.enums.OrderStatus;
import org.javashop.interfaces.Savable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    public Order{
        Validate.notNull(account,"Account not known");
        Validate.notNull(orderID,"Must contain order ID");
        Validate.notNull(dateTime,"Must contain date and time");
        Validate.notEmpty(productsList,"Products list is empty");
        Validate.isTrue(total != null && total.signum() > 0,"Total must not be null nor negative");
    }

    @Override
    public String toString() {
        return "Order{"+orderID+"}" +
                "account=" + account + "\n"+
                ", productsList=" + productsList +"\n"+
                ", dateTime=" + dateTime +"\n"+
                ", total=" + total +"\n"+
                ", status=" + status +"\n";
    }

    @Override
    public String content() {
        return this.toString();
    }

    @Override
    public String fileName() {
        return "Order"+this.orderID+".txt";
    }
}
