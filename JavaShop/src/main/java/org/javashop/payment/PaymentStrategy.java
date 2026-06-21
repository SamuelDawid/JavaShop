package org.javashop.payment;

import org.javashop.models.Invoice;

import java.math.BigDecimal;

@FunctionalInterface
public interface PaymentStrategy {
    void pay(BigDecimal amount, String customerId, String description);
}
