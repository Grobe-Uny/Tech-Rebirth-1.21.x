package com.grobe.techrebirth.fluid;

import com.grobe.techrebirth.TechRebirth;
import com.grobe.techrebirth.block.ModBlocks;
import com.grobe.techrebirth.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModFluids {

    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, TechRebirth.MODID);

    // Change the generic type to BaseFlowingFluid
    public static final DeferredHolder<Fluid, BaseFlowingFluid> SOURCE_LIQUIFIED_COAL = FLUIDS.register("liquified_coal",
            () -> new LiquifiedCoalFluid.Source(ModFluids::getLiquifiedCoalProperties));

    public static final DeferredHolder<Fluid, BaseFlowingFluid> FLOWING_LIQUIFIED_COAL = FLUIDS.register("flowing_liquified_coal",
            () -> new LiquifiedCoalFluid.Flowing(ModFluids::getLiquifiedCoalProperties));

    public static BaseFlowingFluid.Properties getLiquifiedCoalProperties() {
        return new BaseFlowingFluid.Properties(
                ModFluidTypes.LIQUIFIED_COAL_TYPE,
                SOURCE_LIQUIFIED_COAL,
                FLOWING_LIQUIFIED_COAL
        ).block(ModBlocks.LIQUIFIED_COAL_BLOCK).bucket(ModItems.LIQUIFIED_COAL_BUCKET);
    }

    public static void register(IEventBus eventBus){
        FLUIDS.register(eventBus);
    }
}
