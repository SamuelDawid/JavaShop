package org.javashop.web;

import org.javashop.dto.voucherDTO.CreateVoucherRequest;
import org.javashop.dto.voucherDTO.VoucherResponse;
import org.javashop.mapper.VoucherMapper;
import org.javashop.models.Voucher;
import org.javashop.service.VoucherService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/vouchers")
public class VoucherController {
    private final VoucherService voucherService;

    public VoucherController(VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    @GetMapping
    public List<VoucherResponse> all() {
        return voucherService.findAll().stream().map(VoucherMapper::toRespone).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<VoucherResponse> create(@RequestBody CreateVoucherRequest request) {
        Voucher newVoucher = new Voucher(request.voucherName(), request.expirationDate(), request.percentage());
        voucherService.addVoucher(newVoucher);
        return ResponseEntity.status(HttpStatus.CREATED).body(new VoucherResponse(newVoucher.getId(), newVoucher.getVoucherName(), newVoucher.getExpirationDate(), newVoucher.getPercentage(), newVoucher.isUsed()));
    }
}
