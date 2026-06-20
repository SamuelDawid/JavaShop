package org.javashop.Exceptions;

import org.javashop.domain.resources.Electronics;

public class ProductAlreadyExists extends RuntimeException {
    public ProductAlreadyExists(Electronics product) {
        super("Product already exists!" + product.toString());
    }
}
