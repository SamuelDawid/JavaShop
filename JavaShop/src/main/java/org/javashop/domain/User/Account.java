package org.javashop.domain.User;

import lombok.*;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.Validate;
import org.javashop.enums.AccountType;
import org.javashop.models.Voucher;

import java.util.ArrayList;
import java.util.List;

@ToString
@Getter
public class Account {
    private final String accountNumber;
    private final String ownerName;
    @Setter
    private AccountType type;
    @Setter
    private int points;
    private List<Voucher> vouchersList;
    public Account(String accountNumber,String ownerName,AccountType type){
        Validate.notEmpty(accountNumber,"account number must be filled");
        Validate.notEmpty(ownerName,"Owner name and surname must be filled");
        EnumUtils.isValidEnum(AccountType.class,type.name());
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.type = type;
       this.points = 100;
       this.vouchersList = new ArrayList<>();
    }
    public void addVoucherToAccount(@NonNull Voucher voucher){
        vouchersList.add(voucher);
    }
    public void removeVoucherFromAccount(@NonNull Voucher voucher){
        vouchersList.remove(voucher);
    }
}
