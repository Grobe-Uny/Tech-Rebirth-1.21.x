package com.grobe.techrebirth.block.custom.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BaseCableBlockEntity extends BlockEntity {
    public BaseCableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void markNetworkDirty() {
        // Default implementation does nothing, override in subclasses
    }
}
