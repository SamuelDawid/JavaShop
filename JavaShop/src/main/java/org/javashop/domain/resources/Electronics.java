package org.javashop.domain.resources;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.Validate;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * The type Electronics.
 */
@Getter
@SuperBuilder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Electronics {
    @EqualsAndHashCode.Include
    private String Id;
    private String name;
    @Setter
    private BigDecimal price;
    @Setter
    private int quantity;

    /**
     * Instantiates a new Electronics.
     *
     * @param id       the id
     * @param name     the name
     * @param price    the price
     * @param quantity the quantity
     */
    public Electronics (String id,String name, BigDecimal price, int quantity) {
        Validate.notBlank(name,"Name can not be blank");
        Validate.notBlank(id,"Id can not be blank");
        Validate.isTrue(price.signum() > 0,"Price must be more than 0");
        Validate.isTrue(quantity > 0,"Quantity must be more than 0");
        Id = id;
        this.name = name;
        this.price = price.setScale(2, RoundingMode.HALF_UP);
        this.quantity = quantity;

    }

    @Override
    public String toString() {
        return "Product[" + Id + "] " + name +" "+ price + " zł " +"("+quantity+")" ;
    }

    /**
     * Is available boolean.
     *
     * @return the boolean
     */
    public boolean isAvailable(){
        return this.quantity > 0;
    }
}
