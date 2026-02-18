package com.grobe.techrebirth.block.custom;

import com.grobe.techrebirth.block.ModBlockEntities;
import com.grobe.techrebirth.block.custom.entity.BaseMachineBlockEntity;
import com.grobe.techrebirth.block.custom.entity.crusher.ElectricCrusherBlockEntity;
import com.grobe.techrebirth.util.MachineTier;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.component.DataComponents;

public class ElectricCrusherBlock extends BaseMachineBlock {

    public ElectricCrusherBlock(Properties props) {
        super(props, MachineTier.BASIC);
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
        return createTickerHelper(type, ModBlockEntities.ELECTRIC_CRUSHER.get(),
                (lvl, pos, st, be) -> be.tick(lvl, pos, st));
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
                    if (tag.contains("electric_crusher_energy")) {
                        int energy = tag.getInt("electric_crusher_energy");
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
    protected void handleMachineSpecificDrops(BaseMachineBlockEntity machine, Level level, BlockPos pos) {
        if (machine instanceof ElectricCrusherBlockEntity crusher){
            crusher.drops();
        }
    }

    @Override
    public void spawnAfterBreak(BlockState state, ServerLevel level, BlockPos pos, ItemStack tool, boolean dropExperience) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof ElectricCrusherBlockEntity crusher) {
            ItemStack stack = new ItemStack(this.asItem());
            CompoundTag beTag = new CompoundTag();
            beTag.putInt("electric_crusher_energy", crusher.getEnergyStorage().getEnergyStored());
            stack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(beTag));
            popResource(level, pos, stack);
        } else {
            super.spawnAfterBreak(state, level, pos, tool, dropExperience);
        }
    }
}
