package com.grobe.techrebirth.block.custom.alloy;

import com.grobe.techrebirth.block.ModBlockEntities;
import com.grobe.techrebirth.block.custom.BaseMachineBlock;
import com.grobe.techrebirth.block.custom.ElectricFurnaceBlock;
import com.grobe.techrebirth.block.custom.entity.BaseMachineBlockEntity;
import com.grobe.techrebirth.block.custom.entity.ElectricFurnaceBlockEntity;
import com.grobe.techrebirth.block.custom.entity.alloy.AlloySmelterBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class AlloySmelterBlock extends BaseMachineBlock {

    public AlloySmelterBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties, 50000);

    }

    @Override
    public InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult) {
        if (!pLevel.isClientSide()){
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            System.out.println("DEBUG: BlockEntity found: " + entity);
            if (entity instanceof AlloySmelterBlockEntity){
                System.out.println("DEBUG: Opening menu...");
                pPlayer.openMenu((AlloySmelterBlockEntity)entity, pPos);
            } else {
                throw new IllegalStateException("Our Container provider is missing!");
            }
        }
        return InteractionResult.sidedSuccess(pLevel.isClientSide());
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new AlloySmelterBlockEntity(blockPos, blockState);
    }

    public static final MapCodec<AlloySmelterBlock> CODEC = simpleCodec(AlloySmelterBlock::new);

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
        return createTickerHelper(pBlockEntityType, ModBlockEntities.ALLOY_SMELTER.get(),
                (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1));
    }


    // When placed from an item, restore stored energy from the item's BlockEntity data component (safety net)
//    @Override
//    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
//        super.setPlacedBy(level, pos, state, placer, stack);
//        if (!level.isClientSide()) {
//            BlockEntity be = level.getBlockEntity(pos);
//            if (be instanceof AlloySmelterBlockEntity alloySmelter) {
//                CustomData data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
//                if (data != null) {
//                    CompoundTag tag = data.copyTag();
//                    if (tag.contains("alloy_smelter.energy")) {
//                        int energy = tag.getInt("alloy_smelter.energy");
//                        if (energy > 0) {
//                            // Newly placed furnace starts at 0, so receiving is sufficient
//                            alloySmelter.getEnergyStorage().receiveEnergy(energy, false);
//                        }
//                        alloySmelter.setChanged();
//                    }
//                }
//            }
//        }
//    }

    // Ensure inventory contents are dropped whenever the block is removed/replaced
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
//        if (!level.isClientSide() && state.getBlock() != newState.getBlock()) {
//            BlockEntity be = level.getBlockEntity(pos);
//            if (be instanceof AlloySmelterBlockEntity alloySmelter) {
//                alloySmelter.drops();
//            }
//        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    protected void handleMachineSpecificDrops(BaseMachineBlockEntity machine, Level level, BlockPos pos) {
        if(machine instanceof AlloySmelterBlockEntity alloy){
            alloy.drops();
        }
    }

    // When the block is broken by a player, drop a stack that preserves the BE's energy in BlockEntityTag
//    @Override
//    public void spawnAfterBreak(BlockState state, ServerLevel level, BlockPos pos, ItemStack tool, boolean dropExperience) {
//        BlockEntity be = level.getBlockEntity(pos);
//        if (be instanceof AlloySmelterBlockEntity alloySmelter) {
//            // Inventory is dropped in onRemove; avoid double drops here
//            // Create the block item with embedded BE NBT for energy
//            ItemStack stack = new ItemStack(this.asItem());
//            CompoundTag beTag = new CompoundTag();
//            beTag.putInt("alloy_smelter.energy",alloySmelter.getEnergyStorage().getEnergyStored());
//            // 1.21 uses data components for BE data on items
//            stack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(beTag));
//            popResource(level, pos, stack);
//        } else {
//            // Fallback to normal behavior if BE missing for some reason
//            super.spawnAfterBreak(state, level, pos, tool, dropExperience);
//            return;
//        }
//        // Do not call super to avoid default loot (which would drop another bare block)
//    }
}
