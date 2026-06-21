package org.javashop.interfaces;

import java.math.BigDecimal;

@FunctionalInterface
public interface PaymentStrategy {
    void pay(BigDecimal amount, String customerId, String description);
}
