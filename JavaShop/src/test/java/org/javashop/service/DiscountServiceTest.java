package org.javashop.service;

import org.javashop.Exceptions.InvalidVoucherException;
import org.javashop.Exceptions.NotAvailableForCompanyAccountsException;
import org.javashop.Exceptions.OnlyCompanyAccountDiscountException;
import org.javashop.domain.User.Account;
import org.javashop.enums.AccountType;
import org.javashop.models.Voucher;
import org.javashop.repo.VoucherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DiscountServiceTest {

    @Mock
    VoucherRepository repository;
    @InjectMocks
    DiscountService discountService;
    //
    Account company,normal;
    BigDecimal basePrice,basePriceNotEven;
    Voucher testVoucher;
    @BeforeEach
    void setUp(){
         company = new Account("111-111","Company", AccountType.COMPANY);
         normal = new Account("222-222","Normal", AccountType.NORMAL);
         basePrice = new BigDecimal("125");
         basePriceNotEven = new BigDecimal("93.34");
         testVoucher = new Voucher("10PERC", LocalDate.of(2026,6,22),10);

    }
    @Test
    void shouldApplyCompanyDiscount(){
        BigDecimal result = discountService.applyCompany(basePrice,company.getType());
        //assert
        assertThat(result).isEqualByComparingTo("116.25");
    }
    @Test
    void shouldThrowOnlyCompanyAccountDiscountException(){
        OnlyCompanyAccountDiscountException ex = assertThrows(OnlyCompanyAccountDiscountException.class, () -> discountService.applyCompany(basePrice,normal.getType()));
        assertThat(ex.getMessage()).isEqualTo("This discount is available only for Companies");
    }
    @Test
    void shouldApplyVoucher(){
        when(repository.validateVoucher(testVoucher)).thenReturn(true);
        BigDecimal result = discountService.applyVoucher(basePrice,testVoucher);
        assertThat(result).isEqualByComparingTo("113");
    }
    @Test
    void shouldThrowInvalidVoucherException(){
        LocalDate fakeToday = LocalDate.of(2026,5,15);
        try (MockedStatic<LocalDate> mockedDate = Mockito.mockStatic(LocalDate.class, Mockito.CALLS_REAL_METHODS)) {
            mockedDate.when(LocalDate::now).thenReturn(fakeToday);
            Voucher expired = new Voucher("expired",LocalDate.of(2026,5,20),10);
            assertThat(repository.validateVoucher(expired)).isFalse();
            InvalidVoucherException ex = assertThrows(InvalidVoucherException.class,() -> discountService.applyVoucher(basePrice,expired));
            assertThat(ex.getMessage()).isEqualTo("Invalid or expired voucher");
        }
    }
    @Test
    void shouldExchangePoints(){
        Voucher expected = new Voucher("GENERATED10",LocalDate.now().plusDays(6),10);
        when(repository.generateVoucher(100)).thenReturn(expected);
        Voucher result = discountService.exchangePoints(normal,100);
        assertThat(result.percentage()).isEqualTo(10);
        assertThat(result.expirationDate()).isEqualTo(LocalDate.now().plusDays(6));
        assertThat(result).isEqualTo(expected);
    }
    @Test
    void shouldThrowNotAvailableForCompanyAccountsExceptionWhenAccountIsCompany(){
        NotAvailableForCompanyAccountsException ex = assertThrows(NotAvailableForCompanyAccountsException.class, () -> discountService.exchangePoints(company,100));
        assertThat(ex.getMessage()).isEqualTo("This option is not available for Company type account");
    }


}