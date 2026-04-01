package com.grobe.techrebirth.item;

import com.grobe.techrebirth.TechRebirth;
import com.grobe.techrebirth.item.custom.TinArmorItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;


public class ModArmorItems {

    public static final DeferredRegister.Items ARMOR_ITEMS = DeferredRegister.createItems(TechRebirth.MODID);
    // Blazing gold
    public static final DeferredItem<ArmorItem> BLAZING_GOLD_HELMET = ARMOR_ITEMS.register("blazing_gold_helmet",
            ()-> new ArmorItem(ModArmorMaterials.BLAZING_GOLD_ARMOR_MATERIAL, ArmorItem.Type.HELMET,
                    new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(40))));

    public static final DeferredItem<ArmorItem> BLAZING_GOLD_CHESTPLATE = ARMOR_ITEMS.register("blazing_gold_chestplate",
            ()-> new ArmorItem(ModArmorMaterials.BLAZING_GOLD_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(40))));

    public static final DeferredItem<ArmorItem> BLAZING_GOLD_LEGGINGS = ARMOR_ITEMS.register("blazing_gold_leggings",
            ()-> new ArmorItem(ModArmorMaterials.BLAZING_GOLD_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(40))));

    public static final DeferredItem<ArmorItem> BLAZING_GOLD_BOOTS = ARMOR_ITEMS.register("blazing_gold_boots",
            ()-> new ArmorItem(ModArmorMaterials.BLAZING_GOLD_ARMOR_MATERIAL, ArmorItem.Type.BOOTS,
                    new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(40))));
    //Tin
    public static DeferredItem<ArmorItem> TIN_HELMET = ARMOR_ITEMS.register("tin_helmet",
            ()-> new TinArmorItem(ModArmorMaterials.TIN_ARMOR_MATERIAL, ArmorItem.Type.HELMET,
                    new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(40))));

    public static final DeferredItem<ArmorItem> TIN_CHESTPLATE = ARMOR_ITEMS.register("tin_chestplate",
            ()-> new TinArmorItem(ModArmorMaterials.TIN_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(40))));

    public static final DeferredItem<ArmorItem> TIN_LEGGINGS = ARMOR_ITEMS.register("tin_leggings",
            ()-> new TinArmorItem(ModArmorMaterials.TIN_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(40))));

    public static final DeferredItem<ArmorItem> TIN_BOOTS = ARMOR_ITEMS.register("tin_boots",
            ()-> new TinArmorItem(ModArmorMaterials.TIN_ARMOR_MATERIAL, ArmorItem.Type.BOOTS,
                    new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(40))));


    public static void register(IEventBus eventbus){
        ARMOR_ITEMS.register(eventbus);
    }

}
