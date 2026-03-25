package com.grobe.techrebirth.fluid;

import com.grobe.techrebirth.block.ModBlocks;
import com.grobe.techrebirth.item.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

import java.util.function.Supplier;

public abstract class LiquifiedCoalFluid extends BaseFlowingFluid {
    // Change constructor to accept a Supplier for the properties
    protected LiquifiedCoalFluid(Supplier<Properties> properties) {
        super(properties.get());
    }

    @Override
    public Fluid getFlowing() {
        return ModFluids.FLOWING_LIQUIFIED_COAL.get();
    }

    @Override
    public Fluid getSource() {
        return ModFluids.SOURCE_LIQUIFIED_COAL.get();
    }

    @Override
    public Item getBucket() {
        return ModItems.LIQUIFIED_COAL_BUCKET.get();
    }

    @Override
    protected net.minecraft.world.level.block.state.BlockState createLegacyBlock(FluidState state) {
        return ModBlocks.LIQUIFIED_COAL_BLOCK.get().defaultBlockState().setValue(net.minecraft.world.level.block.LiquidBlock.LEVEL, getLegacyLevel(state));
    }

    public static class Flowing extends LiquifiedCoalFluid {
        public Flowing(Supplier<Properties> properties) {
            super(properties);
            registerDefaultState(getStateDefinition().any().setValue(LEVEL, 7));
        }

        @Override
        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        @Override
        public int getAmount(FluidState state) {
            return state.getValue(LEVEL);
        }

        @Override
        public boolean isSource(FluidState state) {
            return false;
        }
    }

    public static class Source extends LiquifiedCoalFluid {
        public Source(Supplier<Properties> properties) {
            super(properties);
        }

        @Override
        public int getAmount(FluidState state) {
            return 8;
        }

        @Override
        public boolean isSource(FluidState state) {
            return true;
        }
    }
}
