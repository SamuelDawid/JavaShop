package org.javashop.config;

import org.javashop.domain.resources.Computer;
import org.javashop.domain.resources.SmartPhone;
import org.javashop.enums.Colour;
import org.javashop.enums.pc.CPU;
import org.javashop.enums.pc.GPU;
import org.javashop.enums.pc.RAM;
import org.javashop.enums.phone.BATTERY;
import org.javashop.models.Voucher;
import org.javashop.service.ProductManager;
import org.javashop.service.VoucherService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.TemporalAmount;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {
    private final ProductManager productManager;
    private final VoucherService voucherService;
    public DataSeeder(ProductManager productManager, VoucherService voucherService) {
        this.productManager = productManager;
        this.voucherService = voucherService;
    }

    @Override
    public void run(String... args){
//regionProducts
        productManager.addProducts(List.of(
                new Computer("PC-1", "Gaming Beast", new BigDecimal("3999.99"), 5,
                        CPU.AMD, GPU.NVIDIA, RAM.GB32),
                new Computer("PC-2", "Office Pro", new BigDecimal("1499.99"), 10,
                        CPU.INTEL, GPU.INTEL, RAM.GB16),
                new SmartPhone("PH-1", "iPhone 15", new BigDecimal("4299.99"), 8,
                        BATTERY.mAh_4000, Colour.BLACK),
                new SmartPhone("PH-2", "Samsung S24", new BigDecimal("3199.99"), 3,
                        BATTERY.mAh_5000, Colour.GRAY),
                new SmartPhone("PH-3", "Xiaomi 13", new BigDecimal("999.99"), 15,
                        BATTERY.mAh_5000, Colour.GREEN)));

        //endregion
        //regionVoucehrs
        TemporalAmount VOUCHER_MAX_DAYS = Period.ofDays(7);
            LocalDate expirationDate = LocalDate.now().plus(VOUCHER_MAX_DAYS);
            voucherService.addVoucher(new Voucher("XMAX", expirationDate,5));
            voucherService.addVoucher(new Voucher("SUMMER15",expirationDate ,15));
            voucherService.addVoucher(new Voucher("SUMMER5", expirationDate,5));
            voucherService.addVoucher(new Voucher("SUMMER10", expirationDate,10));
        //endregion
    }
}
