package com.grobe.techrebirth.util;

import net.minecraft.util.StringRepresentable;

public enum MetalType implements StringRepresentable {
    TIN("tin", 0xDEDEDE),
    NICKEL("nickel", 0xE3E6C3),
    INVAR("invar", 0xA1A48D),
    LEAD("lead", 0x4A526F),
    STEEL("steel", 0x565656),
    COPPER("copper", 0xFF6D00), // Vanilla copper color approximation
    IRON("iron", 0xD8D8D8),     // Vanilla iron color approximation
    GOLD("gold", 0xFDF55F),     // Vanilla gold color approximation
    DIAMOND("diamond", 0x33EBCB), // Vanilla diamond color approximation
    BLAZING_GOLD("blazing_gold", 0xFFD700); // Example custom metal

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
