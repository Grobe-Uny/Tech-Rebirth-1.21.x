package com.grobe.techrebirth.item.custom;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.resources.ResourceLocation;
import com.grobe.techrebirth.TechRebirth;

public class TinArmorItem extends ArmorItem {
    // 5% speed boost per piece
    private static final double SPEED_BONUS = 0.05;

    public TinArmorItem(Holder<ArmorMaterial> material, Type type, Properties properties) {
        super(material, type, properties);
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers() {
        ItemAttributeModifiers modifiers = super.getDefaultAttributeModifiers();
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();

        // Copy existing modifiers (defense, toughness, etc.)
        for (ItemAttributeModifiers.Entry entry : modifiers.modifiers()) {
            builder.add(entry.attribute(), entry.modifier(), entry.slot());
        }

        // Add movement speed modifier
        // We use a unique ID based on the slot so they stack
        ResourceLocation modifierId = ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "tin_armor_speed_" + this.getType().getName());
        
        builder.add(
                Attributes.MOVEMENT_SPEED,
                new AttributeModifier(modifierId, SPEED_BONUS, AttributeModifier.Operation.ADD_MULTIPLIED_BASE),
                EquipmentSlotGroup.bySlot(this.getType().getSlot()) // Apply only when worn in the correct slot
        );

        return builder.build();
    }
}
