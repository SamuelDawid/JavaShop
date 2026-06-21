package org.javashop.interfaces;
// Open/Closed:
import org.javashop.domain.User.Account;
import org.javashop.models.Cart;

import java.math.BigDecimal;
// Strategy pattern
public interface DiscountPolicy {
    BigDecimal apply(Cart cart, Account account);
}
