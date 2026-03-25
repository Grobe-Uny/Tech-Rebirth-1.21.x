package com.grobe.techrebirth.block.custom.infuser;

import com.grobe.techrebirth.block.custom.BaseMachineBlock;
import com.grobe.techrebirth.block.custom.entity.BaseMachineBlockEntity;
import com.grobe.techrebirth.block.custom.entity.infuser.FluidInfuserBlockEntity;
import com.grobe.techrebirth.util.MachineTier;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class FluidInfuserBlock extends BaseMachineBlock {
    public FluidInfuserBlock(Properties properties) {
        super(properties, MachineTier.BASIC);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FluidInfuserBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return super.getTicker(level, state, blockEntityType);
    }

    @Override
    protected void handleMachineSpecificDrops(BaseMachineBlockEntity machine, Level level, BlockPos pos) {

    }


    public static final MapCodec<FluidInfuserBlock> CODEC = simpleCodec(FluidInfuserBlock::new);

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
