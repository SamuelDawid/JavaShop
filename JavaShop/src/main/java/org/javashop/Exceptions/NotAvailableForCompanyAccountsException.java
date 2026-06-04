package org.javashop.Exceptions;

public class NotAvailableForCompanyAccountsException extends RuntimeException {
    public NotAvailableForCompanyAccountsException() {
        super("This option is not available for Company type account");
    }
}
