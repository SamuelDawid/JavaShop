package org.javashop.interfaces;

import java.math.BigDecimal;
import java.util.Optional;

public interface Validator {
    Optional<String> validate(BigDecimal amount, String customerId);
}
