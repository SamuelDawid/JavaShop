package org.javashop.models;

import org.apache.commons.lang3.Validate;

import java.time.LocalDate;

public record Voucher(String voucherName,
                      LocalDate expirationDate,
                      int percentage
                      ) {
    public Voucher{
        Validate.notEmpty(voucherName,"voucher can not be empty");
        Validate.isTrue(expirationDate.isAfter(LocalDate.now()),"Voucher expiration date must be valid");
        Validate.isTrue(percentage > 0 && percentage <= 25,"Max voucher is 25%");
    }

    @Override
    public String toString() {
        return "Voucher: " +voucherName + ", expirationDate: "+ expirationDate +", percentage:" + percentage ;
    }
}
