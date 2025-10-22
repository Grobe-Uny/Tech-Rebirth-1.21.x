package com.grobe.techrebirth.datagen;

import com.grobe.techrebirth.TechRebirth;
import com.grobe.techrebirth.item.ModItems;
import com.grobe.techrebirth.util.ModTags;
import com.grobe.techrebirth.block.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
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
        // Add our ingots to both c: and neoforge: ingot tags via Dual helpers
        for (var tagKey : ModTags.Items.INGOTS_LEAD_D.both())   tag(tagKey).add(ModItems.LEAD_INGOT.get());
        for (var tagKey : ModTags.Items.INGOTS_TIN_D.both())    tag(tagKey).add(ModItems.TIN_INGOT.get());
        for (var tagKey : ModTags.Items.INGOTS_NICKEL_D.both()) tag(tagKey).add(ModItems.NICKEL_INGOT.get());
        for (var tagKey : ModTags.Items.INGOTS_INVAR_D.both())  tag(tagKey).add(ModItems.INVAR_INGOT.get());
        for (var tagKey : ModTags.Items.INGOTS_STEEL_D.both())   tag(tagKey).add(ModItems.STEEL_INGOT.get());

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
                .addTag(ModTags.Items.INGOTS_STEEL_D.neoforge());
        tag(ingotsBoth[1])
                .addTag(ModTags.Items.INGOTS_LEAD_D.common())
                .addTag(ModTags.Items.INGOTS_TIN_D.common())
                .addTag(ModTags.Items.INGOTS_NICKEL_D.common())
                .addTag(ModTags.Items.INGOTS_INVAR_D.common())
                .addTag(ModTags.Items.INGOTS_STEEL_D.common());
    }

}
