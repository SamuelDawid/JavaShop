package org.javashop.repo;


import lombok.NonNull;
import org.javashop.Exceptions.NotEnoughPointsException;
import org.javashop.Exceptions.VoucherAlreadyExistsException;
import org.javashop.Exceptions.VoucherNotFoundException;
import org.javashop.models.Voucher;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.TemporalAmount;
import java.util.*;


/**
 * In-memory implementation of {@link VoucherRepository} backed by a HashMap
 */
public class InMemoryVoucherRepository implements VoucherRepository{
    /**
     * The List of vouchers available.
     */
    Map<Voucher,Boolean> listOfVouchers = new HashMap<>();
    private static final Map<Integer, Integer> POINTS_TO_DISCOUNT = Map.of(
            100, 10,
            150, 15,
            200, 20,
            250, 25
    );
    private static final TemporalAmount VOUCHER_MAX_DAYS = Period.ofDays(7);

    /**
     * Validates whether a voucher is active and not expired
     * @param voucher the voucher to vailidate
     * @return true if voucher is valid,false if expired or already used.
     * @throws VoucherNotFoundException if the voucher does not exist in the repository
     */
    public boolean validateVoucher(@NonNull Voucher voucher) {
        if(!listOfVouchers.containsKey(voucher)) throw new VoucherNotFoundException();
        if(voucher.expirationDate().isBefore(LocalDate.now())) return false;
        return listOfVouchers.get(voucher);
    }

    /**
     * Adds voucher to the repository
     * @param voucher the voucher to add
     * @throws VoucherAlreadyExistsException if voucher already exists in the repository
     */
    public void addVoucher(@NonNull Voucher voucher){
        if(listOfVouchers.containsKey(voucher)) throw new VoucherAlreadyExistsException();
        listOfVouchers.putIfAbsent(voucher, true);
    }

    /**
     * Removes voucher from the repository
     * @param voucher the voucher to remove
     * @return true if voucher removed successfully, false if operation failed
     * @throws  VoucherNotFoundException if voucher was not found in the repository
     */
    public boolean deleteVoucher(@NonNull Voucher voucher){
        if(!listOfVouchers.containsKey(voucher)) throw new VoucherNotFoundException();
        return listOfVouchers.remove(voucher) != null;
    }

    /**
     * Return optional of voucher, returns Optional.empty() if not found in the repository
     * @param voucherName name of the voucher to find
     * @return optional of voucher if found, returns Optional.empty() if not found in the repository
     */
    public Optional<Voucher> findVoucher(String voucherName){
      return listOfVouchers.keySet().stream().filter(voucher -> voucher.voucherName().equals(voucherName)).findAny();
    }

    /**
     * Generates a new voucher based on the given points amount.
     * @param points the number of points to exchange.
     * @return a new voucher with discount and expiration date
     * @throws  NotEnoughPointsException if points do not match any discount tier
     */
    public Voucher generateVoucher(int points){
        String voucher = Long.toHexString(UUID.randomUUID().getMostSignificantBits()).substring(0, 8).toUpperCase();
        Integer discount = POINTS_TO_DISCOUNT.get(points);
        if(discount == null) throw new NotEnoughPointsException("Not enough points");
        return new Voucher(voucher, LocalDate.now().plus(VOUCHER_MAX_DAYS),discount);
    }

    /**
     * Returns an unmodifiable view of the points-to-discount mapping.
     * @return unmodifiable map where key is points required and value is discount percentage
     */
    public Map<Integer,Integer> getPointsToDiscount(){
        return Collections.unmodifiableMap(POINTS_TO_DISCOUNT);
    }
}
