package org.javashop.models;

import lombok.RequiredArgsConstructor;
import org.javashop.domain.User.Account;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class Invoice {
    private final String invoiceNumber;
    private final LocalDateTime issueDate;
    private final List<InvoiceLine> listOfProductsWithAdjustedQuantity;
    private final BigDecimal total;
    private final Account userInformation;
}
