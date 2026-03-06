package com.grobe.techrebirth.item;

import com.grobe.techrebirth.util.ModTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;

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
            return BlockTags.INCORRECT_FOR_NETHERITE_TOOL;
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
   public static final Tier TIN = new Tier(){
       @Override
       public int getUses() {return 300;}
       @Override
       public float getSpeed() {return 8.0f;}
       @Override
       public float getAttackDamageBonus() {return 2.0f;}
       @Override
       public int getEnchantmentValue() {return 10;}
       @Override
       public Ingredient getRepairIngredient() {return Ingredient.of(ModTags.Items.INGOTS_TIN_D.common());}
       @Override
       public TagKey<Block> getIncorrectBlocksForDrops() { return BlockTags.INCORRECT_FOR_DIAMOND_TOOL;}
   };

}
