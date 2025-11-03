package com.grobe.techrebirth.block.custom;

import com.grobe.techrebirth.block.custom.entity.BaseMachineBlockEntity;
import com.grobe.techrebirth.block.custom.entity.ElectricCentrifugeBlockEntity;
import com.grobe.techrebirth.util.MachineTier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class ElectricCentrifugeBlock extends BaseMachineBlock {

    public ElectricCentrifugeBlock(Properties properties, MachineTier tier) {
        super(properties, tier);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ElectricCentrifugeBlockEntity(pos, state);
    }

    @Override
    protected void handleMachineSpecificDrops(BaseMachineBlockEntity machine, Level level, BlockPos pos) {
        // No specific drops to handle for this machine
    }
}
