package org.javashop.mapper;

import org.javashop.dto.voucherDTO.CreateVoucherRequest;
import org.javashop.dto.voucherDTO.VoucherResponse;
import org.javashop.models.Voucher;

public class VoucherMapper {
    private VoucherMapper() {
    }

    public static VoucherResponse toRespone(Voucher voucher) {
        return new VoucherResponse(voucher.getId(), voucher.getVoucherName(), voucher.getExpirationDate(), voucher.getPercentage(), voucher.isUsed());
    }

    public static Voucher toEntity(CreateVoucherRequest request) {
        return new Voucher(request.voucherName(), request.expirationDate(), request.percentage());
    }
}
