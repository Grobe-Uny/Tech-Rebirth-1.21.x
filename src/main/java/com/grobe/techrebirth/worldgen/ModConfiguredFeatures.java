package com.grobe.techrebirth.worldgen;

import com.grobe.techrebirth.TechRebirth;
import com.grobe.techrebirth.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;

import java.util.List;

public class ModConfiguredFeatures {

    public static final ResourceKey<ConfiguredFeature<?,?>> TIN_ORE_KEY = registerName("tin_ore");
    public static final ResourceKey<ConfiguredFeature<?,?>> NICKEL_ORE_KEY = registerName("nickel_ore");
    public static final ResourceKey<ConfiguredFeature<?,?>> LEAD_ORE_KEY = registerName("lead_ore");

    public static void Bootstrap(BootstrapContext<ConfiguredFeature<?,?>> context){
        RuleTest stoneReplacable = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
        RuleTest deepslateReplacable = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);

        List<OreConfiguration.TargetBlockState> tinOres = List.of(OreConfiguration.target(stoneReplacable, ModBlocks.TIN_ORE.get().defaultBlockState()),
                OreConfiguration.target(deepslateReplacable, ModBlocks.TIN_DEEPSLATE_ORE.get().defaultBlockState()));
        List<OreConfiguration.TargetBlockState> nickelOres = List.of(OreConfiguration.target(stoneReplacable, ModBlocks.NICKEL_ORE.get().defaultBlockState()),
                OreConfiguration.target(deepslateReplacable, ModBlocks.NICKEL_DEEPSLATE_ORE.get().defaultBlockState()));
        List<OreConfiguration.TargetBlockState> leadOres = List.of(OreConfiguration.target(stoneReplacable, ModBlocks.LEAD_ORE.get().defaultBlockState()),
                OreConfiguration.target(deepslateReplacable, ModBlocks.LEAD_DEEPSLATE_ORE.get().defaultBlockState()));

        register(context, TIN_ORE_KEY, Feature.ORE, new OreConfiguration(tinOres, 8));
        register(context, NICKEL_ORE_KEY, Feature.ORE, new OreConfiguration(nickelOres, 8));
        register(context, LEAD_ORE_KEY, Feature.ORE, new OreConfiguration(leadOres, 8));

    }


    public static ResourceKey<ConfiguredFeature<?, ?>> registerName (String name){
        return  ResourceKey.create(Registries.CONFIGURED_FEATURE,  ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, name));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register (BootstrapContext<ConfiguredFeature<?,?>> context,
                                                                                           ResourceKey<ConfiguredFeature<?,?>>key , F feature, FC configuration){
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }


}
