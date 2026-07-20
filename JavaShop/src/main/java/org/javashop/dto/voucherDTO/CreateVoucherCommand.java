package org.javashop.dto.voucherDTO;

import java.time.LocalDate;

public record CreateVoucherCommand(String voucherName,
                                   LocalDate expirationDate,
                                   int percentage) {
}
