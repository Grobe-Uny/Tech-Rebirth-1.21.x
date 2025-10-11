package com.grobe.techrebirth.item;

import com.grobe.techrebirth.TechRebirth;
import com.grobe.techrebirth.item.custom.UpgradeItem;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;


public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(TechRebirth.MODID);

    //stuff already in minecraft but adding support for other mod items
    public static final DeferredItem<Item> IRON_POWDER = ITEMS.register("iron_powder",
            () -> new Item (new Item.Properties()));
    public static final DeferredItem<Item> COPPER_POWDER = ITEMS.register("copper_powder",
            ()-> new Item (new Item.Properties()));
    public static final DeferredItem<Item> COPPER_GEAR = ITEMS.register("copper_gear",
            ()-> new Item(new Item.Properties()));
    public static final DeferredItem<Item> IRON_GEAR = ITEMS.register("iron_gear",
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


    //miscellaneous items
    public static final DeferredItem<Item> REDSTONE_RECEPTION_COIL = ITEMS.register("redstone_reception_coil",
            ()-> new Item(new Item.Properties()));



    //Food Items
    public static final DeferredItem<Item> COOKED_CARROT = ITEMS.register("cooked_carrot",
            () -> new Item(new Item.Properties().food(ModFoodProperties.COOKED_CARROT)));


    public static void register(IEventBus eventbus){
        ITEMS.register(eventbus);
    }
}
