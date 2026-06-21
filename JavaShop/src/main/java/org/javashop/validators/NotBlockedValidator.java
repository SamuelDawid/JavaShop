package org.javashop.validators;

import lombok.RequiredArgsConstructor;
import org.javashop.domain.User.Account;
import org.javashop.interfaces.Validator;
import org.javashop.repo.AccountsRepository;

import java.math.BigDecimal;
import java.util.Optional;

@RequiredArgsConstructor
public class NotBlockedValidator implements Validator {
    private final AccountsRepository accountsRepository;
    @Override
    public Optional<String> validate(BigDecimal amount, String customerId) {
        Optional<Account> accountToValidate = accountsRepository.findAccount(customerId);
        if(accountToValidate.get().isBlocked()) return Optional.of("Account is blocked");
        return Optional.empty();
    }
}
