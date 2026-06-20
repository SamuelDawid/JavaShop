package org.javashop.Exceptions;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String productID) {
        super("Product not found with ID: "+ productID);
    }
}
