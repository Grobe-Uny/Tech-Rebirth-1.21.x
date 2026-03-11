package com.grobe.techrebirth.block.custom.purifier;

import com.grobe.techrebirth.block.ModBlockEntities;
import com.grobe.techrebirth.block.custom.BaseMachineBlock;
import com.grobe.techrebirth.block.custom.entity.BaseMachineBlockEntity;
import com.grobe.techrebirth.block.custom.entity.purifier.ElectricPurifierBlockEntity;
import com.grobe.techrebirth.util.MachineTier;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ElectricPurifierBlock extends BaseMachineBlock {

    public ElectricPurifierBlock(Properties pProperties){ super(pProperties, MachineTier.BASIC);}
    public ElectricPurifierBlock(Properties pProperties, MachineTier tier) {
        super(pProperties, tier);
    }


    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ElectricPurifierBlockEntity(pos, state);
    }
    public static final MapCodec<ElectricPurifierBlock> CODEC = simpleCodec(ElectricPurifierBlock::new);

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return createTicker(pLevel, pBlockEntityType, ModBlockEntities.ELECTRIC_PURIFIER.get());
    }


    // When placed from an item, restore stored energy from the item's BlockEntity data component (safety net)
    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
    }

    @Override
    protected void handleMachineSpecificDrops(BaseMachineBlockEntity machine, Level level, BlockPos pos) {

    }


}
