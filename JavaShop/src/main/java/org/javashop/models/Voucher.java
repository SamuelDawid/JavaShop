package org.javashop.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.Validate;

import java.time.LocalDate;

@Entity
@Table(name = "vouchers")
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private long id;
    @Getter
    @Column(nullable = false)
    private String voucherName;
    @Future
    @Getter
    private LocalDate expirationDate;
    @Getter
    private int percentage;
    @Getter
    @Setter
    private boolean isUsed;

    public Voucher() {}

    @Builder
    public Voucher(String voucherName, LocalDate expirationDate, int percentage) {
        Validate.notEmpty(voucherName, "voucher can not be empty");
        Validate.notNull(expirationDate, "expiraton date can not be null");
        Validate.isTrue(expirationDate.isAfter(LocalDate.now()), "Voucher expiration date must be valid");
        Validate.isTrue(percentage > 0 && percentage <= 25, "Max voucher is 25%");
        this.voucherName = voucherName;
        this.expirationDate = expirationDate;
        this.percentage = percentage;
        this.isUsed = false;
    }

    @Override
    public String toString() {
        return "Voucher: " + voucherName + ", expirationDate: " + expirationDate + ", percentage:" + percentage;
    }
}
