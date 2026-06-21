package org.javashop.repo;

import lombok.NonNull;
import org.javashop.domain.User.Account;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryAccountRepository implements AccountsRepository{
    private final Map<String,Account> accountMap = new HashMap<>();
    @Override
    public void addAccount(@NonNull Account account) {
         accountMap.put(account.getAccountNumber(),account);
    }

    @Override
    public boolean deleteAccount(Account account) {
        return false;
    }

    @Override
    public Optional<Account> findAccount(String accountNumber) {
        return Optional.ofNullable(accountMap.get(accountNumber));
    }

    @Override
    public boolean blockAccount(Account account) {
        account.setBlocked(true);
        return account.isBlocked();
    }
}
