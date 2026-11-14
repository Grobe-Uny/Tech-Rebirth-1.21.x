package com.grobe.techrebirth.energy;

public class EnergyConfig {


    private final int capacity;
    private final int maxReceive;
    private final int maxExtract;
    private final int initialEnergy;


    public EnergyConfig(int capacity, int maxReceive, int maxExtract, int initialEnergy) {
        if (capacity <= 0)
            throw new IllegalArgumentException("Energy capacity must be greater than 0!");

        if (maxReceive < 0)
            throw new IllegalArgumentException("Max receive cannot be negative!");

        if (maxExtract < 0)
            throw new IllegalArgumentException("Max extract cannot be negative!");

        if (initialEnergy < 0 || initialEnergy > capacity)
            throw new IllegalArgumentException("Initial energy must be between 0 and capacity!");

        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
        this.initialEnergy = initialEnergy;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getInitialEnergy() {
        return initialEnergy;
    }
    public int getMaxExtract() {
        return maxExtract;
    }
    public int getMaxReceive() {
        return maxReceive;
    }
}
