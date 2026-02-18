package com.grobe.techrebirth.block.custom.furnace;

import com.grobe.techrebirth.block.ModBlockEntities;
import com.grobe.techrebirth.block.custom.entity.furnace.ReinforcedElectricFurnaceBlockEntity;
import com.grobe.techrebirth.util.MachineTier;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class ReinforcedElectricFurnaceBlock extends ElectricFurnaceBlock {
    public ReinforcedElectricFurnaceBlock(Properties properties) {
        super(properties, MachineTier.REINFORCED);
    }

    public static final MapCodec<ReinforcedElectricFurnaceBlock> CODEC = simpleCodec(ReinforcedElectricFurnaceBlock::new);
    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ReinforcedElectricFurnaceBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return BaseEntityBlock.createTickerHelper(type, ModBlockEntities.REINFORCED_ELECTRIC_FURNACE.get(),
                (lvl, pos, st, be) -> be.tick(lvl, pos, st));
    }
}
