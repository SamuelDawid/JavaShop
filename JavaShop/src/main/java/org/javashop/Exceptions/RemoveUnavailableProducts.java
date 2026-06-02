package org.javashop.Exceptions;

public class RemoveUnavailableProducts extends RuntimeException {
    public RemoveUnavailableProducts(String product) {

        super("Unavailable Product: " + product);
    }
}
