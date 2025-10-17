package com.grobe.techrebirth.util;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModDataComponents {

    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "techrebirth");

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> STORED_ENERGY =
            DATA_COMPONENTS.register("stored_energy", () ->
                    DataComponentType.<Integer>builder()
                            .persistent(Codec.INT)
                            .build());
}
// U ModDataComponents.java ili slično

//import com.grobe.techrebirth.TechRebirth;
//import com.grobe.techrebirth.util.MachineItemData;
//import net.minecraft.core.component.DataComponentType;
//import net.minecraft.resources.ResourceLocation;
//import net.neoforged.bus.api.IEventBus;
//
//public class ModDataComponents {
//    public static final DataComponentType<MachineItemData> MACHINE_DATA =
//            DataComponentType.<MachineItemData>builder()
//                    .persistent(MachineItemData.CODEC)
//                    .networkSynchronized(MachineItemData.STREAM_CODEC)
//                    .build();
//
//    public static void register(IEventBus bus) {
//        bus.addListener(ModDataComponents::registerComponents);
//    }
//
//    private static void registerComponents(RegisterDataComponentsEvent event) {
//        event.register(ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "machine_data"), MACHINE_DATA);
//    }
//}

