package org.javashop.discount;

import lombok.RequiredArgsConstructor;
import org.javashop.domain.User.Account;
import org.javashop.interfaces.DiscountPolicy;
import org.javashop.models.Cart;
import org.javashop.models.Voucher;
import org.javashop.service.DiscountService;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Optional;

@RequiredArgsConstructor
public class VoucherDiscountPolicy implements DiscountPolicy {
    private final DiscountService discountService;

    @Override
    public BigDecimal apply(Cart cart, Account account) {
        Optional<Voucher> biggest = account.getVouchersList().stream()
                .max(Comparator.comparingInt(Voucher::getPercentage));
        if (biggest.isEmpty()) {
            return cart.getCartTotal(); // nothing to apply
        }
        Voucher voucher = biggest.get();
        account.removeVoucherFromAccount(voucher);
        return discountService.applyVoucher(cart.getCartTotal(), voucher);
    }
}
