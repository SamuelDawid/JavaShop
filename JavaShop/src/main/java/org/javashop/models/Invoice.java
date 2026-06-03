package org.javashop.models;


import org.apache.commons.lang3.Validate;
import org.javashop.domain.User.Account;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record Invoice(String invoiceNumber,
                      String issueDate,
                      List<InvoiceLine> listOfProductsWithAdjustedQuantity,
                      BigDecimal total,
                      Account userInformation) {
 public Invoice{
     Validate.notEmpty(invoiceNumber,"Inv number must be filled");
     Validate.notEmpty(issueDate,"Issue Date must be present");
     Validate.notNull(listOfProductsWithAdjustedQuantity,"product List must be present");
     Validate.notNull(total,"Invoice must have total amount");
     Validate.notNull(userInformation,"Account must be assigned to Invoice");
 }

    @Override
    public String toString() {
        return "Invoice " + invoiceNumber + "\n" +
                ", issueDate:" + issueDate + "\n"
                + listOfProductsWithAdjustedQuantity + "\n"+
                ", total: " + total +"\n"+
                ", user: " + userInformation
                ;
    }
}
