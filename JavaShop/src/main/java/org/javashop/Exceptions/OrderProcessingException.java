package org.javashop.Exceptions;

public class OrderProcessingException extends RuntimeException {
    public OrderProcessingException() {
        super("Nothing could be shipped - all products out of stock");
    }
}
