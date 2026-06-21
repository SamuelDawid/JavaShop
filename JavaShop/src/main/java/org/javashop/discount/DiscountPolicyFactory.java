package org.javashop.discount;

import lombok.RequiredArgsConstructor;
import org.javashop.domain.User.Account;
import org.javashop.enums.AccountType;
import org.javashop.interfaces.DiscountPolicy;
import org.javashop.service.DiscountService;

import java.util.Map;
//Factory
@RequiredArgsConstructor
public class DiscountPolicyFactory {
    private final Map<AccountType, DiscountPolicy> policies;

    public static DiscountPolicyFactory create(DiscountService service) {
        return new DiscountPolicyFactory(Map.of(
                AccountType.COMPANY, new CompanyDiscountPolicy(service),
                AccountType.NORMAL,  new VoucherDiscountPolicy(service)
        ));
    }

    public DiscountPolicy forAccount(Account account) {
        return policies.get(account.getType());
    }
}
