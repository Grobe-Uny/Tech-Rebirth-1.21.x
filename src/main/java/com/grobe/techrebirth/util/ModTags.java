package com.grobe.techrebirth.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;



/**
 * Centralized tag keys to keep datagen and runtime references neat and consistent.
 * Define tags once here and reuse across providers and recipes.
 */
public final class ModTags {

    private ModTags() {}

    public static final class Items {
        private static TagKey<Item> tag(String ns, String path) {
            return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ns, path));
        }

        /**
         * Pair of item TagKeys for the same logical tag in both namespaces.
         * neoforge = primary namespace on NeoForge; common = "c" namespace mirror.
         */
        public static final class Dual {
            private final TagKey<Item> neoforge;
            private final TagKey<Item> common;

            private Dual(TagKey<Item> neoforge, TagKey<Item> common) {
                this.neoforge = neoforge;
                this.common = common;
            }
            public TagKey<Item> neoforge() { return neoforge; }
            public TagKey<Item> common() { return common; }
            public TagKey<Item>[] both() { @SuppressWarnings("unchecked") TagKey<Item>[] arr = new TagKey[]{ neoforge, common }; return arr; }
        }

        /**
         * Create a pair of item tags for neoforge:<path> and c:<path>.
         * Example: dual("ingots/lead") -> neoforge:ingots/lead and c:ingots/lead
         */
        public static Dual dual(String path) {
            return new Dual(tag("neoforge", path), tag("c", path));
        }



        // Duals for common categories so you can easily add to both namespaces
        public static final Dual INGOTS_D = dual("ingots");
        public static final Dual INGOTS_LEAD_D = dual("ingots/lead");
        public static final Dual INGOTS_NICKEL_D = dual("ingots/nickel");
        public static final Dual INGOTS_TIN_D = dual("ingots/tin");
        public static final Dual INGOTS_INVAR_D = dual("ingots/invar");
        public static final Dual INGOTS_STEEL_D = dual("ingots/steel");
        public static final Dual INGOTS_BLAZING_GOLD_D = dual("ingots/blazing_gold");
        public static final Dual BASE_OBSIDIAN_INGOT_D = dual("ingots/base_obsidian");
        public static final Dual REFINED_OBSIDIAN_INGOT = dual("ingots/refined_obsidian");
        public static final Dual LITHIUM_INGOT_D = dual("ingots/lithium");

        // Duals for catalysts in electric centrifuge
        public static final Dual CATALYSTS_D = dual("catalyst");
        public static final Dual CATALYST_BLAZE = dual("catalyst/blaze");
        public static final Dual CATALYST_DIAMOND = dual("catalyst/diamond");

        // Duals for dust/powder compatibility
        public static final Dual DUSTS_D = dual("dusts");
        public static final Dual IRON_DUST_D = dual("dusts/iron");
        public static final Dual COPPER_DUST_D = dual("dusts/copper");
        public static final Dual GOLD_DUST_D = dual("dusts/gold");
        public static final Dual TIN_DUST_D = dual("dusts/tin");
        public static final Dual NICKEL_DUST_D = dual("dusts/nickel");
        public static final Dual DIAMOND_DUST_D = dual("dusts/diamond");
        public static final Dual PURIFIED_IRON_DUST_D = dual("dusts/iron");
        public static final Dual PURIFIED_COPPER_DUST_D = dual("dusts/copper");
        public static final Dual PURIFIED_GOLD_DUST_D = dual("dusts/gold");
        public static final Dual PURIFIED_TIN_DUST_D = dual("dusts/tin");
        public static final Dual PURIFIED_NICKEL_DUST_D = dual("dusts/nickel");
        public static final Dual OBSIDIAN_DUST_D = dual("dusts/obsidian");
        public static final Dual LITHIUM_DUST_D = dual("dusts/lithium");


        // Duals for raw ores compatibility
        public static final Dual RAW_MATERIALS_D = dual("raw_materials");
        public static final Dual RAW_TIN_D = dual("raw_materials/tin");
        public static final Dual RAW_NICKEL_D = dual("raw_materials/nickel");
        public static final Dual RAW_LEAD_D = dual("raw_materials/lead");

        // Furnace behavior categories (TechRebirth-specific)
        public static final Dual FURNACE_FAST_D = dual("techrebirth/furnace_fast");
        public static final Dual FURNACE_HEAVY_D = dual("techrebirth/furnace_heavy");

        // Canonical tags used in recipes (prefer neoforge on NeoForge)
        public static final TagKey<Item> INGOTS = tag("neoforge", "ingots");
        public static final TagKey<Item> INGOTS_LEAD = tag("neoforge", "ingots/lead");
        public static final TagKey<Item> INGOTS_NICKEL = tag("neoforge", "ingots/nickel");
        public static final TagKey<Item> INGOTS_TIN = tag("neoforge", "ingots/tin");
        public static final TagKey<Item> INGOTS_INVAR = tag("neoforge", "ingots/invar");
        public static final TagKey<Item> RAW_MATERIALS_LEAD = tag("neoforge", "raw_materials/lead");
        public static final TagKey<Item> ORES_LEAD_ITEMS = tag("neoforge", "ores/lead");

        // Common (c:) mirrors for compatibility when generating tags
        public static final TagKey<Item> C_INGOTS = tag("c", "ingots");
        public static final TagKey<Item> C_INGOTS_LEAD = tag("c", "ingots/lead");
        public static final TagKey<Item> C_RAW_MATERIALS_LEAD = tag("c", "raw_materials/lead");
        public static final TagKey<Item> C_ORES_LEAD_ITEMS = tag("c", "ores/lead");

    }

    public static final class Blocks {
        private static TagKey<Block> tag(String ns, String path) {
            return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(ns, path));
        }

        /**
         * Pair of block TagKeys for the same logical tag in both namespaces.
         */
        public static final class Dual {
            private final TagKey<Block> neoforge;
            private final TagKey<Block> common;
            private Dual(TagKey<Block> neoforge, TagKey<Block> common) {
                this.neoforge = neoforge;
                this.common = common;
            }
            public TagKey<Block> neoforge() { return neoforge; }
            public TagKey<Block> common() { return common; }
            public TagKey<Block>[] both() { @SuppressWarnings("unchecked") TagKey<Block>[] arr = new TagKey[]{ neoforge, common }; return arr; }
        }

        /**
         * Create a pair of block tags for neoforge:<path> and c:<path>.
         * Example: dual("ores/lead") -> neoforge:ores/lead and c:ores/lead
         */
        public static Dual dual(String path) {
            return new Dual(tag("neoforge", path), tag("c", path));
        }

        // Duals for common block categories
        public static final Dual ORES_LEAD_D = dual("ores/lead");

        public static final TagKey<Block> ORES_LEAD = tag("neoforge", "ores/lead");
        public static final TagKey<Block> C_ORES_LEAD = tag("c", "ores/lead");
    }
}
