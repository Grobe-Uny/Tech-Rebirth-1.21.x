package com.grobe.techrebirth.block.custom.alloy;

import com.grobe.techrebirth.block.ModBlockEntities;
import com.grobe.techrebirth.block.custom.entity.alloy.HardenedAlloySmelterBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class HardenedAlloySmelterBlock extends AlloySmelterBlock {

    public HardenedAlloySmelterBlock(Properties pProperties) {
        super(pProperties);
    }

//    @Override
//    public InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult) {
//        if (!pLevel.isClientSide()){
//            BlockEntity entity = pLevel.getBlockEntity(pPos);
//            System.out.println("DEBUG: BlockEntity found: " + entity);
//            if (entity instanceof HardenedAlloySmelterBlockEntity){
//                System.out.println("DEBUG: Opening menu...");
//                pPlayer.openMenu((HardenedAlloySmelterBlockEntity)entity, pPos);
//            } else {
//                throw new IllegalStateException("Our Container provider is missing!");
//            }
//        }
//        return InteractionResult.sidedSuccess(pLevel.isClientSide());
//    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new HardenedAlloySmelterBlockEntity(blockPos, blockState);
    }

    public static final MapCodec<HardenedAlloySmelterBlock> CODEC = simpleCodec(HardenedAlloySmelterBlock::new);

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return createTickerHelper(pBlockEntityType, ModBlockEntities.HARDENED_ALLOY_SMELTER.get(),
                (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1));
    }

}
