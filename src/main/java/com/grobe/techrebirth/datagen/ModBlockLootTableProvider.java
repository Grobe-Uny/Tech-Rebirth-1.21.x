package com.grobe.techrebirth.datagen;

import com.grobe.techrebirth.block.ModBlocks;
import com.grobe.techrebirth.item.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.fml.common.Mod;

import java.util.Set;

public class ModBlockLootTableProvider extends BlockLootSubProvider {
    protected ModBlockLootTableProvider(HolderLookup.Provider pRegistries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), pRegistries);
    }

    @Override
    protected void generate() {
        for (var ore : ModBlocks.ORE_BLOCKS.values()){
            dropSelf(ore.get());
        }
        dropSelf(ModBlocks.MACHINE_BASE.get());
        dropSelf(ModBlocks.ELECTRIC_FURNACE.get());
        dropSelf(ModBlocks.ENERGY_BANK.get());
        dropSelf(ModBlocks.GENERATOR.get());
        dropSelf(ModBlocks.ENERGY_CABLE.get());
        dropSelf(ModBlocks.ELECTRIC_CRUSHER.get());
        dropSelf(ModBlocks.ALLOY_SMELTER.get());
        dropSelf(ModBlocks.HARDENED_ALLOY_SMELTER.get());
        dropSelf(ModBlocks.HARDENED_ELECTRIC_FURNACE.get());
        dropSelf(ModBlocks.REINFORCED_ELECTRIC_FURNACE.get());
        dropSelf(ModBlocks.ELECTRIC_CENTRIFUGE.get());
        dropSelf(ModBlocks.CRUCIBLE.get());
        dropSelf(ModBlocks.ELECTRIC_PURIFIER.get());
        dropSelf(ModBlocks.FLUID_INFUSER.get());

        dropSelf(ModBlocks.FLUID_TANK.get());

        add(ModBlocks.CREATIVE_ELECTRIC_FURNACE.get(), block -> LootTable.lootTable());

        add(ModBlocks.NICKEL_ORE.get(),
                block -> createOreDrop(ModBlocks.NICKEL_ORE.get(), ModItems.RAW_NICKEL.get()));
        add(ModBlocks.NICKEL_DEEPSLATE_ORE.get(),
                block -> createOreDrop(ModBlocks.NICKEL_DEEPSLATE_ORE.get(), ModItems.RAW_NICKEL.get()));

        add(ModBlocks.LEAD_ORE.get(),
                block -> createOreDrop(ModBlocks.LEAD_ORE.get(), ModItems.RAW_LEAD.get()));
        add(ModBlocks.LEAD_DEEPSLATE_ORE.get(),
                block -> createOreDrop(ModBlocks.LEAD_DEEPSLATE_ORE.get(), ModItems.RAW_LEAD.get()));
        add(ModBlocks.TIN_ORE.get(),
                block -> createOreDrop(ModBlocks.TIN_ORE.get(), ModItems.RAW_TIN.get()));
        add(ModBlocks.TIN_DEEPSLATE_ORE.get(),
                block -> createOreDrop(ModBlocks.TIN_DEEPSLATE_ORE.get(), ModItems.RAW_TIN.get()));

        add(ModBlocks.LITHIUM_ORE.get(),
                block -> createOreDrop(ModBlocks.LITHIUM_ORE.get(), ModItems.LITHIUM_POWDER.get()));
    }

    @Override
    protected Iterable<Block> getKnownBlocks(){
        return ModBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }



}
