package com.grobe.techrebirth.block.custom.alloy;

import com.grobe.techrebirth.block.ModBlockEntities;
import com.grobe.techrebirth.block.custom.BaseMachineBlock;
import com.grobe.techrebirth.block.custom.entity.BaseMachineBlockEntity;
import com.grobe.techrebirth.block.custom.entity.alloy.AlloySmelterBlockEntity;
import com.grobe.techrebirth.util.MachineTier;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class AlloySmelterBlock extends BaseMachineBlock {

    public AlloySmelterBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties, MachineTier.BASIC);

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


    // Ensure inventory contents are dropped whenever the block is removed/replaced
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    protected void handleMachineSpecificDrops(BaseMachineBlockEntity machine, Level level, BlockPos pos) {
        if(machine instanceof AlloySmelterBlockEntity alloy){
            alloy.drops();
        }
    }
    
}
