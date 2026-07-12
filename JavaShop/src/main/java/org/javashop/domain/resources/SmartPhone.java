package org.javashop.domain.resources;

import jakarta.persistence.*;
import org.apache.commons.lang3.Validate;
import org.javashop.enums.Colour;
import org.javashop.enums.phone.ACCESSORIES;
import org.javashop.enums.phone.BATTERY;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("PHONE")
public class SmartPhone extends Electronics {
    @Enumerated(EnumType.STRING)
    BATTERY battery;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    List<ACCESSORIES> accessories;

    @Enumerated(EnumType.STRING)
    Colour colour;
    public SmartPhone() {}
    public SmartPhone(String id, String name, BigDecimal price, int quantity, BATTERY battery, Colour colour) {
        super(id, name, price, quantity);
        Validate.notNull(battery.name(), "Please select valid battery type");
        Validate.notNull(colour.name(), "Please select valid colour");
        this.battery = battery;
        this.accessories = new ArrayList<>(List.of(ACCESSORIES.No_Extras));
        this.colour = colour;
    }
}
