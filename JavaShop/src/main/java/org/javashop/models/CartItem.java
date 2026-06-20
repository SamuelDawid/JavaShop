package org.javashop.models;

import org.apache.commons.lang3.Validate;
import org.javashop.domain.resources.Electronics;

/**
 * The type Cart item.
 */
public record CartItem(Electronics product,int qty) {
    /**
     * Instantiates a new Cart item.
     *
     * @param product the product
     * @param qty     the qty
     */
    public CartItem{
        Validate.notNull(product,"Product can't be null");
        Validate.isTrue(qty > 0, " Quantity must be higher than 0");
    }

    @Override
    public String toString() {
        return "Product[" + product.getId() + "] " + product.getName() +" "+ product.getPrice() + " zł " +"("+qty+")" ;
    }
}
