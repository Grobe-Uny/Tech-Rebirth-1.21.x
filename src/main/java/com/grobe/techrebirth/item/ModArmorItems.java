package com.grobe.techrebirth.item;

import com.grobe.techrebirth.item.custom.TinArmorItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;

import static com.grobe.techrebirth.item.ModItems.ITEMS;

public class ModArmorItems {

    // Blazing gold
    public static final DeferredItem<ArmorItem> BLAZING_GOLD_HELMET = ITEMS.register("blazing_gold_helmet",
            ()-> new ArmorItem(ModArmorMaterials.BLAZING_GOLD_ARMOR_MATERIAL, ArmorItem.Type.HELMET,
                    new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(40))));

    public static final DeferredItem<ArmorItem> BLAZING_GOLD_CHESTPLATE = ITEMS.register("blazing_gold_chestplate",
            ()-> new ArmorItem(ModArmorMaterials.BLAZING_GOLD_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(40))));

    public static final DeferredItem<ArmorItem> BLAZING_GOLD_LEGGINGS = ITEMS.register("blazing_gold_leggings",
            ()-> new ArmorItem(ModArmorMaterials.BLAZING_GOLD_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(40))));

    public static final DeferredItem<ArmorItem> BLAZING_GOLD_BOOTS = ITEMS.register("blazing_gold_boots",
            ()-> new ArmorItem(ModArmorMaterials.BLAZING_GOLD_ARMOR_MATERIAL, ArmorItem.Type.BOOTS,
                    new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(40))));
    //Tin
    public static DeferredItem<ArmorItem> TIN_HELMET = ITEMS.register("tin_helmet",
            ()-> new TinArmorItem(ModArmorMaterials.TIN_ARMOR_MATERIAL, ArmorItem.Type.HELMET,
                    new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(40))));

    public static final DeferredItem<ArmorItem> TIN_CHESTPLATE = ITEMS.register("tin_chestplate",
            ()-> new TinArmorItem(ModArmorMaterials.TIN_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(40))));

    public static final DeferredItem<ArmorItem> TIN_LEGGINGS = ITEMS.register("tin_leggings",
            ()-> new TinArmorItem(ModArmorMaterials.TIN_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(40))));

    public static final DeferredItem<ArmorItem> TIN_BOOTS = ITEMS.register("tin_boots",
            ()-> new TinArmorItem(ModArmorMaterials.TIN_ARMOR_MATERIAL, ArmorItem.Type.BOOTS,
                    new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(40))));

}
