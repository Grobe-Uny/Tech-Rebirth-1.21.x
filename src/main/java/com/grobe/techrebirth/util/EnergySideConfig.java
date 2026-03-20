package com.grobe.techrebirth.util;

import net.minecraft.util.StringRepresentable;

public enum EnergySideConfig implements StringRepresentable {
    INPUT("input"),
    OUTPUT("output"),
    NONE("none");

    private final String name;

    EnergySideConfig(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    public EnergySideConfig next() {
        return values()[(this.ordinal() + 1) % values().length];
    }
}
