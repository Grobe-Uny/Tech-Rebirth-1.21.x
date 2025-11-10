package com.grobe.techrebirth.block.custom.entity;

import com.grobe.techrebirth.block.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidTank;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

public class FluidTankBlockEntity extends BlockEntity {
    private final FluidTank fluidTank = new FluidTank(16000) {
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

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("fluid", fluidTank.writeToNBT(new CompoundTag()));
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        fluidTank.readFromNBT(tag.getCompound("fluid"));
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }
}
