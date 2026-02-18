package com.grobe.techrebirth.block.custom;

import com.grobe.techrebirth.block.ModBlockEntities;
import com.grobe.techrebirth.block.custom.entity.furnace.CreativeElectricFurnaceBlockEntity;
import com.grobe.techrebirth.block.custom.furnace.ElectricFurnaceBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import com.mojang.serialization.MapCodec;

public class CreativeElectricFurnaceBlock extends ElectricFurnaceBlock {

    public static final MapCodec<CreativeElectricFurnaceBlock> CODEC = simpleCodec(CreativeElectricFurnaceBlock::new);

    public CreativeElectricFurnaceBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CreativeElectricFurnaceBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return BaseEntityBlock.createTickerHelper(type, ModBlockEntities.CREATIVE_ELECTRIC_FURNACE.get(),
                (lvl, pos, st, be) -> be.tick(lvl, pos, st));
    }
}
