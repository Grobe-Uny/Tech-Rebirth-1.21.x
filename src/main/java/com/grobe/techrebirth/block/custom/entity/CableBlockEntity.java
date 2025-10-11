package com.grobe.techrebirth.block.custom.entity;

import com.grobe.techrebirth.block.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.capabilities.Capability;
import net.neoforged.neoforge.common.capabilities.Capabilities;
import net.neoforged.neoforge.energy.EnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CableBlockEntity extends BlockEntity {

    // The energy storage for the cable
    private final EnergyStorage energyStorage = new EnergyStorage(1000, 256, 256, 0);

    public CableBlockEntity(BlockPos pPos, BlockState pState) {
        super(ModBlockEntities.CABLE.get(), pPos, pState);
    }

    // Tick method, called every tick
    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, CableBlockEntity pBlockEntity) {
        if (pLevel.isClientSide()) {
            return;
        }

        // Distribute energy to adjacent blocks
        List<EnergyStorage> consumers = new ArrayList<>();
        for (Direction direction : Direction.values()) {
            BlockEntity adjacentBlockEntity = pLevel.getBlockEntity(pPos.relative(direction));
            if (adjacentBlockEntity != null) {
                adjacentBlockEntity.getCapability(Capabilities.ENERGY, direction.getOpposite()).ifPresent(energyStorage -> {
                    if (energyStorage.canReceive()) {
                        consumers.add(energyStorage);
                    }
                });
            }
        }

        if (!consumers.isEmpty()) {
            int energyToTransfer = Math.min(pBlockEntity.energyStorage.getEnergyStored(), 256 * consumers.size());
            int energyPerConsumer = energyToTransfer / consumers.size();
            for (EnergyStorage consumer : consumers) {
                int received = consumer.receiveEnergy(energyPerConsumer, false);
                pBlockEntity.energyStorage.extractEnergy(received, false);
            }
        }
    }

    // Save data to NBT
    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.putInt("energy", energyStorage.getEnergyStored());
        super.saveAdditional(pTag);
    }

    // Load data from NBT
    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        energyStorage.setEnergy(pTag.getInt("energy"));
    }

    // Expose capabilities
    @Override
    public <T> @NotNull T getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == Capabilities.ENERGY) {
            return (T) energyStorage;
        }
        return super.getCapability(cap, side);
    }

    public EnergyStorage getEnergyStorage() {
        return energyStorage;
    }
}