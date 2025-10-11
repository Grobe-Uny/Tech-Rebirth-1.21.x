package com.grobe.techrebirth.enchantment;

import com.grobe.techrebirth.TechRebirth;
import com.grobe.techrebirth.enchantment.custom.LightningStrikeEnchantmentEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import org.openjdk.nashorn.internal.runtime.linker.Bootstrap;

public class ModEnchantments {
    public static final ResourceKey<Enchantment> LIGHTNING_STRIKE = ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "lightning_strike"));

    public static void bootstrap(BootstrapContext<Enchantment> context){
        var enchantments = context.lookup(Registries.ENCHANTMENT);
        var items = context.lookup(Registries.ITEM);

        register(context, LIGHTNING_STRIKE, Enchantment.enchantment(Enchantment.definition(
                items.getOrThrow(ItemTags.WEAPON_ENCHANTABLE),
                items.getOrThrow(ItemTags.SWORD_ENCHANTABLE),
                5,
                3,
                Enchantment.dynamicCost(4,6),
                Enchantment.dynamicCost(16, 6),
                2,
                EquipmentSlotGroup.MAINHAND))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM, new LightningStrikeEnchantmentEffect()));
    }

    private static void register(BootstrapContext<Enchantment> registry, ResourceKey<Enchantment> key, Enchantment.Builder builder){
        registry.register(key, builder.build(key.location()));
    }

}
