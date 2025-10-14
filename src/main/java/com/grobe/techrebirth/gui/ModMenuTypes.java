package com.grobe.techrebirth.gui;

import com.grobe.techrebirth.TechRebirth;
import com.grobe.techrebirth.gui.alloy_smelter.AlloySmelterMenu;
import com.grobe.techrebirth.gui.electric_crusher.ElectricCrusherMenu;
import com.grobe.techrebirth.gui.electric_furnace.ElectricFurnaceMenu;
import com.grobe.techrebirth.gui.generator.GeneratorMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, TechRebirth.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<ElectricFurnaceMenu>> ELECTRIC_FURNACE_MENU =
            MENUS.register("electric_furnace_menu", () -> IMenuTypeExtension.create((windowId, inv, buf) -> new ElectricFurnaceMenu(windowId, inv, buf)));

    public static final DeferredHolder<MenuType<?>, MenuType<AlloySmelterMenu>> ALLOY_SMELTER_MENU =
            MENUS.register("alloy_smelter_menu", () -> IMenuTypeExtension.create((windowId, inv, buf) -> new AlloySmelterMenu(windowId, inv, buf)));

    public static final DeferredHolder<MenuType<?>, MenuType<com.grobe.techrebirth.gui.electric_crusher.ElectricCrusherMenu>> ELECTRIC_CRUSHER_MENU =
            MENUS.register("electric_crusher_menu", () -> IMenuTypeExtension.create((windowId, inv, buf) -> new ElectricCrusherMenu(windowId, inv, buf)));

    public static final DeferredHolder<MenuType<?>, MenuType<com.grobe.techrebirth.gui.generator.GeneratorMenu>> GENERATOR_MENU =
            MENUS.register("generator_menu", () -> IMenuTypeExtension.create((windowId, inv, buf) -> new GeneratorMenu(windowId, inv, buf)));

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}