package org.javashop.repo;

import org.javashop.models.Voucher;

public interface VoucherRepository {
    boolean validateVoucher(Voucher voucher);

}
