package com.grobe.techrebirth.block.custom.generators.solar;

import com.grobe.techrebirth.block.ModBlockEntities;
import com.grobe.techrebirth.block.custom.entity.generators.solar.SolarGeneratorBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class SolarGeneratorBlock extends BaseEntityBlock {
    // Definiramo oblik: X1, Y1, Z1, X2, Y2, Z2 (u pixelima 0-16)
    // 0, 0, 0 je donji kut, 16, 4, 16 je suprotni kut (visina 4)
    protected static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 4, 16);

    public SolarGeneratorBlock(Properties properties){
        super(properties);
    }

    public static final MapCodec<SolarGeneratorBlock> CODEC = simpleCodec(SolarGeneratorBlock::new);

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec(){
        return CODEC;
    }
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context){
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState state){
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
        return new SolarGeneratorBlockEntity(pos, state);
    }
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type){
        if (level.isClientSide()) return null;

        return createTickerHelper(type, ModBlockEntities.SOLAR_GENERATOR.get(),
                (lvl, pos, st, be) -> be.tick(lvl, pos, st));
    }

}
