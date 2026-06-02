package org.javashop.enums.PC;

public enum RAM {
    GB16("16 GB"),
    GB32("32 GB"),
    GB64("64 GB");

    public final String label;
    private RAM(String label){
        this.label = label;
    }
}
