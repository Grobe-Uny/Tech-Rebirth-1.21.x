package com.grobe.techrebirth.block.custom.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.ItemStackHandler;

public abstract class BaseGeneratorBlockEntity extends BlockEntity implements MenuProvider {
    protected final ItemStackHandler itemHandler;
    protected final GeneratorEnergyStorage energyHandler;
    protected final ContainerData data;


    public BaseGeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int invSize, int dataSize) {
        super(type, pos, state);
        this.itemHandler = new ItemStackHandler(invSize) {
            @Override
            protected void onContentsChanged(int slot) { setChanged(); }
        };
        // Koristimo tvoju logiku za kapacitet iz tiera
        this.energyHandler = new GeneratorEnergyStorage(getCapacity(), getMaxExtract(), getMaxExtract(), 0);
        this.data = createContainerData(dataSize);
    }

    protected abstract int getCapacity();
    protected abstract int getMaxExtract();


    protected abstract ContainerData createContainerData(int size);

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;

        boolean isGenerating = false;
        if (canGenerate()) {
            int produced = generateEnergy();
            if (produced > 0) {
                energyHandler.receiveInternal(produced);
                isGenerating = true;
            }
        }

        // Slanje energije u susjedne blokove
        if (energyHandler.getEnergyStored() > 0) {
            pushEnergy();
        }

        // Vizualno ažuriranje (LIT property)
        if (state.hasProperty(BlockStateProperties.LIT) && state.getValue(BlockStateProperties.LIT) != isGenerating) {
            level.setBlock(pos, state.setValue(BlockStateProperties.LIT, isGenerating), 3);
        }
    }

    protected abstract boolean canGenerate();
    protected abstract int generateEnergy();

    private void pushEnergy() {
        for (Direction dir : Direction.values()) {
            var handler = level.getCapability(Capabilities.EnergyStorage.BLOCK, worldPosition.relative(dir), dir.getOpposite());
            if (handler != null && handler.canReceive()) {
                int toSend = energyHandler.extractEnergy(getMaxExtract(), true);
                int received = handler.receiveEnergy(toSend, false);
                energyHandler.extractEnergy(received, false);
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.put("inventory", itemHandler.serializeNBT(provider));
        tag.put("energy", energyHandler.serializeNBT(provider));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        itemHandler.deserializeNBT(provider, tag.getCompound("inventory"));
        energyHandler.deserializeNBT(provider, tag.get("energy"));
    }

    public IEnergyStorage getEnergyStorage() {
        return energyHandler;
    }

    // Interna klasa za energiju (slična tvojoj DirtyEnergyStorage)
    protected class GeneratorEnergyStorage extends EnergyStorage {
        public GeneratorEnergyStorage(int cap, int rec, int ext, int energy) { super(cap, rec, ext, energy); }
        public void setEnergy(int energy) { this.energy = energy; setChanged(); }
        public void receiveInternal(int amount) { this.energy = Math.min(this.capacity, this.energy + amount); setChanged(); }
        @Override public int extractEnergy(int maxExtract, boolean simulate) {
            int extracted = super.extractEnergy(maxExtract, simulate);
            if (!simulate && extracted > 0) setChanged();
            return extracted;
        }
    }
}
