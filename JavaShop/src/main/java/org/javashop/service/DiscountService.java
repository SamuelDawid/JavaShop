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
import org.javashop.interfaces.DiscountCalculations;
import org.javashop.models.Voucher;
import org.javashop.repo.VoucherRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Map;

/**
 * The type Discount service.
 */
@RequiredArgsConstructor
public class DiscountService implements DiscountCalculations {
    private final VoucherRepository voucherRepository;
    private static final BigDecimal COMPANY_DISCOUNT_AMMOUNT = new BigDecimal("0.93");
    /**
     * Applies a 7% discount for company accounts.
     * Throws an exception if the account type is not COMPANY
     *
     * @param basePrice the original price before discount
     * @param type      the account type
     * @return the price after applying company discount
     * @throws OnlyCompanyAccountDiscountException if account type is not COMPANY
     */
    @Override
    public BigDecimal applyCompany(@NonNull BigDecimal basePrice, @NonNull AccountType type) {
        if (type != AccountType.COMPANY) throw new OnlyCompanyAccountDiscountException();
        return basePrice.multiply(COMPANY_DISCOUNT_AMMOUNT).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Applies voucher discount to the base price.
     * Validates the voucher before applying the discount
     *
     * @param basePrice the original price before discount
     * @param voucher   the voucher to apply
     * @return the price after applying the voucher discount
     * @throws InvalidVoucherException if the voucher is invalid or expired
     */
    @Override
    public BigDecimal applyVoucher(@NonNull BigDecimal basePrice, @NonNull Voucher voucher) {
        if (!voucherRepository.validateVoucher(voucher)) throw new InvalidVoucherException();
        BigDecimal discountFraction = BigDecimal.valueOf(voucher.percentage())
                .divide(new BigDecimal(100), 4, RoundingMode.HALF_UP);
        BigDecimal discountAmount = basePrice.multiply(discountFraction);
        return basePrice.subtract(discountAmount).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Exchanges loyalty points for a voucher.
     * Not available for company accounts
     * Validates the points amount.
     *
     * @param account the account exchanging points
     * @param points  the number of points to exchange
     * @return the generated voucher
     * @throws IllegalArgumentException                         if points are negative or zero
     * @throws NotAvailableForCompanyAccountsException          if the account type is COMPANY
     * @throws org.javashop.Exceptions.NotEnoughPointsException if the points amount do not match any discount
     */
    @Override
    public Voucher exchangePoints(@NonNull Account account, int points) {
        Validate.isTrue(points > 0, "points amount can not be negative");
        if (account.getType() == AccountType.COMPANY) throw new NotAvailableForCompanyAccountsException();
        return voucherRepository.generateVoucher(points);
    }

    /**
     * Returns an unmodifiable view of the points-to-discount mapping.
     *
     * @return Returns an unmodifiable map where key is points amount and value is discount percentage
     */
    public Map<Integer, Integer> getPointsToDiscount() {
        return Collections.unmodifiableMap(voucherRepository.getPointsToDiscount());
    }

    /**
     * Return the highest discount percentage available for the given points balance.
     * Returns 0 if no discount tier is reachable
     *
     * @param currentPoints the current points of the account
     * @return the maximum available discount percentage, or 0 if non-available
     */
    public int getMaxAvailableDiscount(int currentPoints) {
        return voucherRepository.getPointsToDiscount().entrySet().stream()
                .filter(e -> e.getKey() <= currentPoints)
                .mapToInt(Map.Entry::getValue)
                .max()
                .orElse(0);
    }

    /**
     * Return the number of points required to obtain a specific discount percentage.
     *
     * @param discountPercent the desired discount percentage
     * @return the points requited for the given discount
     * @throws NoSuchDiscountException if no tier exists for the given discount percentage
     */
    public int getPointsForDiscount(int discountPercent) {
        return voucherRepository.getPointsToDiscount().entrySet().stream()
                .filter(e -> e.getValue().equals(discountPercent))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(() -> new NoSuchDiscountException("No such discount: " + discountPercent));
    }

    /**
     * Add voucher to repository.
     *
     * @param voucher the voucher
     */
    public void addVoucherToRepository(@NonNull Voucher voucher) {
        voucherRepository.addVoucher(voucher);
    }
}
