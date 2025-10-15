package com.grobe.techrebirth.block.custom.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.items.ItemStackHandler;

/**
 * A reusable base for powered machines that provides:
 * - An ItemStackHandler with onContentsChanged -> setChanged
 * - An EnergyStorage that marks the BE dirty on changes
 * Subclasses only need to implement isItemValid and their own menu/logic.
 */
public abstract class BaseMachineBlockEntity extends BlockEntity implements MenuProvider {

    protected final ItemStackHandler itemHandler;
    protected final DirtyEnergyStorage energyHandler;

    protected int progress = 0;
    protected int maxProgress = 100;

    protected ContainerData data;

    protected ContainerData createContainerData(int size) {
        return new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> progress;
                    case 1 -> maxProgress;
                    case 2 -> energyHandler.getEnergyStored();
                    case 3 -> energyHandler.getMaxEnergyStored();
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> progress = value;
                    case 1 -> maxProgress = value;
                    case 2 -> energyHandler.setEnergy(value);
                }
            }

            @Override
            public int getCount() {
                return size;
            }
        };
    }


    protected BaseMachineBlockEntity(
            BlockEntityType<?> type,
            BlockPos pos,
            BlockState state,
            int inventorySize,
            int energyCapacity,
            int maxReceive,
            int maxExtract,
            int initialEnergy,
            int dataSize
    ) {
        super(type, pos, state);
        this.data = createContainerData(dataSize);
        this.itemHandler = new ItemStackHandler(inventorySize) {
            @Override
            protected void onContentsChanged(int slot) {
                BaseMachineBlockEntity.this.setChanged();
            }
            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return BaseMachineBlockEntity.this.isItemValid(slot, stack);
            }
        };
        this.energyHandler = new DirtyEnergyStorage(energyCapacity, maxReceive, maxExtract, initialEnergy);
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    public EnergyStorage getEnergyStorage() {
        return energyHandler;
    }

    public int getProgress() {
        return progress;
    }

    public int getMaxProgress() {
        return maxProgress;
    }
    public float getProgressPercent() {
        return maxProgress > 0 ? (float) progress / maxProgress : 0;
    }

    protected void setEnergyStored(int energy) {
        this.energyHandler.setEnergy(energy);
    }

    protected void fillEnergyToMax() {
        this.energyHandler.setEnergy(this.energyHandler.getMaxEnergyStored());
    }

    protected abstract boolean isItemValid(int slot, ItemStack stack);
    public ContainerData getContainerData() {
        return data;
    }

    protected class DirtyEnergyStorage extends EnergyStorage {
        public DirtyEnergyStorage(int capacity, int maxReceive, int maxExtract, int energy) {
            super(capacity, maxReceive, maxExtract, energy);
        }
        public void setEnergy(int energy) {
            this.energy = Math.min(energy, this.capacity);
            BaseMachineBlockEntity.this.setChanged();
        }
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            int received = super.receiveEnergy(maxReceive, simulate);
            if (!simulate && received > 0) {
                BaseMachineBlockEntity.this.setChanged();
            }
            return received;
        }
        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            int extracted = super.extractEnergy(maxExtract, simulate);
            if (!simulate && extracted > 0) {
                BaseMachineBlockEntity.this.setChanged();
            }
            return extracted;
        }
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;
    }
}
