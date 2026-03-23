package com.grobe.techrebirth.fluid;

import com.grobe.techrebirth.block.ModBlocks;
import com.grobe.techrebirth.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

public class LiquifiedCoalFluid extends BaseFlowingFluid {
    protected LiquifiedCoalFluid(Properties properties) {
        super(properties);
    }

    @Override
    public  Fluid getFlowing() {
        return ModFluids.FLOWING_LIQUIFIED_COAL.get();
    }

    @Override
    public Fluid getSource() {
        return ModFluids.SOURCE_LIQUIFIED_COAL.get();
    }

    @Override
    public int getAmount(FluidState state) {
        return state.getValue(LEVEL);
    }

    @Override
    public boolean canBeReplacedWith(FluidState state, net.minecraft.world.level.BlockGetter level, BlockPos pos, Fluid fluid, Direction direction) {
        return direction == net.minecraft.core.Direction.DOWN || !fluid.is(FluidTags.WATER);
    }

    @Override
    public Item getBucket() {
        return ModItems.LIQUIFIED_COAL_BUCKET.get();
    }

    @Override
    protected BlockState createLegacyBlock(net.minecraft.world.level.material.FluidState state) {
        return ModBlocks.LIQUIFIED_COAL_BLOCK.get().defaultBlockState().setValue(net.minecraft.world.level.block.LiquidBlock.LEVEL, getLegacyLevel(state));
    }

    @Override
    public boolean isSource(FluidState state) {
        return false;
    }

    public static class Flowing extends LiquifiedCoalFluid {
        public Flowing(Properties properties) {
            super(properties);
            registerDefaultState(getStateDefinition().any().setValue(LEVEL, 7));
        }

        @Override
        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }


}
}
