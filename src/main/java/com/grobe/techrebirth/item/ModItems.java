package com.grobe.techrebirth.item;

import com.grobe.techrebirth.TechRebirth;
import com.grobe.techrebirth.fluid.ModFluids;
import com.grobe.techrebirth.item.custom.UpgradeItem;
import com.grobe.techrebirth.item.custom.WrenchItem;
import com.grobe.techrebirth.item.custom.util.EnergyInformatorItem;
import com.grobe.techrebirth.util.MetalType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;


public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(TechRebirth.MODID);

    // A map to hold our dynamically registered nuggets
    public static final Map<MetalType, DeferredItem<Item>> NUGGETS = new EnumMap<>(MetalType.class);



    static {
        for (MetalType metal : MetalType.values()) {
            NUGGETS.put(metal, ITEMS.register(metal.getSerializedName() + "_nugget",
                    () -> new Item(new Item.Properties())));
        }
    }

    //stuff already in minecraft but adding support for other mod items
    public static final DeferredItem<Item> IRON_POWDER = ITEMS.register("iron_powder",
            () -> new Item (new Item.Properties()));
    public static final DeferredItem<Item> COPPER_POWDER = ITEMS.register("copper_powder",
            ()-> new Item (new Item.Properties()));
    public static final DeferredItem<Item> GOLD_POWDER = ITEMS.register("gold_powder",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> COPPER_GEAR = ITEMS.register("copper_gear",
            ()-> new Item(new Item.Properties()));
    public static final DeferredItem<Item> IRON_GEAR = ITEMS.register("iron_gear",
            ()-> new Item(new Item.Properties()));
    public static final DeferredItem<Item> DIAMOND_POWDER = ITEMS.register("diamond_powder",
            () -> new Item(new Item.Properties()));


    public static final DeferredItem<Item> LITHIUM_POWDER = ITEMS.register("lithium_powder",
            ()-> new Item(new Item.Properties()));
    public static final DeferredItem<Item> LITHIUM_INGOT = ITEMS.register("lithium_ingot",
            ()-> new Item(new Item.Properties()));


    public static final DeferredItem<Item> SPEED_UPGRADE = ITEMS.register("speed_upgrade",
            () -> new UpgradeItem(new Item.Properties()));

    public static final DeferredItem<Item> EFFICIENCY_UPGRADE = ITEMS.register("efficiency_upgrade",
            () -> new UpgradeItem(new Item.Properties()));

    // tin
    public static final DeferredItem<Item> RAW_TIN = ITEMS.register("raw_tin",
            ()-> new Item(new Item.Properties()));
    public static final DeferredItem<Item> TIN_INGOT = ITEMS.register("tin_ingot",
            ()-> new Item (new Item.Properties()){
                @Override
                public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
                    pTooltipComponents.add(Component.translatable("tooltip.techrebirth.raw_tin"));
                    super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
                }
            });
    public static final DeferredItem<Item> TIN_POWDER = ITEMS.register("tin_powder",
            ()-> new Item (new Item.Properties()));
    public static final DeferredItem<Item> TIN_GEAR = ITEMS.register("tin_gear",
            ()-> new Item (new Item.Properties()));

    //nickel
    public static final DeferredItem<Item> RAW_NICKEL = ITEMS.register("raw_nickel",
            ()-> new Item(new Item.Properties()));
    public static final DeferredItem<Item> NICKEL_INGOT = ITEMS.register("nickel_ingot",
            ()-> new Item(new Item.Properties()));
    public static final DeferredItem<Item> NICKEL_POWDER = ITEMS.register("nickel_powder",
            ()-> new Item(new Item.Properties()));

    //invar
    public static final DeferredItem<Item> INVAR_INGOT = ITEMS.register("invar_ingot",
    () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INVAR_GEAR = ITEMS.register("invar_gear",
            ()-> new Item(new Item.Properties()));


    //lead
    public static final DeferredItem<Item> LEAD_INGOT = ITEMS.register("lead_ingot",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> RAW_LEAD = ITEMS.register("raw_lead",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> LEAD_GEAR = ITEMS.register("lead_gear",
            () -> new Item(new Item.Properties()));



    //steel
    public static final DeferredItem<Item> STEEL_INGOT = ITEMS.register("steel_ingot",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> STEEL_GEAR = ITEMS.register("steel_gear",
            () -> new Item(new Item.Properties()));

    //misc ingots
    public static final DeferredItem<Item> BASE_GOLDIUM_INGOT = ITEMS.register("base_goldium_ingot",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BLAZING_GOLD_INGOT = ITEMS.register("blazing_gold_ingot",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BASE_OBSIDIAN_INGOT = ITEMS.register("base_obsidian_ingot", 
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> REFINED_OBSIDIAN_INGOT = ITEMS.register("refined_obsidian_ingot", 
            () -> new Item(new Item.Properties()));



    public static final DeferredItem<Item> ISOLATUM_BLEND = ITEMS.register("isolatum_blend",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> ISOLATUM_COMPOSITE = ITEMS.register("isolatum_composite",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CONDUCTIUM_INGOT = ITEMS.register("conductium_ingot",
            () -> new Item(new Item.Properties()));


    public static final DeferredItem<Item> PURIFIED_IRON_POWDER = ITEMS.register("purified_iron_powder",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> PURIFIED_COPPER_POWDER = ITEMS.register("purified_copper_powder",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> PURIFIED_GOLD_POWDER = ITEMS.register("purified_gold_powder",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> PURIFIED_TIN_POWDER = ITEMS.register("purified_tin_powder",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> PURIFIED_NICKEL_POWDER = ITEMS.register("purified_nickel_powder",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> OBSIDIAN_POWDER = ITEMS.register("obsidian_powder", 
            () -> new Item(new Item.Properties()));



    //miscellaneous items
    public static final DeferredItem<Item> REDSTONE_RECEPTION_COIL = ITEMS.register("redstone_reception_coil",
            ()-> new Item(new Item.Properties()));
    public static final DeferredItem<Item> LEAD_WRENCH = ITEMS.register("lead_wrench",
            () -> new WrenchItem(new Item.Properties()
                    .stacksTo(1)
            ));
    public static final DeferredItem<Item> ENERGY_INFORMATOR = ITEMS.register("energy_informator",
            ()-> new EnergyInformatorItem(new Item.Properties()
                    .stacksTo(1)));

    public static final DeferredItem<Item> LIQUIFIED_COAL_BUCKET = ITEMS.register("liquified_coal_bucket",
            () -> new BucketItem(ModFluids.SOURCE_LIQUIFIED_COAL.get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

    //Food Items
    public static final DeferredItem<Item> COOKED_CARROT = ITEMS.register("cooked_carrot",
            () -> new Item(new Item.Properties().food(ModFoodProperties.COOKED_CARROT)));

    public static void register(IEventBus eventbus){
        ITEMS.register(eventbus);
    }
}
