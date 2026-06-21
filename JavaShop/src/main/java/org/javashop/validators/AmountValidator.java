package org.javashop.validators;

import org.javashop.interfaces.Validator;

import java.math.BigDecimal;
import java.util.Optional;

public class AmountValidator implements Validator {
    @Override
    public Optional<String> validate(BigDecimal amount, String customerId) {
        if (amount.signum() <= 0) return Optional.of("Amount must be grater than 0");
        return Optional.empty();
    }
}
