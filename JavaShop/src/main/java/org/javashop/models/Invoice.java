package org.javashop.models;


import org.apache.commons.lang3.Validate;
import org.javashop.domain.User.Account;
import org.javashop.interfaces.Savable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * The type Invoice.
 */
public record Invoice(String invoiceNumber,
                      ZonedDateTime issueDate,
                      List<InvoiceLine> listOfProductsWithAdjustedQuantity,
                      BigDecimal total,
                      Account userInformation) implements Savable {
    /**
     * Instantiates a new Invoice.
     *
     * @param invoiceNumber                      the invoice number
     * @param issueDate                          the issue date
     * @param listOfProductsWithAdjustedQuantity the list of products with adjusted quantity
     * @param total                              the total
     * @param userInformation                    the user information
     */
    public Invoice{
     Validate.notEmpty(invoiceNumber,"Inv number must be filled");
     Validate.notNull(listOfProductsWithAdjustedQuantity,"product List must be present");
     Validate.notNull(total,"Invoice must have total amount");
     Validate.notNull(userInformation,"Account must be assigned to Invoice");
 }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm z");
        return "Invoice " + invoiceNumber + "\n" +
                "-issueDate:" + issueDate.format(formatter) + "\n"
                + listOfProductsWithAdjustedQuantity + "\n"+
                "-total: " + total +"\n"+
                "-user: " + userInformation;
    }

    @Override
    public String content() {
        return this.toString();
    }

    @Override
    public String fileName() {
        return "Invoice"+this.invoiceNumber+".txt";
    }
}
