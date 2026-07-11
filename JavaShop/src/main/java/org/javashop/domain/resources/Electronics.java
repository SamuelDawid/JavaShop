package org.javashop.domain.resources;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.Validate;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "product_type")
@Getter
public class Electronics {
    @Id
    private String id;
    private String name;
    @Setter
    @Column(precision = 12, scale = 2)
    private BigDecimal price;
    @Setter
    private int quantity;

    protected Electronics() {
    }

    public Electronics(String id, String name, BigDecimal price, int quantity) {
        Validate.notBlank(name, "Name can not be blank");
        Validate.notBlank(id, "Id can not be blank");
        Validate.isTrue(price.signum() > 0, "Price must be more than 0");
        Validate.isTrue(quantity > 0, "Quantity must be more than 0");
        this.id = id;
        this.name = name;
        this.price = price.setScale(2, RoundingMode.HALF_UP);
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Product[" + id + "] " + name + " " + price + " zł " + "(" + quantity + ")";
    }

    public boolean isAvailable() {
        return this.quantity > 0;
    }
}
