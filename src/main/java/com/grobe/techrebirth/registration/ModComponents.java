package com.grobe.techrebirth.registration;

import com.grobe.techrebirth.TechRebirth;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public class ModComponents {
    // Registar za komponente
    public static final DeferredRegister<DataComponentType<?>> COMPONENTS =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, TechRebirth.MODID);

    // Naša komponenta za praćene pozicije (Lista BlockPos-ova)
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<BlockPos>>> TRACKED_POSITIONS =
            COMPONENTS.register("tracked_positions",
                    () -> DataComponentType.<List<BlockPos>>builder()
                            .persistent(BlockPos.CODEC.listOf()) // Omogućuje spremanje u world file
                            .cacheEncoding()
                            .build());
}