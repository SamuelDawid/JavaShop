package org.javashop.Exceptions;

public class NoSuchDiscountException extends RuntimeException {
    public NoSuchDiscountException(String message) {
        super(message);
    }
}
