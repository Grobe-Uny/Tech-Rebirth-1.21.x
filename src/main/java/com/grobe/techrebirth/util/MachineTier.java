package com.grobe.techrebirth.util;

public enum MachineTier {
    BASIC(1.0f, 1.0f, 25000, 512,"basic"),
    HARDENED(1.15f, 0.9f, 50000,1024, "hardened"),
    REINFORCED(1.25f, 0.8f, 100000,2048, "reinforced");

    public final float speedMultiplier;
    public final float energyMultiplier;
    public final int energyCapacity;
    public final int energyInput;
    public final String name;

    MachineTier(float speedMultiplier, float energyMultiplier, int energyCapacity,int energyInput, String name){
        this.speedMultiplier = speedMultiplier;
        this.energyMultiplier = energyMultiplier;
        this.energyCapacity = energyCapacity;
        this.energyInput = energyInput;
        this.name = name;
    }

}
