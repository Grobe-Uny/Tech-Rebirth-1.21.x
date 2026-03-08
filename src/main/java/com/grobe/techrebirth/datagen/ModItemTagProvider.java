package com.grobe.techrebirth.datagen;

import com.grobe.techrebirth.TechRebirth;
import com.grobe.techrebirth.item.ModItems;
import com.grobe.techrebirth.util.ModTags;
import com.grobe.techrebirth.block.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends ItemTagsProvider {

    public ModItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup,
                              BlockTagsProvider blockTags, ExistingFileHelper helper) {
        super(output, lookup, blockTags.contentsGetter(), TechRebirth.MODID, helper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider){

        tag(ItemTags.TRIMMABLE_ARMOR)
                .add(ModItems.BLAZING_GOLD_HELMET.get())
                .add(ModItems.BLAZING_GOLD_CHESTPLATE.get())
                .add(ModItems.BLAZING_GOLD_LEGGINGS.get())
                .add(ModItems.BLAZING_GOLD_BOOTS.get())
                .add(ModItems.TIN_HELMET.get())
                .add(ModItems.TIN_CHESTPLATE.get())
                .add(ModItems.TIN_LEGGINGS.get())
                .add(ModItems.TIN_BOOTS.get());

        tag(ItemTags.SWORDS)
                .add(ModItems.BLAZING_GOLD_SWORD.get())
                .add(ModItems.TIN_SWORD.get());
        tag(ItemTags.AXES)
                .add(ModItems.BLAZING_GOLD_AXE.get())
                .add(ModItems.TIN_AXE.get());
        tag(ItemTags.PICKAXES)
                .add(ModItems.BLAZING_GOLD_PICKAXE.get())
                .add(ModItems.TIN_PICKAXE.get());
        tag(ItemTags.SHOVELS)
                .add(ModItems.BLAZING_GOLD_SHOVEL.get())
                .add(ModItems.TIN_SHOVEL.get());
        tag(ItemTags.HOES)
                .add(ModItems.BLAZING_GOLD_HOE.get())
                .add(ModItems.TIN_HOE.get());



        // Add catalysts to both c: and neoforge: catalyst via Dual helpers
        for(var tagKey : ModTags.Items.CATALYST_BLAZE.both())   tag(tagKey).add(Items.BLAZE_POWDER);

        // Add our ingots to both c: and neoforge: ingot tags via Dual helpers
        for (var tagKey : ModTags.Items.INGOTS_LEAD_D.both())   tag(tagKey).add(ModItems.LEAD_INGOT.get());
        for (var tagKey : ModTags.Items.INGOTS_TIN_D.both())    tag(tagKey).add(ModItems.TIN_INGOT.get());
        for (var tagKey : ModTags.Items.INGOTS_NICKEL_D.both()) tag(tagKey).add(ModItems.NICKEL_INGOT.get());
        for (var tagKey : ModTags.Items.INGOTS_INVAR_D.both())  tag(tagKey).add(ModItems.INVAR_INGOT.get());
        for (var tagKey : ModTags.Items.INGOTS_STEEL_D.both())  tag(tagKey).add(ModItems.STEEL_INGOT.get());
        for (var tagKey : ModTags.Items.INGOTS_BLAZING_GOLD_D.both()) tag(tagKey).add(ModItems.BLAZING_GOLD_INGOT.get());


        // Add our dusts to both c: and neoforge: dust tags via Dual helpers
        for(var tagKey : ModTags.Items.IRON_DUST_D.both())      tag(tagKey).add(ModItems.IRON_POWDER.get());
        for(var tagKey : ModTags.Items.COPPER_DUST_D.both())    tag(tagKey).add(ModItems.COPPER_POWDER.get());
        for(var tagKey : ModTags.Items.TIN_DUST_D.both())       tag(tagKey).add(ModItems.TIN_POWDER.get());
        for(var tagKey : ModTags.Items.NICKEL_DUST_D.both())    tag(tagKey).add(ModItems.NICKEL_POWDER.get());
        for(var tagKey : ModTags.Items.DIAMOND_DUST_D.both())   tag(tagKey).add(ModItems.DIAMOND_POWDER.get());

        // Add our raw materials to both c: and neoforge: raw_materials tags via Dual helpers
        for(var tagKey : ModTags.Items.RAW_NICKEL_D.both())     tag(tagKey).add(ModItems.RAW_NICKEL.get());
        for(var tagKey : ModTags.Items.RAW_LEAD_D.both())     tag(tagKey).add(ModItems.RAW_LEAD.get());
        for(var tagKey : ModTags.Items.RAW_TIN_D.both())     tag(tagKey).add(ModItems.RAW_TIN.get());


        // Furnace category tags: mark heavy items (raws + ores) so furnace treats them as heavier
        for (var tagKey : ModTags.Items.FURNACE_HEAVY_D.both()) {
            tag(tagKey)
                .add(ModItems.RAW_TIN.get())
                .add(ModItems.RAW_NICKEL.get())
                .add(ModItems.RAW_LEAD.get())
                .add(ModBlocks.TIN_ORE.get().asItem())
                .add(ModBlocks.TIN_DEEPSLATE_ORE.get().asItem())
                .add(ModBlocks.NICKEL_ORE.get().asItem())
                .add(ModBlocks.NICKEL_DEEPSLATE_ORE.get().asItem())
                .add(ModBlocks.LEAD_ORE.get().asItem())
                .add(ModBlocks.LEAD_DEEPSLATE_ORE.get().asItem());
        }
        // Optionally mark some foods as fast for clarity (can expand later)
        for (var tagKey : ModTags.Items.FURNACE_FAST_D.both()) {
            tag(tagKey)
                .add(net.minecraft.world.item.Items.POTATO)
                .add(net.minecraft.world.item.Items.BEEF)
                .add(net.minecraft.world.item.Items.PORKCHOP);
        }

        // Ensure umbrella tags include their subtags in both namespaces
        var ingotsBoth = ModTags.Items.INGOTS_D.both();
        tag(ingotsBoth[0])
                .addTag(ModTags.Items.INGOTS_LEAD_D.neoforge())
                .addTag(ModTags.Items.INGOTS_TIN_D.neoforge())
                .addTag(ModTags.Items.INGOTS_NICKEL_D.neoforge())
                .addTag(ModTags.Items.INGOTS_INVAR_D.neoforge())
                .addTag(ModTags.Items.INGOTS_BLAZING_GOLD_D.neoforge())
                .addTag(ModTags.Items.INGOTS_STEEL_D.neoforge());
        tag(ingotsBoth[1])
                .addTag(ModTags.Items.INGOTS_LEAD_D.common())
                .addTag(ModTags.Items.INGOTS_TIN_D.common())
                .addTag(ModTags.Items.INGOTS_NICKEL_D.common())
                .addTag(ModTags.Items.INGOTS_INVAR_D.common())
                .addTag(ModTags.Items.INGOTS_BLAZING_GOLD_D.common())
                .addTag(ModTags.Items.INGOTS_STEEL_D.common());

        var catalystsBoth = ModTags.Items.CATALYSTS_D.both();
        tag(catalystsBoth[0])
                .addTag(ModTags.Items.CATALYST_BLAZE.neoforge()
                );
        tag(catalystsBoth[1])
                .addTag(ModTags.Items.CATALYST_BLAZE.common());

        var dustsBoth = ModTags.Items.DUSTS_D.both();
        tag(dustsBoth[0])
                .addTag(ModTags.Items.IRON_DUST_D.neoforge())
                .addTag(ModTags.Items.COPPER_DUST_D.neoforge())
                .addTag(ModTags.Items.NICKEL_DUST_D.neoforge())
                .addTag(ModTags.Items.DIAMOND_DUST_D.neoforge())
                .addTag(ModTags.Items.TIN_DUST_D.neoforge());
        tag(dustsBoth[1])
                .addTag(ModTags.Items.IRON_DUST_D.common())
                .addTag(ModTags.Items.COPPER_DUST_D.common())
                .addTag(ModTags.Items.NICKEL_DUST_D.common())
                .addTag(ModTags.Items.DIAMOND_DUST_D.common())
                .addTag(ModTags.Items.TIN_DUST_D.common());

        var rawMaterialsBoth = ModTags.Items.RAW_MATERIALS_D.both();
        tag(rawMaterialsBoth[0])
                .addTag(ModTags.Items.RAW_TIN_D.neoforge())
                .addTag(ModTags.Items.RAW_LEAD_D.neoforge())
                .addTag(ModTags.Items.RAW_NICKEL_D.neoforge());
        tag(rawMaterialsBoth[1])
                .addTag(ModTags.Items.RAW_LEAD_D.common())
                .addTag(ModTags.Items.RAW_TIN_D.common())
                .addTag(ModTags.Items.RAW_NICKEL_D.common());

    }

}
