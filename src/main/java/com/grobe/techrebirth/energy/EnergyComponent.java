package com.grobe.techrebirth.energy;

import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class EnergyComponent {

    private final InternalEnergyStorage storage;

    public EnergyComponent(EnergyConfig config, Runnable onEnergyChanged){
        if (onEnergyChanged == null)
            throw new IllegalArgumentException("Energy change callback cannot be null!");
        if (config == null)
            throw new IllegalArgumentException("Energy config cannot be null!");
        this.storage = new InternalEnergyStorage(config.getCapacity(), config.getMaxReceive(), config.getMaxExtract(), config.getInitialEnergy(), onEnergyChanged);
    }

    public IEnergyStorage getEnergyStorage(){
        return storage;
    }
    public int generateEnergy(int amount){
        return storage.generateEnergy(amount);
    }
    public int spendEnergy(int amount){
        return storage.spendEnergy(amount);
    }
    public int getEnergyStored(){
        return storage.getEnergyStored();
    }
    public int getMaxEnergyStored(){
        return storage.getMaxEnergyStored();
    }
    private static class InternalEnergyStorage extends EnergyStorage {
        private final Runnable onEnergyChanged;

        public InternalEnergyStorage(int capacity, int maxReceive, int maxExtract, int initialEnergy, Runnable onEnergyChanged) {
            super(capacity, maxReceive, maxExtract, initialEnergy);
            this.onEnergyChanged = onEnergyChanged;
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            int received = super.receiveEnergy(maxReceive, simulate);

            if(!simulate && received > 0)
                onEnergyChanged.run();
            return received;
        }
        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            int extracted = super.extractEnergy(maxExtract, simulate);

            if (!simulate && extracted > 0) {
                onEnergyChanged.run();
            }

            return extracted;
        }

        public int generateEnergy(int amount) {
            if (amount <= 0) return 0;

            int generated = Math.min(amount, capacity - energy);

            if (generated > 0) {
                energy += generated;
                onEnergyChanged.run();
            }

            return generated;
        }

        public int spendEnergy(int amount) {
            if (amount <= 0) return 0;

            int spent = Math.min(amount, energy);

            if (spent > 0) {
                energy -= spent;
                onEnergyChanged.run();
            }

            return spent;
        }

    }
}
