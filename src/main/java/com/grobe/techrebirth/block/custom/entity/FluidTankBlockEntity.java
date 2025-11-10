package com.grobe.techrebirth.block.custom.entity;

import com.grobe.techrebirth.block.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

public class FluidTankBlockEntity extends BlockEntity {

    public static final int CAPACITY = 16000;
    private final FluidTank fluidTank = new FluidTank(CAPACITY) {
        @Override
        protected void onContentsChanged() {
            setChanged();
            getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    };

    public FluidTankBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FLUID_TANK.get(), pos, state);
    }

    public IFluidHandler getFluidHandler() {
        return fluidTank;
    }

    public FluidTank getFluidTank() {
        return fluidTank;
    }

    public FluidStack getFluid(){ return fluidTank.getFluid(); }


    // TICKER METODE - BITNO ZA BUDUĆE KABLOVE I ANIMACIJE
    public static void tick(Level level, BlockPos pos, BlockState state, FluidTankBlockEntity blockEntity) {
        if (level.isClientSide()) {
            blockEntity.clientTick(level, pos, state);
        } else {
            blockEntity.serverTick(level, pos, state);
        }
    }

    private void clientTick(Level level, BlockPos pos, BlockState state) {
        // OVDJE IDE ANIMACIJA I PARTIKLI
        // Npr: fluid ripple efekti, bubble animacije, itd.
    }

    private void serverTick(Level level, BlockPos pos, BlockState state) {
        // OVDJE IDE AUTOMATSKI FLUID TRANSFER ZA KABLOVE
        // Npr: provjera susjednih blokova, pumpanje fluida, itd.

        // Primjer za buduće kablove:
        // transferFluidToNeighbors();
    }

    // METODA ZA BUDUĆE KABLOVE
    private void transferFluidToNeighbors() {
        // TODO: Implementirati kada napraviš kablove
        // Provjeri susjedne blokove i transferaj fluid
    }



    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        // ISPRAVNO SPREMANJE FLUIDA
        CompoundTag fluidTag = new CompoundTag();
        fluidTank.writeToNBT(provider, fluidTag);
        tag.put("FluidTank", fluidTag);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        // ISPRAVNO UČITAVANJE FLUIDA
        if (tag.contains("FluidTank")) {
            fluidTank.readFromNBT(provider, tag.getCompound("FluidTank"));
        }
    }
//    @Override
//    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
//        tag.putInt("fluid", this.fluidTank.getFluidAmount());
//        super.saveAdditional(tag, provider);
//    }
//
//    @Override
//    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
//        super.loadAdditional(tag, provider);
//       fluidTank.readFromNBT(tag.getCompound("fluid"));
//        fluidTank.setCapacity(tag.getInt("fluid"));
//    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return saveWithoutMetadata(provider);
    }
}
