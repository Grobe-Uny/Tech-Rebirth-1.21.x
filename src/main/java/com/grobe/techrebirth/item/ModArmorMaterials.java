package com.grobe.techrebirth.item;

import com.grobe.techrebirth.TechRebirth;
import com.grobe.techrebirth.util.ModTags;
import net.minecraft.Util;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

public class ModArmorMaterials {

    public static final Holder<ArmorMaterial> BLAZING_GOLD_ARMOR_MATERIAL = register("blazing_gold_armor",
            Util.make(new EnumMap<>(ArmorItem.Type.class), attribute ->{
                attribute.put(ArmorItem.Type.BOOTS, 4);
                attribute.put(ArmorItem.Type.LEGGINGS, 7);
                attribute.put(ArmorItem.Type.CHESTPLATE, 8);
                attribute.put(ArmorItem.Type.HELMET, 4);
                attribute.put(ArmorItem.Type.BODY, 11);
            }), 13, 2f, 0.2f, ()-> Ingredient.of(ModItems.BLAZING_GOLD_INGOT.get()));

    public static final Holder<ArmorMaterial> TIN_ARMOR_MATERIAL = register("tin_armor",
            Util.make(new EnumMap<>(ArmorItem.Type.class), attribute -> {
                attribute.put(ArmorItem.Type.BOOTS,2);
                attribute.put(ArmorItem.Type.LEGGINGS,4);
                attribute.put(ArmorItem.Type.CHESTPLATE,5);
                attribute.put(ArmorItem.Type.HELMET,3);
                attribute.put(ArmorItem.Type.BODY,7);
            }),10, 0f, 0f, () -> Ingredient.of(ModTags.Items.INGOTS_TIN_D.common()));



    private static Holder<ArmorMaterial> register (String name, EnumMap<ArmorItem.Type, Integer> typeProtection,
                                                   int enchantability, float toughness, float knockbackResistance,
                                                   Supplier<Ingredient> ingredientSupplier){
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, name);
        Holder<SoundEvent> equipSound = SoundEvents.ARMOR_EQUIP_NETHERITE;
        List<ArmorMaterial.Layer> layers = List.of(new ArmorMaterial.Layer(location));

        EnumMap<ArmorItem.Type, Integer> typeMap = new EnumMap<>(ArmorItem.Type.class);
        for(ArmorItem.Type type : ArmorItem.Type.values()){
            typeMap.put(type, typeProtection.get(type));
        }

        return Registry.registerForHolder(BuiltInRegistries.ARMOR_MATERIAL, location,
                new ArmorMaterial(typeProtection, enchantability, equipSound, ingredientSupplier, layers, toughness, knockbackResistance));
    }
}
