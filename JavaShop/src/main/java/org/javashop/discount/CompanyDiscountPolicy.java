package org.javashop.discount;

import lombok.RequiredArgsConstructor;
import org.javashop.domain.User.Account;
import org.javashop.interfaces.DiscountPolicy;
import org.javashop.models.Cart;
import org.javashop.service.DiscountService;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class CompanyDiscountPolicy implements DiscountPolicy {
    private final DiscountService discountService;

    @Override
    public BigDecimal apply(Cart cart, Account account) {
        return discountService.applyCompany(cart.getCartTotal(), account.getType());
    }
}
