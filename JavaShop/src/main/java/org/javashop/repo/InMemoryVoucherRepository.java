package org.javashop.repo;


import lombok.NonNull;
import org.javashop.Exceptions.NotEnoughPointsException;
import org.javashop.Exceptions.VoucherAlreadyExistsException;
import org.javashop.Exceptions.VoucherNotFoundException;
import org.javashop.models.Voucher;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.TemporalAmount;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


public class InMemoryVoucherRepository implements VoucherRepository{
    Map<Voucher,Boolean> listOfVouchers = new HashMap<>();
    private static final Map<Integer, Integer> POINTS_TO_DISCOUNT = Map.of(
            100, 10,
            150, 15,
            200, 20,
            250, 25
    );
    private static final TemporalAmount VOUCHER_MAX_DAYS = Period.ofDays(7);
    @Override
    public boolean validateVoucher(@NonNull Voucher voucher) {
        if(!listOfVouchers.containsKey(voucher)) throw new VoucherNotFoundException();

        return listOfVouchers.get(voucher);
    }
    public boolean addVoucher(@NonNull Voucher voucher){
        if(listOfVouchers.containsKey(voucher)) throw new VoucherAlreadyExistsException();
        return Boolean.TRUE.equals(listOfVouchers.putIfAbsent(voucher, true));
    }
    public boolean deleteVoucher(@NonNull Voucher voucher){
        if(!listOfVouchers.containsKey(voucher)) throw new VoucherNotFoundException();
        return listOfVouchers.remove(voucher) == null;
    }
    public Optional<Voucher> findVoucher(String voucherName){
      return listOfVouchers.keySet().stream().filter(voucher -> voucher.voucherName().equals(voucherName)).findAny();
    }
    public Voucher generateVoucher(int points){
        String voucher = Long.toHexString(UUID.randomUUID().getMostSignificantBits()).substring(0, 8).toUpperCase();
        Integer discount = POINTS_TO_DISCOUNT.get(points);
        if(discount == null) throw new NotEnoughPointsException("Not enough points");
        return new Voucher(voucher, LocalDate.now().plus(VOUCHER_MAX_DAYS),discount);

    }
}
