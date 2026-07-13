package org.javashop.dto.voucherDTO;

import java.time.LocalDate;

public record CreateVoucherRequest(String voucherName, LocalDate expirationDate, int percentage) {
}
