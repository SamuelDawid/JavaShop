package org.javashop.mapper;

import org.javashop.dto.voucherDTO.CreateVoucherCommand;
import org.javashop.dto.voucherDTO.VoucherDto;
import org.javashop.models.Voucher;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VoucherMapper {
    VoucherDto toRespone(Voucher voucher);
    Voucher toEntity(CreateVoucherCommand request);
}
