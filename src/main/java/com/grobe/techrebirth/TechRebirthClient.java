package com.grobe.techrebirth;

import com.grobe.techrebirth.block.ModBlockEntities;
import com.grobe.techrebirth.block.ModBlocks;
import com.grobe.techrebirth.client.renderer.Crucible;
import com.grobe.techrebirth.client.renderer.CrucibleRenderer;
import com.grobe.techrebirth.client.renderer.FluidTankBER;
import com.grobe.techrebirth.item.ModItems;
import com.grobe.techrebirth.util.MetalType;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.Map;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = TechRebirth.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = TechRebirth.MODID, value = Dist.CLIENT)
public class TechRebirthClient {
    public TechRebirthClient(ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
        TechRebirth.LOGGER.info("HELLO FROM CLIENT SETUP");
        TechRebirth.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.FLUID_TANK.get(), FluidTankBER::new);
        event.registerBlockEntityRenderer(ModBlockEntities.CRUCIBLE.get(), CrucibleRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(Crucible.LAYER_LOCATION, Crucible::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        // Nuggets
        for (Map.Entry<MetalType, DeferredItem<Item>> entry : ModItems.NUGGETS.entrySet()) {
            event.register((stack, tintIndex) -> {
                return tintIndex == 0 ? entry.getKey().getColor() : -1;
            }, entry.getValue().get());
        }

        // Ore Blocks (Items)
        for (Map.Entry<MetalType, DeferredBlock<Block>> entry : ModBlocks.ORE_BLOCKS.entrySet()) {
            if (entry.getKey() == MetalType.DIAMOND) continue; // Skip Diamond Block item colors
            event.register((stack, tintIndex) -> {
                return tintIndex == 0 ? entry.getKey().getColor() : -1;
            }, entry.getValue().get());
        }
    }

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        // Ore Blocks
        for (Map.Entry<MetalType, DeferredBlock<Block>> entry : ModBlocks.ORE_BLOCKS.entrySet()) {
            if (entry.getKey() == MetalType.DIAMOND) continue; // Skip Diamond Block colors
            event.register((state, world, pos, tintIndex) -> {
                return tintIndex == 0 ? entry.getKey().getColor() : -1;
            }, entry.getValue().get());
        }
    }
}
