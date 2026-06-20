package org.javashop.Exceptions;

public class InvalidQuantityException extends RuntimeException {
    public InvalidQuantityException() {

        super("Quantity must be a positive number!");
    }
}
