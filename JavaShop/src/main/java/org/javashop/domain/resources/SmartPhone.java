package org.javashop.domain.resources;

import org.apache.commons.lang3.Validate;
import org.javashop.enums.Colour;
import org.javashop.enums.phone.ACCESSORIES;
import org.javashop.enums.phone.BATTERY;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Smart phone.
 */
public class SmartPhone extends Electronics {
    /**
     * The Battery.
     */
    BATTERY battery;
    /**
     * The Accessories.
     */
    List<ACCESSORIES> accessories;
    /**
     * The Colour.
     */
    Colour colour;

    /**
     * Instantiates a new Smartphone.
     *
     * @param id       the id
     * @param name     the name
     * @param price    the price
     * @param quantity the quantity
     * @param battery  the battery
     * @param colour   the colour
     */
    public SmartPhone(String id, String name, BigDecimal price, int quantity, BATTERY battery, Colour colour) {
        super(id, name, price, quantity);
        Validate.notBlank(battery.name(),"Please select valid battery type");
        Validate.notBlank(colour.name(),"Please select valid colour");
        this.battery = battery;
        this.accessories = new ArrayList<>(List.of(ACCESSORIES.No_Extras));
        this.colour = colour;
    }
}
