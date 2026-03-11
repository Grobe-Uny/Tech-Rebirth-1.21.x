package com.grobe.techrebirth.block;

import com.grobe.techrebirth.TechRebirth;
import com.grobe.techrebirth.block.custom.entity.*;
import com.grobe.techrebirth.block.custom.entity.alloy.AlloySmelterBlockEntity;
import com.grobe.techrebirth.block.custom.entity.alloy.HardenedAlloySmelterBlockEntity;
import com.grobe.techrebirth.block.custom.entity.bank.EnergyBankBlockEntity;
import com.grobe.techrebirth.block.custom.entity.crusher.ElectricCrusherBlockEntity;
import com.grobe.techrebirth.block.custom.entity.furnace.CreativeElectricFurnaceBlockEntity;
import com.grobe.techrebirth.block.custom.entity.furnace.ElectricFurnaceBlockEntity;
import com.grobe.techrebirth.block.custom.entity.furnace.HardenedElectricFurnaceBlockEntity;
import com.grobe.techrebirth.block.custom.entity.furnace.ReinforcedElectricFurnaceBlockEntity;
import com.grobe.techrebirth.block.custom.entity.purifier.ElectricPurifierBlockEntity;
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
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<HardenedElectricFurnaceBlockEntity>> HARDENED_ELECTRIC_FURNACE = register(
            "hardened_electric_furnace", () -> BlockEntityType.Builder.of(HardenedElectricFurnaceBlockEntity::new, ModBlocks.HARDENED_ELECTRIC_FURNACE.get())
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ReinforcedElectricFurnaceBlockEntity>> REINFORCED_ELECTRIC_FURNACE = register(
            "reinforced_electric_furnace", () -> BlockEntityType.Builder.of(ReinforcedElectricFurnaceBlockEntity::new, ModBlocks.REINFORCED_ELECTRIC_FURNACE.get())
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CreativeElectricFurnaceBlockEntity>> CREATIVE_ELECTRIC_FURNACE = register(
            "creative_electric_furnace", () -> BlockEntityType.Builder.of(CreativeElectricFurnaceBlockEntity::new,ModBlocks.CREATIVE_ELECTRIC_FURNACE.get())
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GeneratorBlockEntity>> GENERATOR = register(
            "generator", () -> BlockEntityType.Builder.of(GeneratorBlockEntity::new, ModBlocks.GENERATOR.get())
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnergyCableBlockEntity>> ENERGY_CABLE = register(
            "energy_cable", () -> BlockEntityType.Builder.of(EnergyCableBlockEntity::new, ModBlocks.ENERGY_CABLE.get())
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
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<HardenedAlloySmelterBlockEntity>> HARDENED_ALLOY_SMELTER = register(
            "hardened_alloy_smelter", () -> BlockEntityType.Builder.of(HardenedAlloySmelterBlockEntity::new, ModBlocks.HARDENED_ALLOY_SMELTER.get())
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ElectricCentrifugeBlockEntity>> ELECTRIC_CENTRIFUGE_BE = register(
            "electric_centrifuge", () -> BlockEntityType.Builder.of(ElectricCentrifugeBlockEntity::new, ModBlocks.ELECTRIC_CENTRIFUGE.get())
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ElectricPurifierBlockEntity>> ELECTRIC_PURIFIER = register(
            "electric_purifier", () -> BlockEntityType.Builder.of(ElectricPurifierBlockEntity::new, ModBlocks.ELECTRIC_PURIFIER.get())
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FluidTankBlockEntity>> FLUID_TANK = register(
            "fluid_tank", () -> BlockEntityType.Builder.of(FluidTankBlockEntity::new, ModBlocks.FLUID_TANK.get())
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CrucibleBlockEntity>> CRUCIBLE = register(
            "crucible", () -> BlockEntityType.Builder.of(CrucibleBlockEntity::new, ModBlocks.CRUCIBLE.get())
    );

    private static <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> register(String name, Supplier<BlockEntityType.Builder<T>> builder) {
        return BLOCK_ENTITIES.register(name,()-> builder.get().build(null));
    }

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
