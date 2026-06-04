package org.javashop.domain.User;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.javashop.enums.AccountType;

@ToString
@RequiredArgsConstructor
@Getter
public class Account {
    private final String accountNumber;
    private final String ownerName;
    @Setter
    private AccountType type = AccountType.NORMAL;
    @Setter
    private int points =100;
}
