package org.javashop.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResult(String customerId,
                            BigDecimal amount,
                            LocalDateTime dateTime,
                            String message,
                            boolean successful,
                            String method) {
    public PaymentResult markSuccessful() {
        return new PaymentResult(customerId, amount, dateTime, message, true, method);
    }
}


