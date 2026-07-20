package org.javashop.web;

import org.javashop.dto.voucherDTO.CreateVoucherCommand;
import org.javashop.dto.voucherDTO.VoucherDto;
import org.javashop.models.Voucher;
import org.javashop.service.VoucherService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vouchers")
public class VoucherController {
    private final VoucherService voucherService;

    public VoucherController(VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    @GetMapping
    public List<VoucherDto> all() {
        return voucherService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<VoucherDto> byId(@PathVariable long id) {
        return voucherService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<VoucherDto> create(@RequestBody CreateVoucherCommand request) {
        Voucher newVoucher = new Voucher(request.voucherName(), request.expirationDate(), request.percentage());
        voucherService.addVoucher(newVoucher);
        return ResponseEntity.status(HttpStatus.CREATED).body(new VoucherDto(newVoucher.getId(), newVoucher.getVoucherName(), newVoucher.getExpirationDate(), newVoucher.getPercentage(), newVoucher.isUsed()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        if (voucherService.findById(id).isEmpty()) return ResponseEntity.notFound().build();
        voucherService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
