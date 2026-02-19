package com.grobe.techrebirth.block.custom.furnace;

import com.grobe.techrebirth.block.custom.BaseMachineBlock;
import com.grobe.techrebirth.block.custom.entity.BaseMachineBlockEntity;
import com.grobe.techrebirth.block.custom.entity.furnace.ElectricFurnaceBlockEntity;
import com.grobe.techrebirth.block.ModBlockEntities;
import com.grobe.techrebirth.util.MachineTier;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;

public class ElectricFurnaceBlock extends BaseMachineBlock {

    public ElectricFurnaceBlock(Properties pProperties) {
        super(pProperties, MachineTier.BASIC);
    }
    public ElectricFurnaceBlock(Properties pProperties, MachineTier tier) {
        super(pProperties, tier);
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
        return createTicker(pLevel, pBlockEntityType, ModBlockEntities.ELECTRIC_FURNACE.get());
    }


    // When placed from an item, restore stored energy from the item's BlockEntity data component (safety net)
    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, net.minecraft.world.entity.LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
    }

    @Override
    protected void handleMachineSpecificDrops(BaseMachineBlockEntity machine, Level level, BlockPos pos){
        if(machine instanceof ElectricFurnaceBlockEntity furnace)
        {
            furnace.drops();
            if(level instanceof ServerLevel serverLevel)
            {
                int xp = furnace.drainPendingXpRandomRounded();
                if(xp > 0 ){
                    ExperienceOrb.award(serverLevel, Vec3.atCenterOf(pos), xp);
                }
            }
        }
    }

}
