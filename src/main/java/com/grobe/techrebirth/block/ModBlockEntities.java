package com.grobe.techrebirth.block;

import com.grobe.techrebirth.TechRebirth;
import com.grobe.techrebirth.block.custom.entity.*;
import com.grobe.techrebirth.block.custom.entity.alloy.AlloySmelterBlockEntity;
import com.grobe.techrebirth.block.custom.entity.bank.EnergyBankBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, TechRebirth.MODID);



    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ElectricFurnaceBlockEntity>> ELECTRIC_FURNACE = register(
            "electric_furnace", () -> BlockEntityType.Builder.of(ElectricFurnaceBlockEntity::new, ModBlocks.ELECTRIC_FURNACE.get())
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CreativeElectricFurnaceBlockEntity>> CREATIVE_ELECTRIC_FURNACE = register(
            "creative_electric_furnace", () -> BlockEntityType.Builder.of(CreativeElectricFurnaceBlockEntity::new,ModBlocks.CREATIVE_ELECTRIC_FURNACE.get())
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GeneratorBlockEntity>> GENERATOR = register(
            "generator", () -> BlockEntityType.Builder.of(GeneratorBlockEntity::new, ModBlocks.GENERATOR.get())
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnergyCableBlockEntity>> CABLE = register(
            "cable", () -> BlockEntityType.Builder.of(EnergyCableBlockEntity::new, ModBlocks.ENERGY_CABLE.get())
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnergyBankBlockEntity>> ENERGY_BANK = register(
            "energy_bank", () -> BlockEntityType.Builder.of(EnergyBankBlockEntity::new, ModBlocks.ENERGY_BANK.get())
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ElectricCrusherBlockEntity>> ELECTRIC_CRUSHER = register(
            "electric_crusher", () -> BlockEntityType.Builder.of(ElectricCrusherBlockEntity::new, ModBlocks.ELECTRIC_CRUSHER.get())
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AlloySmelterBlockEntity>> ALLOY_SMELTER = register(
            "alloy_smelter", () -> BlockEntityType.Builder.of(AlloySmelterBlockEntity::new, ModBlocks.ALLOY_SMELTER.get())
    );

    private static <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> register(String name, Supplier<BlockEntityType.Builder<T>> builder) {
        return BLOCK_ENTITIES.register(name,()-> builder.get().build(null));
    }

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
