package org.javashop.models;

import lombok.Builder;
import org.apache.commons.lang3.Validate;

import java.time.LocalDate;

@Builder
public record Voucher(String voucherName,
                      LocalDate expirationDate,
                      int percentage,
                      boolean isUsed
                      ) {
    public Voucher{
        Validate.notEmpty(voucherName,"voucher can not be empty");
        Validate.notNull(expirationDate, "expiraton date can not be null");
        Validate.isTrue(expirationDate.isAfter(LocalDate.now()),"Voucher expiration date must be valid");
        Validate.isTrue(percentage > 0 && percentage <= 25,"Max voucher is 25%");
    }
    public Voucher(String voucherName,
                   LocalDate expirationDate,
                   int percentage){
        this(voucherName,expirationDate,percentage,false);
    }

    @Override
    public String toString() {
        return "Voucher: " +voucherName + ", expirationDate: "+ expirationDate +", percentage:" + percentage ;
    }
}
