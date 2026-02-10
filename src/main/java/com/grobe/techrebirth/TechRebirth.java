package com.grobe.techrebirth;

import com.grobe.techrebirth.block.ModBlockEntities;
import com.grobe.techrebirth.block.ModBlocks;
import com.grobe.techrebirth.enchantment.ModEnchantmentEffects;
import com.grobe.techrebirth.gui.ModMenuTypes;
import com.grobe.techrebirth.gui.alloy_smelter.AlloySmelterMenu;
import com.grobe.techrebirth.gui.alloy_smelter.AlloySmelterScreen;
import com.grobe.techrebirth.gui.electric_crusher.ElectricCrusherScreen;
import com.grobe.techrebirth.gui.electric_furnace.ElectricFurnaceScreen;
import com.grobe.techrebirth.gui.generator.GeneratorScreen;
import com.grobe.techrebirth.item.ModItems;
import com.grobe.techrebirth.recipe.ModRecipeTypes;
import com.grobe.techrebirth.sound.ModSounds;
import com.grobe.techrebirth.util.ModDataComponents;
import com.grobe.techrebirth.util.TooltipModifier;
import net.neoforged.fml.common.EventBusSubscriber;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(TechRebirth.MODID)
public class TechRebirth {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "techrebirth";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();


    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public TechRebirth(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (TechRebirth) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        ModCreativeTabs.register(modEventBus);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModDataComponents.DATA_COMPONENTS.register(modEventBus);
        ModRecipeTypes.register(modEventBus);


        ModEnchantmentEffects.register(modEventBus);
        ModSounds.register(modEventBus);

        modEventBus.addListener(this::addCreativeTab);
        //modEventBus.addListener(ModCreativeTabs::addCreative);

        NeoForge.EVENT_BUS.addListener(TooltipModifier::onItemTooltip);


        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.CLIENT, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {

    }

    private void addCreativeTab(BuildCreativeModeTabContentsEvent event){

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onRegisterMenuScreens(net.neoforged.neoforge.client.event.RegisterMenuScreensEvent event)
        {
            event.register(ModMenuTypes.ELECTRIC_FURNACE_MENU.get(), ElectricFurnaceScreen::new);
            event.register(ModMenuTypes.GENERATOR_MENU.get(), GeneratorScreen::new);
            event.register(ModMenuTypes.ELECTRIC_CRUSHER_MENU.get(), ElectricCrusherScreen::new);
            event.register(ModMenuTypes.ALLOY_SMELTER_MENU.get(), AlloySmelterScreen::new);
            event.register(ModMenuTypes.ELECTRIC_CENTRIFUGE_MENU.get(), com.grobe.techrebirth.gui.electric_centrifuge.ElectricCentrifugeScreen::new);
        }
    }
}
