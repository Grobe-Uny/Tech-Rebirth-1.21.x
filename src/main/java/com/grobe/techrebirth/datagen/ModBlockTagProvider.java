package com.grobe.techrebirth.datagen;

import com.grobe.techrebirth.TechRebirth;
import com.grobe.techrebirth.block.ModBlocks;
import com.grobe.techrebirth.util.MetalType;
import com.grobe.techrebirth.util.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,  @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, TechRebirth.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        // Lead ore tags in both common and neoforge namespaces via Dual helper
        for (var tagKey : ModTags.Blocks.ORES_LEAD_D.both()) {
            tag(tagKey)
                .add(ModBlocks.LEAD_ORE.get())
                .add(ModBlocks.LEAD_DEEPSLATE_ORE.get());
        }

        // Mining tool and tier requirements
        var pickaxeTag = tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.MACHINE_BASE.get())
                .add(ModBlocks.ELECTRIC_FURNACE.get())
                .add(ModBlocks.NICKEL_DEEPSLATE_ORE.get())
                .add(ModBlocks.LEAD_DEEPSLATE_ORE.get())
                .add(ModBlocks.LEAD_ORE.get())
                .add(ModBlocks.NICKEL_ORE.get())
                .add(ModBlocks.TIN_ORE.get())
                .add(ModBlocks.TIN_DEEPSLATE_ORE.get())
                //.add(ModBlocks.INVAR_BLOCK.get())
                .add(ModBlocks.ENERGY_BANK.get())
                .add(ModBlocks.GENERATOR.get())
                .add(ModBlocks.ENERGY_CABLE.get())
                .add(ModBlocks.ALLOY_SMELTER.get())
                .add(ModBlocks.HARDENED_ALLOY_SMELTER.get())
                .add(ModBlocks.HARDENED_ELECTRIC_FURNACE.get())
                .add(ModBlocks.REINFORCED_ELECTRIC_FURNACE.get())
                .add(ModBlocks.ELECTRIC_CENTRIFUGE.get())
                .add(ModBlocks.FLUID_TANK.get())
                .add(ModBlocks.ELECTRIC_CRUSHER.get())
                .add(ModBlocks.ELECTRIC_PURIFIER.get());


        tag(BlockTags.NEEDS_STONE_TOOL)
                .add(ModBlocks.NICKEL_ORE.get())
                .add(ModBlocks.LEAD_ORE.get())
                .add(ModBlocks.TIN_ORE.get())
                .add(ModBlocks.ENERGY_CABLE.get());
        var ironToolTag = tag(BlockTags.NEEDS_IRON_TOOL)
                .add(ModBlocks.TIN_DEEPSLATE_ORE.get())
                .add(ModBlocks.NICKEL_DEEPSLATE_ORE.get())
                .add(ModBlocks.LEAD_DEEPSLATE_ORE.get())
                //.add(ModBlocks.INVAR_BLOCK.get())
                .add(ModBlocks.ELECTRIC_FURNACE.get())
                .add(ModBlocks.MACHINE_BASE.get())
                .add(ModBlocks.ENERGY_BANK.get())
                .add(ModBlocks.GENERATOR.get())
                .add(ModBlocks.ALLOY_SMELTER.get())
                .add(ModBlocks.HARDENED_ALLOY_SMELTER.get())
                .add(ModBlocks.ELECTRIC_CRUSHER.get())
                .add(ModBlocks.HARDENED_ELECTRIC_FURNACE.get())
                .add(ModBlocks.FLUID_TANK.get())
                .add(ModBlocks.ELECTRIC_CENTRIFUGE.get())
                .add(ModBlocks.ELECTRIC_PURIFIER.get())
                .add(ModBlocks.REINFORCED_ELECTRIC_FURNACE.get());

        // Automatically add tags for generated ore blocks
        for (Map.Entry<MetalType, DeferredBlock<Block>> entry : ModBlocks.ORE_BLOCKS.entrySet()) {
            if (entry.getKey() == MetalType.DIAMOND) continue; // Skip Diamond Block tags
            pickaxeTag.add(entry.getValue().get());
            ironToolTag.add(entry.getValue().get());
        }
    }
}
