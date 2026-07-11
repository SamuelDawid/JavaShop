package org.javashop.config;

import org.javashop.interfaces.PaymentStrategy;
import org.javashop.interfaces.Validator;
import org.javashop.repo.AccountsRepository;
import org.javashop.service.PaymentService;
import org.javashop.validators.AmountValidator;
import org.javashop.validators.NotBlockedValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
public class ShopConfig {
    @Bean public PaymentService paymentService(AccountsRepository accountRepository){
        Map<String, PaymentStrategy> methods = Map.of(
                "KARTA",   (amount, customerId, desc) -> System.out.println("Card charged: " + amount),
                "BLIK",    (amount, customerId, desc) -> System.out.println("BLIK paid: " + amount),
                "PRZELEW", (amount, customerId, desc) -> System.out.println("Transfer sent: " + amount)
        );
        List<Validator> validators = List.of(
                new AmountValidator(),
                new NotBlockedValidator(accountRepository)
        );
        return new PaymentService(methods,validators);
    }
}
