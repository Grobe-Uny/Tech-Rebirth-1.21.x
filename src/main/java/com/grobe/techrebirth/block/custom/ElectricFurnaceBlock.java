package com.grobe.techrebirth.block.custom;

import com.grobe.techrebirth.block.custom.entity.ElectricFurnaceBlockEntity;
import com.grobe.techrebirth.block.ModBlockEntities;
import com.grobe.techrebirth.util.ModDataComponents;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
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
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.core.component.DataComponents;

public class ElectricFurnaceBlock extends BaseEntityBlock {

    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.Plane.HORIZONTAL);
    public ElectricFurnaceBlock(Properties pProperties) {

        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LIT, false).setValue(FACING, Direction.NORTH));
    }

    @Override
    public InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult) {
        if (!pLevel.isClientSide()){
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            if (entity instanceof ElectricFurnaceBlockEntity){
                ((ServerPlayer) pPlayer).openMenu((ElectricFurnaceBlockEntity)entity, pPos);
            } else {
                throw new IllegalStateException("Our Container provider is missing!");
            }
        }
        return InteractionResult.sidedSuccess(pLevel.isClientSide());
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new ElectricFurnaceBlockEntity(blockPos, blockState);
    }

    public static final MapCodec<ElectricFurnaceBlock> CODEC = simpleCodec(ElectricFurnaceBlock::new);

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if(pLevel.isClientSide()){
            return null;
        }
        return createTickerHelper(pBlockEntityType, ModBlockEntities.ELECTRIC_FURNACE.get(),
                (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(LIT, FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    // When placed from an item, restore stored energy from the item's BlockEntity data component (safety net)
    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, net.minecraft.world.entity.LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ElectricFurnaceBlockEntity furnace) {
                // 🔹 Pokušaj dohvatiti spremljenu energiju iz DataComponent sustava
                Integer storedEnergy = stack.get(ModDataComponents.STORED_ENERGY);

                if (storedEnergy != null && storedEnergy > 0) {
                    furnace.getEnergyStorage().receiveEnergy(storedEnergy, false);
                    furnace.setChanged();
                }
                /*CustomData data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
                if (data != null) {
                    CompoundTag tag = data.copyTag();
                    if (tag.contains("electric_furnace.energy")) {
                        int energy = tag.getInt("electric_furnace.energy");
                        if (energy > 0) {
                            // Newly placed furnace starts at 0, so receiving is sufficient
                            furnace.getEnergyStorage().receiveEnergy(energy, false);
                        }
                        furnace.setChanged();
                    }
                }*/
            }
        }
    }

    // Ensure inventory contents are dropped whenever the block is removed/replaced
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!level.isClientSide() && state.getBlock() != newState.getBlock()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ElectricFurnaceBlockEntity furnace) {
                // 🔹 Spremi energiju u DataComponent itema
                int energy = furnace.getEnergyStorage().getEnergyStored();
                ItemStack stack = new ItemStack(state.getBlock().asItem());
                stack.set(ModDataComponents.STORED_ENERGY, energy);

                // 🔹 Dropaj item s pohranjenom energijom
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
                furnace.drops();
                if(level instanceof ServerLevel serverLevel){
                    int xp = furnace.drainPendingXpRandomRounded();
                    if(xp > 0){
                        ExperienceOrb.award(serverLevel, Vec3.atCenterOf(pos), xp);
                    }
                }
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    // When the block is broken by a player, drop a stack that preserves the BE's energy in BlockEntityTag
   /* @Override
    public void spawnAfterBreak(BlockState state, ServerLevel level, BlockPos pos, ItemStack tool, boolean dropExperience) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof ElectricFurnaceBlockEntity furnace) {
            // Inventory is dropped in onRemove; avoid double drops here
            // Create the block item with embedded BE NBT for energy
            ItemStack stack = new ItemStack(this.asItem());
            CompoundTag beTag = new CompoundTag();
            beTag.putInt("electric_furnace.energy", furnace.getEnergyStorage().getEnergyStored());
            // 1.21 uses data components for BE data on items
            stack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(beTag));
            popResource(level, pos, stack);
        } else {
            // Fallback to normal behavior if BE missing for some reason
            super.spawnAfterBreak(state, level, pos, tool, dropExperience);
            return;
        }
        // Do not call super to avoid default loot (which would drop another bare block)
    }*/
}