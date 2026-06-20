package org.javashop.Exceptions;

public class VoucherNotFoundException extends RuntimeException {
    public VoucherNotFoundException() {
        super("Voucher was not found");
    }
}
