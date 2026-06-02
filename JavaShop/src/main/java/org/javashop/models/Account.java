package org.javashop.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Account {
    private final String accountNumber;
    private final String ownerName;

}
