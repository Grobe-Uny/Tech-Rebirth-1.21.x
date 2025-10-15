package com.grobe.techrebirth.block.custom.entity;

import com.grobe.techrebirth.block.ModBlockEntities;
import com.grobe.techrebirth.event.ModCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.EnergyStorage;
import org.jetbrains.annotations.Range;

public class EnergyCableBlockEntity extends BlockEntity {

    private class ModEnergy extends EnergyStorage {
        public ModEnergy(int capacity, int maxReceive, int maxExtract, int energy) {
            super(capacity, maxReceive, maxExtract, energy);
        }
        public void setEnergy(int energy) {
            this.energy = Math.min(energy, this.capacity);
            EnergyCableBlockEntity.this.setChanged();
        }
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            int received = super.receiveEnergy(maxReceive, simulate);
            if (!simulate && received > 0) EnergyCableBlockEntity.this.setChanged();
            return received;
        }
        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            int extracted = super.extractEnergy(maxExtract, simulate);
            if (!simulate && extracted > 0) EnergyCableBlockEntity.this.setChanged();
            return extracted;
        }
    }

    private final ModEnergy energyStorage = new ModEnergy(1000, 256, 256, 0);

    public EnergyCableBlockEntity(BlockPos pPos, BlockState pState) {
        super(ModBlockEntities.CABLE.get(), pPos, pState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, EnergyCableBlockEntity be) {
        if (level.isClientSide()) return;

        // Push energy to adjacent receivers
        if (be.energyStorage.getEnergyStored() > 0) {
            for (Direction dir : Direction.values()) {
                if (be.energyStorage.getEnergyStored() <= 0) break;
                BlockPos nPos = pos.relative(dir);
                BlockState nState = level.getBlockState(nPos);
                BlockEntity nBe = level.getBlockEntity(nPos);
                if (nBe == null) continue;
                /*EnergyStorage target = level.getCapability(ModCapabilities.ALL_MACHINES, nPos, nState, nBe, dir.getOpposite());
                if (target == null || !target.canReceive()) continue;
                int toSend = Math.min(256, be.energyStorage.getEnergyStored());
                if (toSend <= 0) continue;
                int received = target.receiveEnergy(toSend, false);
                if (received > 0) be.energyStorage.extractEnergy(received, false);*/
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        tag.putInt("energy", energyStorage.getEnergyStored());
        super.saveAdditional(tag, provider);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        this.energyStorage.setEnergy(tag.getInt("energy"));
    }

    public EnergyStorage getEnergyStorage() { return energyStorage; }
}