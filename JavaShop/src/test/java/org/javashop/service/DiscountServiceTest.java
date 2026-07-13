package org.javashop.service;

import org.javashop.Exceptions.*;
import org.javashop.domain.User.Account;
import org.javashop.enums.AccountType;
import org.javashop.models.Voucher;
import org.javashop.repo.VoucherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DiscountServiceTest {

    @Mock
    VoucherRepository repository;
    @InjectMocks
    DiscountService discountService;
    //
    Account company, normal;
    BigDecimal basePrice, basePriceNotEven;
    Voucher testVoucher;

    @BeforeEach
    void setUp() {
        company = new Account("111-111", "Company", AccountType.COMPANY);
        normal = new Account("222-222", "Normal", AccountType.NORMAL);
        basePrice = new BigDecimal("125");
        basePriceNotEven = new BigDecimal("93.34");
        testVoucher = new Voucher("10PERC", LocalDate.of(2026, 6, 22), 10);

    }

    @Test
    void shouldApplyCompanyDiscount() {
        BigDecimal result = discountService.applyCompany(basePrice, company.getType());
        //assert
        assertThat(result).isEqualByComparingTo("116.25");
    }

    @Test
    void shouldThrowOnlyCompanyAccountDiscountException() {
        OnlyCompanyAccountDiscountException ex = assertThrows(OnlyCompanyAccountDiscountException.class, () -> discountService.applyCompany(basePrice, normal.getType()));
        assertThat(ex.getMessage()).isEqualTo("This discount is available only for Companies");
    }

//    @Test
//    void shouldApplyVoucher() {
//        when(repository.validateVoucher(testVoucher)).thenReturn(true);
//        BigDecimal result = discountService.applyVoucher(basePrice, testVoucher);
//        assertThat(result).isEqualByComparingTo("112.5");
//    }
//
//    @Test
//    void shouldThrowInvalidVoucherException() {
//        LocalDate fakeToday = LocalDate.of(2026, 5, 15);
//        try (MockedStatic<LocalDate> mockedDate = Mockito.mockStatic(LocalDate.class, Mockito.CALLS_REAL_METHODS)) {
//            mockedDate.when(LocalDate::now).thenReturn(fakeToday);
//            Voucher expired = new Voucher("expired", LocalDate.of(2026, 5, 20), 10);
//            assertThat(repository.validateVoucher(expired)).isFalse();
//            InvalidVoucherException ex = assertThrows(InvalidVoucherException.class, () -> discountService.applyVoucher(basePrice, expired));
//            assertThat(ex.getMessage()).isEqualTo("Invalid or expired voucher");
//        }
//    }
//
//    @Test
//    void shouldExchangePoints() {
//        Voucher expected = new Voucher("GENERATED10", LocalDate.now().plusDays(6), 10);
//        when(repository.generateVoucher(100)).thenReturn(expected);
//        Voucher result = discountService.exchangePoints(normal, 100);
//        assertThat(result.percentage()).isEqualTo(10);
//        assertThat(result.expirationDate()).isEqualTo(LocalDate.now().plusDays(6));
//        assertThat(result).isEqualTo(expected);
//    }
//
//    @Test
//    void shouldThrowNotAvailableForCompanyAccountsExceptionWhenAccountIsCompany() {
//        NotAvailableForCompanyAccountsException ex = assertThrows(NotAvailableForCompanyAccountsException.class, () -> discountService.exchangePoints(company, 100));
//        assertThat(ex.getMessage()).isEqualTo("This option is not available for Company type account");
//    }
//
//    @Test
//    void shouldAddVoucherToRepository() {
//        discountService.addVoucherToRepository(testVoucher);
//        verify(repository).addVoucher(testVoucher);
//    }
//
//    @Nested
//    class AccountTest {
//        Account testAcc = new Account("111-111", "Samuel K", AccountType.NORMAL);
//
//        @Test
//        void shouldAddVoucherToAccount() {
//            testAcc.addVoucherToAccount(testVoucher);
//
//            assertThat(testAcc.getVouchersList()).hasSize(1);
//            assertThat(testAcc.getVouchersList().getFirst()).isEqualTo(testVoucher);
//        }
//
//        @Test
//        void shouldRemoveVoucherFromAccount() {
//            testAcc.addVoucherToAccount(testVoucher);
//            assertThat(testAcc.getVouchersList()).hasSize(1);
//            testAcc.removeVoucherFromAccount(testVoucher);
//            assertThat(testAcc.getVouchersList()).isEmpty();
//        }
//
//        @Test
//        void shouldRemoveExpiredOrUsedVouchers() {
//            // Arrange
//            LocalDate creationDate = LocalDate.of(2026, 5, 1);
//            LocalDate expiryDate = LocalDate.of(2026, 5, 5);
//            LocalDate usedExpiry = LocalDate.of(2026, 5, 10);
//            LocalDate futureDate = LocalDate.of(2026, 5, 15);
//            //ACT
//            try (MockedStatic<LocalDate> mockedDate = Mockito.mockStatic(LocalDate.class, Mockito.CALLS_REAL_METHODS)) {
//                mockedDate.when(LocalDate::now).thenReturn(creationDate);
//
//                Voucher willExpire = new Voucher("willExpire", expiryDate, 10);
//                Voucher usedOne = new Voucher("used", usedExpiry, 10, true);
//                testAcc.addVoucherToAccount(willExpire);
//                testAcc.addVoucherToAccount(usedOne);
//                assertThat(testAcc.getVouchersList()).hasSize(2);
//
//                // change mock expiration day
//                mockedDate.when(LocalDate::now).thenReturn(futureDate);
//
//                testAcc.removeExpiredOrUsedVouchers();
//                assertThat(testAcc.getVouchersList()).isEmpty();
//            }
//        }
//    }
//
//    @Nested
//    class VoucherRepositoryTest {
//
//        VoucherRepository repository = new InMemoryVoucherRepository();
//
//        @Test
//        void shouldReturnTrueForValidVoucher() {
//            repository.addVoucher(testVoucher);
//            assertThat(repository.validateVoucher(testVoucher)).isTrue();
//        }
//
//        @Test
//        void shouldThrowVoucherNotFoundExceptionWhenValidating() {
//            VoucherNotFoundException ex = assertThrows(VoucherNotFoundException.class, () -> repository.validateVoucher(testVoucher));
//            assertThat(ex.getMessage()).isEqualTo("Voucher was not found");
//        }
//
//        @Test
//        void shouldDeleteVoucher() {
//            repository.addVoucher(testVoucher);
//            assertThat(repository.deleteVoucher(testVoucher)).isTrue();
//        }
//
//        @Test
//        void shouldThrowVoucherNotFoundExceptionWhenDeleting() {
//            Voucher notfound = new Voucher("NOTFOUND", LocalDate.now().plusDays(5), 10);
//            VoucherNotFoundException ex = assertThrows(VoucherNotFoundException.class, () -> repository.deleteVoucher(notfound));
//            assertThat(ex.getMessage()).isEqualTo("Voucher was not found");
//        }
//
//        @Test
//        void shouldGenerateVoucher() {
//            Voucher result = repository.generateVoucher(100);
//            assertAll(
//                    () -> assertThat(result).isNotNull(),
//                    () -> assertThat(result.voucherName()).isNotEmpty(),
//                    () -> assertThat(result.percentage()).isGreaterThan(0),
//                    () -> assertThat(result.isUsed()).isFalse()
//            );
//        }
//
//        @Test
//        void shouldThrowNotEnoughPointsException() {
//            NotEnoughPointsException ex = assertThrows(NotEnoughPointsException.class, () -> repository.generateVoucher(45));
//            assertThat(ex.getMessage()).isEqualTo("Not enough points");
//        }
//    }
}