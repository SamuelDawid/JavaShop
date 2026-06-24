package org.javashop.repo;

import org.javashop.domain.User.Account;
import org.springframework.stereotype.Repository;

import java.util.Optional;
//Repository

public interface AccountsRepository {
    void addAccount(Account account);
    boolean deleteAccount(Account account);
    Optional<Account> findAccount(String accountNumber);
    boolean blockAccount(String accountNumber);
}
