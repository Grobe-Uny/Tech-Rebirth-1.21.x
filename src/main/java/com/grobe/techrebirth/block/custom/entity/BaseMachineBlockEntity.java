package com.grobe.techrebirth.block.custom.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
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

    protected BaseMachineBlockEntity(
            BlockEntityType<?> type,
            BlockPos pos,
            BlockState state,
            int inventorySize,
            int energyCapacity,
            int maxReceive,
            int maxExtract,
            int initialEnergy
    ) {
        super(type, pos, state);
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

    protected void setEnergyStored(int energy) {
        this.energyHandler.setEnergy(energy);
    }

    protected void fillEnergyToMax() {
        this.energyHandler.setEnergy(this.energyHandler.getMaxEnergyStored());
    }

    protected abstract boolean isItemValid(int slot, ItemStack stack);

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
}
