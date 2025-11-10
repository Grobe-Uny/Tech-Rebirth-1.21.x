package com.grobe.techrebirth;

import com.grobe.techrebirth.block.ModBlocks;
import com.grobe.techrebirth.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TechRebirth.MODID);


    public static final Supplier<CreativeModeTab> TECH_REBIRTH_ITEMS = CREATIVE_MODE_TAB.register("tech_rebirth_items",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.IRON_GEAR.get()))
                    .title(Component.translatable("creativetab.techrebirth.tech_rebirth_items"))
                    .displayItems((itemDisplayParameters, output)-> {
                        output.accept(ModItems.IRON_GEAR);
                        output.accept(ModItems.COPPER_GEAR);
                        output.accept(ModItems.TIN_GEAR);
                        output.accept(ModItems.INVAR_GEAR);
                        output.accept(ModItems.LEAD_GEAR);
                        output.accept(ModItems.STEEL_GEAR);
                        output.accept(ModItems.IRON_POWDER);
                        output.accept(ModItems.COPPER_POWDER);
                        output.accept(ModItems.TIN_POWDER);
                        output.accept(ModItems.DIAMOND_POWDER);
                        output.accept(ModItems.REDSTONE_RECEPTION_COIL);
                        output.accept(ModItems.COOKED_CARROT);
                        output.accept(ModItems.EFFICIENCY_UPGRADE);
                        output.accept(ModItems.SPEED_UPGRADE);
                        output.accept(ModItems.BLAZING_GOLD_HELMET);
                        output.accept(ModItems.BLAZING_GOLD_CHESTPLATE);
                        output.accept(ModItems.BLAZING_GOLD_LEGGINGS);
                        output.accept(ModItems.BLAZING_GOLD_BOOTS);

                        output.accept(ModItems.BLAZING_GOLD_SWORD);
                        output.accept(ModItems.BLAZING_GOLD_AXE);
                        output.accept(ModItems.BLAZING_GOLD_PICKAXE);
                        output.accept(ModItems.BLAZING_GOLD_SHOVEL);
                        output.accept(ModItems.BLAZING_GOLD_HOE);


                    })
                    .build());
    public static final Supplier<CreativeModeTab> TECH_REBIRTH_BLOCKS = CREATIVE_MODE_TAB.register("tech_rebirth_blocks",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModBlocks.ELECTRIC_FURNACE.get()))
                    .withTabsBefore(ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "tech_rebirth_items"))
                    .title(Component.translatable("creativetab.techrebirth.tech_rebirth_blocks"))
                    .displayItems((itemDisplayParameters, output)-> {
                        output.accept(ModBlocks.MACHINE_BASE);
                        output.accept(ModBlocks.ELECTRIC_FURNACE);
                        output.accept(ModBlocks.HARDENED_ELECTRIC_FURNACE);
                        output.accept(ModBlocks.REINFORCED_ELECTRIC_FURNACE);
                        output.accept(ModBlocks.CREATIVE_ELECTRIC_FURNACE);
                        output.accept(ModBlocks.GENERATOR);
                        output.accept(ModBlocks.ELECTRIC_CRUSHER);
                      //  output.accept(ModBlocks.ENERGY_BANK);
                        output.accept(ModBlocks.ALLOY_SMELTER);
                        output.accept(ModBlocks.HARDENED_ALLOY_SMELTER);
                        output.accept(ModBlocks.ELECTRIC_CENTRIFUGE);


                        output.accept(ModBlocks.FLUID_TANK);

                    })
                    .build());
    public static final Supplier<CreativeModeTab> TECH_REBIRTH_CABLES = CREATIVE_MODE_TAB.register("tech_rebirth_cables",
            ()-> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModBlocks.ENERGY_CABLE.get()))
                    .withTabsBefore(ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "tech_rebirth_blocks"))
                    .title(Component.translatable("creativetab.techrebirth.tech_rebirth_cables"))
                    .displayItems((itemDisplayParameters, output) ->{
                    output.accept(ModBlocks.ENERGY_CABLE);
    })
                    .build());
    public static final Supplier<CreativeModeTab> TECH_REBIRTH_ORES = CREATIVE_MODE_TAB.register("tech_rebirth_ores",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.LEAD_INGOT.get()))
                    .withTabsBefore(ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "tech_rebirth_cables"))
                    .title(Component.translatable("creativetab.techrebirth.tech_rebirth_ores"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModBlocks.TIN_ORE);
                        output.accept(ModBlocks.TIN_DEEPSLATE_ORE);
                        output.accept(ModBlocks.NICKEL_ORE);
                        output.accept(ModBlocks.NICKEL_DEEPSLATE_ORE);
                        output.accept(ModBlocks.LEAD_ORE);
                        output.accept(ModBlocks.LEAD_DEEPSLATE_ORE);
                        output.accept(ModBlocks.INVAR_BLOCK);
                        output.accept(ModItems.RAW_TIN);
                        output.accept(ModItems.TIN_INGOT);
                        output.accept(ModItems.RAW_NICKEL);
                        output.accept(ModItems.NICKEL_INGOT);
                        output.accept(ModItems.NICKEL_POWDER);
                        output.accept(ModItems.INVAR_INGOT);
                        output.accept(ModItems.STEEL_INGOT);
                        output.accept(ModItems.LEAD_INGOT);
                        output.accept(ModItems.BLAZING_GOLD_INGOT);
                        output.accept(ModItems.RAW_LEAD);
                    })
                    .build());

    public static void register (IEventBus eventbus){
        CREATIVE_MODE_TAB.register(eventbus);
    }

//    public static void addCreative(BuildCreativeModeTabContentsEvent event){
//        if(event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES){
//            event.accept(ModItems.LIGHTNING_STRIKER_ENCHANTED_BOOK.get());
//        }
//    }

}
