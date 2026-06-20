package org.javashop.domain.resources;

import org.apache.commons.lang3.Validate;
import org.javashop.enums.pc.CPU;
import org.javashop.enums.pc.GPU;
import org.javashop.enums.pc.RAM;

import java.math.BigDecimal;

/**
 * The type Computer.
 */
public class Computer extends Electronics {
    /**
     * The Cpu.
     */
    CPU cpu;
    /**
     * The Gpu.
     */
    GPU gpu;
    /**
     * The Ram.
     */
    RAM ram;

    /**
     * Instantiates a new Computer.
     *
     * @param id       the id
     * @param name     the name
     * @param price    the price
     * @param quantity the quantity
     * @param cpu      the cpu
     * @param gpu      the gpu
     * @param ram      the ram
     */
    public Computer(String id,String name, BigDecimal price, int quantity,CPU cpu,GPU gpu,RAM ram) {
        super(id, name, price, quantity);
        Validate.notBlank(cpu.name(),"Please select valid CPU");
        Validate.notBlank(gpu.name(),"Please select valid GPU");
        Validate.notBlank(ram.name(),"Please select valid RAM");
        this.cpu = cpu;
        this.gpu = gpu;
        this.ram = ram;

    }
}
