package com.grobe.techrebirth.block.custom;

import com.grobe.techrebirth.block.ModBlockEntities;
import com.grobe.techrebirth.block.custom.entity.ElectricCrusherBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.component.DataComponents;

public class ElectricCrusherBlock extends BaseEntityBlock {
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.Plane.HORIZONTAL);

    public ElectricCrusherBlock(Properties props) {
        super(props);
        this.registerDefaultState(this.stateDefinition.any().setValue(LIT, false).setValue(FACING, Direction.NORTH));
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide()) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof ElectricCrusherBlockEntity be) {
                ((ServerPlayer) player).openMenu(be, pos);
            } else {
                throw new IllegalStateException("Our Container provider is missing!");
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ElectricCrusherBlockEntity(pos, state);
    }

    public static final MapCodec<ElectricCrusherBlock> CODEC = simpleCodec(ElectricCrusherBlock::new);

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return createTickerHelper(type, ModBlockEntities.ELECTRIC_CRUSHER.get(),
                (lvl, pos, st, be) -> be.tick(lvl, pos, st));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT, FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, net.minecraft.world.entity.LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ElectricCrusherBlockEntity crusher) {
                CustomData data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
                if (data != null) {
                    CompoundTag tag = data.copyTag();
                    if (tag.contains("electric_crusher.energy")) {
                        int energy = tag.getInt("electric_crusher.energy");
                        if (energy > 0) crusher.getEnergyStorage().receiveEnergy(energy, false);
                        crusher.setChanged();
                    }
                }
            }
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!level.isClientSide() && state.getBlock() != newState.getBlock()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ElectricCrusherBlockEntity crusher) {
                crusher.drops();
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public void spawnAfterBreak(BlockState state, ServerLevel level, BlockPos pos, ItemStack tool, boolean dropExperience) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof ElectricCrusherBlockEntity crusher) {
            ItemStack stack = new ItemStack(this.asItem());
            CompoundTag beTag = new CompoundTag();
            beTag.putInt("electric_crusher.energy", crusher.getEnergyStorage().getEnergyStored());
            stack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(beTag));
            popResource(level, pos, stack);
        } else {
            super.spawnAfterBreak(state, level, pos, tool, dropExperience);
        }
    }
}
