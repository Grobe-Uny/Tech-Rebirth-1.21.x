package com.grobe.techrebirth.block.custom.furnace;

import com.grobe.techrebirth.block.ModBlockEntities;
import com.grobe.techrebirth.block.custom.entity.furnace.HardenedElectricFurnaceBlockEntity;
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


public class HardenedElectricFurnaceBlock extends ElectricFurnaceBlock{
    public HardenedElectricFurnaceBlock(Properties pProperties) {
        super(pProperties, MachineTier.HARDENED);
    }
    public static final MapCodec<HardenedElectricFurnaceBlock> CODEC = simpleCodec(HardenedElectricFurnaceBlock::new);


    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HardenedElectricFurnaceBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTicker(level, type, ModBlockEntities.HARDENED_ELECTRIC_FURNACE.get());
    }

}
