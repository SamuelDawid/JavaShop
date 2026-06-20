package org.javashop.Exceptions;

public class InvalidVoucherException extends RuntimeException {
    public InvalidVoucherException() {
        super("Invalid or expired voucher");
    }
}
