package org.javashop.domain.User;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.javashop.enums.AccountType;

@RequiredArgsConstructor
@Getter
public class Account {
    private final String accountNumber;
    private final String ownerName;
    @Setter
    private AccountType type = AccountType.NORMAL;
}
