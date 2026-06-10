package org.javashop.Exceptions;

public class UnavailableProducts extends RuntimeException {
    public UnavailableProducts(String product) {

        super("Unavailable Product: " + product);
    }
}
