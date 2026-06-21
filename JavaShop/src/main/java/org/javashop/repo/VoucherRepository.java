package org.javashop.repo;

import org.javashop.models.Voucher;

import java.util.Map;
import java.util.Optional;
//Repository
public interface VoucherRepository {
    boolean validateVoucher(Voucher voucher);
    void addVoucher(Voucher voucher);
    boolean deleteVoucher( Voucher voucher);
    Optional<Voucher> findVoucher(String voucherName);
    Voucher generateVoucher(int points);
    Map<Integer,Integer> getPointsToDiscount();
}
