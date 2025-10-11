package com.grobe.techrebirth.datagen;

import com.grobe.techrebirth.TechRebirth;
import com.grobe.techrebirth.enchantment.ModEnchantments;
import com.grobe.techrebirth.worldgen.ModBiomeModifiers;
import com.grobe.techrebirth.worldgen.ModConfiguredFeatures;
import com.grobe.techrebirth.worldgen.ModPlacedFeatures;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ModDatapackProvider extends DatapackBuiltinEntriesProvider {
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            //  Enchantment Registration
            .add(Registries.ENCHANTMENT, ModEnchantments::bootstrap)
            //  Worldgen Registration
            .add(Registries.CONFIGURED_FEATURE, ModConfiguredFeatures::Bootstrap)
            .add(Registries.PLACED_FEATURE, ModPlacedFeatures::Bootstrap)
            .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ModBiomeModifiers::Bootstrap);



    public ModDatapackProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries){
        super(output, registries, BUILDER, Set.of(TechRebirth.MODID));
    }

}
