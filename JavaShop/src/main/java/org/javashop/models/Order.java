package org.javashop.models;

import org.apache.commons.lang3.Validate;
import org.javashop.domain.User.Account;
import org.javashop.domain.resources.Electronics;
import org.javashop.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record Order(Account account,
                    UUID orderID,
                    List<Electronics> productsList,
                    LocalDateTime dateTime,
                    BigDecimal total,
                    OrderStatus status) {
    public Order{
        Validate.notNull(account,"Account not known");
        Validate.notNull(orderID,"Must contain order ID");
        Validate.notNull(dateTime,"Must contain date and time");
        Validate.notEmpty(productsList,"Products list is empty");
        Validate.isTrue(total != null && total.signum() > 0,"Total must not be null nor negative");
    }
}
