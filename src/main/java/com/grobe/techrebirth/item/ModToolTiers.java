package com.grobe.techrebirth.item;

import com.grobe.techrebirth.util.ModTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

public class ModToolTiers {

    public static final Tier BLAZING_GOLD = new Tier() {
        @Override
        public int getUses() {
            return 1800;
        }

        @Override
        public float getSpeed() {
            return 10.0f;
        }

        @Override
        public float getAttackDamageBonus() {
            return 4.0f;
        }

        @Override
        public TagKey<Block> getIncorrectBlocksForDrops() {
            return null;
        }

        @Override
        public int getEnchantmentValue() {
            return 18;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.of(ModTags.Items.INGOTS_BLAZING_GOLD_D.common());
        }
    };
}
