package org.javashop.interfaces;

import org.javashop.domain.User.Account;
import org.javashop.enums.AccountType;
import org.javashop.models.Voucher;

import java.math.BigDecimal;

public interface DiscountStrategy {
    BigDecimal applyCompany(BigDecimal basePrice, AccountType type);
    BigDecimal applyVoucher(BigDecimal vasePrice, Voucher voucher);
    Voucher exchangePoints(Account account, int points);
}
