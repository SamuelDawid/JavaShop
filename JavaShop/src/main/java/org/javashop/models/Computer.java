package org.javashop.models;

import org.apache.commons.lang3.Validate;
import org.javashop.enums.pc.CPU;
import org.javashop.enums.pc.GPU;
import org.javashop.enums.pc.RAM;

import java.math.BigDecimal;

public class Computer extends Electronics {
        CPU cpu;
        GPU gpu;
        RAM ram;

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
