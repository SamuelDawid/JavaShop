package org.javashop.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.Validate;
import org.javashop.Exceptions.InvalidVoucherException;
import org.javashop.Exceptions.NotAvailableForCompanyAccountsException;
import org.javashop.Exceptions.OnlyCompanyAccountDiscountException;
import org.javashop.domain.User.Account;
import org.javashop.enums.AccountType;
import org.javashop.interfaces.DiscountStrategy;
import org.javashop.models.Voucher;
import org.javashop.repo.InMemoryVoucherRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
@RequiredArgsConstructor
public class DiscountService implements DiscountStrategy {
    private final InMemoryVoucherRepository voucherRepository;

    @Override
    public BigDecimal applyCompany(@NonNull BigDecimal basePrice,@NonNull AccountType type) {
        if(type != AccountType.COMPANY) throw new OnlyCompanyAccountDiscountException();
        BigDecimal discount = new BigDecimal("0.93");
        return basePrice.multiply(discount).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal applyVoucher(@NonNull BigDecimal basePrice,@NonNull Voucher voucher) {
        if(!voucherRepository.validateVoucher(voucher)) throw new InvalidVoucherException();
        BigDecimal discount = BigDecimal.valueOf(voucher.percentage()).divide(new BigDecimal(100),RoundingMode.UNNECESSARY);
        return basePrice.multiply(discount).setScale(2,RoundingMode.HALF_UP);
    }

    @Override
    public Voucher exchangePoints(@NonNull Account account, int points) {
        Validate.isTrue(points > 0, "points amount can not be negative");
        if(account.getType() == AccountType.COMPANY) throw new NotAvailableForCompanyAccountsException();
        return voucherRepository.generateVoucher(points);
    }
}
