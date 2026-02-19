package com.grobe.techrebirth.util;

import net.minecraft.util.StringRepresentable;

public enum MetalType implements StringRepresentable {
    TIN("tin", 0xFFDEDEDE),
    NICKEL("nickel", 0xFFE3E6C3),
    INVAR("invar", 0xFFA1A48D),
    LEAD("lead", 0xFF4A526F),
    STEEL("steel", 0xFF565656),
    DIAMOND("diamond", 0xFF33EBCB),
    BLAZING_GOLD("blazing_gold", 0xFFFFD700);

    private final String name;
    private final int color;

    MetalType(String name, int color) {
        this.name = name;
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
