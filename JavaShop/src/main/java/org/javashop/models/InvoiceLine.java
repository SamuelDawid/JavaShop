package org.javashop.models;

import org.apache.commons.lang3.Validate;
import org.javashop.domain.resources.Electronics;

/**
 * The type Invoice line.
 */
public record InvoiceLine(Electronics product,
                          int quantityOrdered,
                          int quantityShipped) {
    /**
     * Instantiates a new Invoice line.
     *
     * @param product         the product
     * @param quantityOrdered the quantity ordered
     * @param quantityShipped the quantity shipped
     */
    public InvoiceLine {
        Validate.notNull(product, "Product can't be null");
        Validate.isTrue(quantityOrdered > 0, "Ordered Quantity can't be negative");
        Validate.isTrue(quantityShipped >= 0, "Shipped Quantity can't be negative");
    }

    @Override
    public String toString() {
        return product + " " +
                ", Ordered=" + quantityOrdered +
                ", Shipped=" + quantityShipped;

    }
}
