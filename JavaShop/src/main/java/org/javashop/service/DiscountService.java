package org.javashop.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.Validate;
import org.javashop.Exceptions.InvalidVoucherException;
import org.javashop.Exceptions.NoSuchDiscountException;
import org.javashop.Exceptions.NotAvailableForCompanyAccountsException;
import org.javashop.Exceptions.OnlyCompanyAccountDiscountException;
import org.javashop.domain.User.Account;
import org.javashop.enums.AccountType;
import org.javashop.interfaces.DiscountStrategy;
import org.javashop.models.Voucher;
import org.javashop.repo.VoucherRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Map;

@RequiredArgsConstructor
public class DiscountService implements DiscountStrategy {
    private final VoucherRepository voucherRepository;

    @Override
    public BigDecimal applyCompany(@NonNull BigDecimal basePrice,@NonNull AccountType type) {
        if(type != AccountType.COMPANY) throw new OnlyCompanyAccountDiscountException();
        BigDecimal discount = new BigDecimal("0.93");
        return basePrice.multiply(discount).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal applyVoucher(@NonNull BigDecimal basePrice,@NonNull Voucher voucher) {
        if(!voucherRepository.validateVoucher(voucher)) throw new InvalidVoucherException();
        BigDecimal discount = BigDecimal.valueOf(voucher.percentage());
        BigDecimal discountAmount = basePrice.divide(discount,RoundingMode.HALF_EVEN);
        return basePrice.subtract(discountAmount);
    }

    @Override
    public Voucher exchangePoints(@NonNull Account account, int points) {
        Validate.isTrue(points > 0, "points amount can not be negative");
        if(account.getType() == AccountType.COMPANY) throw new NotAvailableForCompanyAccountsException();
        return voucherRepository.generateVoucher(points);
    }
    public Map<Integer,Integer> getPointsToDiscount(){
        return Collections.unmodifiableMap(voucherRepository.getPointsToDiscount());
    }
    public int getMaxAvailableDiscount(int currentPoints){
        return voucherRepository.getPointsToDiscount().entrySet().stream()
                .filter(e -> e.getKey() <= currentPoints)
                .mapToInt(Map.Entry::getValue)
                .max()
                .orElse(0);
    }
    public int getPointsForDiscount(int discountPercent){
        return voucherRepository.getPointsToDiscount().entrySet().stream()
                .filter(e -> e.getValue().equals(discountPercent))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(() -> new NoSuchDiscountException("No such discount: " + discountPercent));
    }
    public void addVoucherToRepository(@NonNull Voucher voucher){
        voucherRepository.addVoucher(voucher);
    }
}
