package org.javashop;


import org.javashop.discount.DiscountPolicyFactory;
import org.javashop.domain.User.Account;
import org.javashop.domain.resources.Computer;
import org.javashop.domain.resources.SmartPhone;
import org.javashop.enums.AccountType;
import org.javashop.enums.Colour;
import org.javashop.enums.pc.CPU;
import org.javashop.enums.pc.GPU;
import org.javashop.enums.pc.RAM;
import org.javashop.enums.phone.BATTERY;
import org.javashop.interfaces.PaymentStrategy;
import org.javashop.interfaces.Validator;
import org.javashop.models.Cart;
import org.javashop.repo.InMemoryAccountRepository;
import org.javashop.repo.InMemoryProductRepository;
import org.javashop.repo.InMemoryVoucherRepository;
import org.javashop.service.*;
import org.javashop.validators.AmountValidator;
import org.javashop.validators.NotBlockedValidator;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
@SpringBootApplication
public class App {
    public static void main(String[] args) throws InterruptedException {

        InMemoryProductRepository repository = new InMemoryProductRepository();
        InMemoryVoucherRepository voucherRepository = new InMemoryVoucherRepository();
        InMemoryAccountRepository accountRepository =  new InMemoryAccountRepository();
        ProductManager manager = new ProductManager(repository);
        OrderProcessor orderProcessor = new OrderProcessor(manager);
        DiscountService discountService = new DiscountService(voucherRepository);
        DiscountPolicyFactory discountPolicyFactory = DiscountPolicyFactory.create(discountService);
        Account account = new Account("123", "Samuel K", AccountType.NORMAL);
        accountRepository.addAccount(account);
        Cart cart = new Cart(account);

        Map<String, PaymentStrategy> methods = Map.of(
                "KARTA",   (amount, customerId, desc) -> System.out.println("Card charged: " + amount),
                "BLIK",    (amount, customerId, desc) -> System.out.println("BLIK paid: " + amount),
                "PRZELEW", (amount, customerId, desc) -> System.out.println("Transfer sent: " + amount)
        );

        List<Validator> validators = List.of(
                new AmountValidator(),
                new NotBlockedValidator(accountRepository)
        );

        PaymentService paymentService = new PaymentService(methods, validators);

        paymentService.onPayment(r -> System.out.println("[LOG] "       + r.method() + " " + r.amount() + " ok=" + r.successful()));
        paymentService.onPayment(r -> System.out.println("[ANALYTICS] " + r.amount()));
        paymentService.onPayment(r -> System.out.println("[AUDIT] "     + r.dateTime() + " " + r.message()));

        //regionProducts
        manager.addAllProducts(List.of(
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
        new ShopCLI(manager, cart, orderProcessor, discountService, account,discountPolicyFactory,paymentService).start();
    }
}
