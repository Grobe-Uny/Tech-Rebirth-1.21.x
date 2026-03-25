package com.grobe.techrebirth.fluid;

import com.grobe.techrebirth.TechRebirth;
import com.grobe.techrebirth.fluid.types.BaseFluidType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.joml.Vector3f;

public class ModFluidTypes {

    public static final ResourceLocation LIQUIFIED_COAL_STILL_RL = ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "block/fluid/liquified_coal_still");
    public static final ResourceLocation LIQUIFIED_COAL_FLOWING_RL = ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "block/fluid/liquified_coal_flow");
    public static final ResourceLocation LIQUIFIED_COAL_OVERLAY_RL = ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "block/fluid/liquified_coal_overlay");


    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, TechRebirth.MODID);

    public static final DeferredHolder<FluidType, FluidType> LIQUIFIED_COAL_TYPE = FLUID_TYPES.register("liquified_coal",
            () -> new BaseFluidType(LIQUIFIED_COAL_STILL_RL, LIQUIFIED_COAL_FLOWING_RL, LIQUIFIED_COAL_OVERLAY_RL,
                    0xFF2E2D2D, new Vector3f(46f / 255f, 45f / 255f, 45f / 255f),
                    FluidType.Properties.create()
                            .descriptionId("fluid_type.techrebirth.liquified_coal")
                            .density(2000)
                            .viscosity(3000)
                            .motionScale(0.007)
                            .canPushEntity(true)
                            .canSwim(false)
                            .lightLevel(5))
    );

    public static void register(IEventBus eventBus) {
        FLUID_TYPES.register(eventBus);
    }
}
