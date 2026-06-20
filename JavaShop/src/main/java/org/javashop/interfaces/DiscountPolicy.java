package org.javashop.interfaces;

import org.javashop.domain.User.Account;
import org.javashop.models.Cart;

import java.math.BigDecimal;

public interface DiscountPolicy {
    BigDecimal apply(Cart cart, Account account);
}
