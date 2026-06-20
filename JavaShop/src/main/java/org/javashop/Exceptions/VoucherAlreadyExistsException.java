package org.javashop.Exceptions;

public class VoucherAlreadyExistsException extends RuntimeException {
    public VoucherAlreadyExistsException() {
        super("Voucher Already Exists");
    }
}
