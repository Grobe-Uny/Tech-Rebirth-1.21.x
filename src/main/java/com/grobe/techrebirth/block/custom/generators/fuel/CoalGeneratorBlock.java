package com.grobe.techrebirth.block.custom.generators.fuel;

import com.grobe.techrebirth.block.ModBlockEntities;
import com.grobe.techrebirth.block.custom.entity.generators.fuel.CoalGeneratorBlockEntity;
import com.grobe.techrebirth.block.custom.entity.generators.solar.SolarGeneratorBlockEntity;
import com.grobe.techrebirth.block.custom.generator.GeneratorBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class CoalGeneratorBlock extends BaseEntityBlock {
    public static final MapCodec<CoalGeneratorBlock> CODEC = simpleCodec(CoalGeneratorBlock::new);

    public CoalGeneratorBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
        return new CoalGeneratorBlockEntity(pos, state);
    }
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type){
        if (level.isClientSide()) return null;

        return createTickerHelper(type, ModBlockEntities.COAL_GENERATOR.get(),
                (lvl, pos, st, be) -> be.tick(lvl, pos, st));
    }
}
