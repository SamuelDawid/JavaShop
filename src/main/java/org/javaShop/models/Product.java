package org.javaShop.models;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.Validate;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@SuperBuilder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Product {
    @EqualsAndHashCode.Include
    private final String Id;
    private String name;
    @Setter
    private BigDecimal price;
    @Setter
    private int quantity;


    public Product(@NonNull String id, @NonNull String name, BigDecimal price, int quantity) {
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
        return "Product[" + Id + "] " + name + price + " zł " +"("+quantity+")";
    }
    public boolean isAvailable(){
        return this.quantity > 0;
    }
}
