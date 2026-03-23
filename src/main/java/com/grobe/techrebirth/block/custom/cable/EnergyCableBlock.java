package com.grobe.techrebirth.block.custom.cable;

import com.grobe.techrebirth.block.ModBlockEntities;
import com.grobe.techrebirth.block.custom.entity.EnergyCableBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class EnergyCableBlock extends BaseCableBlock {

    public EnergyCableBlock(Properties properties) {
        super(properties);
    }

    public static final MapCodec<EnergyCableBlock> CODEC = Block.simpleCodec(EnergyCableBlock::new);

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected boolean shouldConnectTo(Level level, BlockPos pos, Direction direction) {
        BlockPos neighborPos = pos.relative(direction);

        // Connect to other energy cables
        if (level.getBlockState(neighborPos).getBlock() instanceof EnergyCableBlock) {
            return true;
        }

        // Connect to machines with energy capability
        return level.getCapability(net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage.BLOCK,
                neighborPos, direction.getOpposite()) != null;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EnergyCableBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) return null;
        return createTickerHelper(blockEntityType, ModBlockEntities.ENERGY_CABLE.get(), EnergyCableBlockEntity::tick);
    }
}
