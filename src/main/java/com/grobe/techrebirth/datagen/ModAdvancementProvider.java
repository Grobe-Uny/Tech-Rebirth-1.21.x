package com.grobe.techrebirth.datagen;

import com.grobe.techrebirth.TechRebirth;
import com.grobe.techrebirth.block.ModBlocks;
import com.grobe.techrebirth.item.ModItems;
import com.grobe.techrebirth.item.ModToolItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ModAdvancementProvider extends AdvancementProvider {
    /**
     * Constructs an advancement provider using the generators to write the
     * advancements to a file.
     *
     * @param output             the target directory of the data generator
     * @param registries         a future of a lookup for registries and their objects
     * @param existingFileHelper a helper used to find whether a file exists
     */
    public ModAdvancementProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper existingFileHelper) {
        super(output, registries, existingFileHelper, List.of(new ModAdvancementGenerator()));
    }

    private static final class ModAdvancementGenerator implements AdvancementGenerator{
        @Override
        public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> saver, ExistingFileHelper existingFileHelper){

            boolean isHidden = FMLLoader.isProduction();
            AdvancementHolder root = Advancement.Builder.advancement()
                    .display(
                            ModItems.TIN_INGOT.get(),
                            Component.translatable("advancements.techrebirth.rebirth_of_technology.title"),
                            Component.translatable("advancements.techrebirth.rebirth_of_technology.description"),
                            ResourceLocation.withDefaultNamespace("textures/gui/advancements/backgrounds/stone.png"),
                            AdvancementType.TASK,
                            true,true,false
                    ).addCriterion("has_tin", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.TIN_INGOT))
                    .save(saver, ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "tech_rebirth_start"), existingFileHelper);

            AdvancementHolder beginning = Advancement.Builder.advancement()
                    .display(
                            ModBlocks.MACHINE_BASE.get(),
                            Component.translatable("advancements.techrebirth.basing_machines.title"),
                            Component.translatable("advancements.techrebirth.basing_machines.description"),
                            ResourceLocation.withDefaultNamespace("textures/gui/advancements/backgrounds/stone.png"),
                            AdvancementType.TASK,
                            true,true,false
                    ).addCriterion("has_machine_base", InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.MACHINE_BASE))
                    .parent(root)
                    .save(saver, ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "beggining_of_machinery"), existingFileHelper);

            AdvancementHolder generating_electricity = Advancement.Builder.advancement()
                    .display(
                            ModBlocks.GENERATOR.get(),
                            Component.translatable("advancements.techrebirth.first_sparks.title"),
                            Component.translatable("advancements.techrebirth.first_sparks.description"),
                            ResourceLocation.withDefaultNamespace("textures/gui/advancements/backgrounds/stone.png"),
                            AdvancementType.TASK,
                            true,true,false
                    ).addCriterion("has_generator", InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.GENERATOR))
                    .parent(beginning)
                    .save(saver, ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "sparks_of_electricity"), existingFileHelper);

            AdvancementHolder start_of_development = Advancement.Builder.advancement()
                    .display(
                            ModBlocks.ELECTRIC_CENTRIFUGE,
                            Component.translatable("advancements.techrebirth.start_of_development.title"),
                            Component.translatable("advancements.techrebirth.start_of_development.description"),
                            ResourceLocation.withDefaultNamespace("textures/gui/advancements/backgrounds/stone.png"),
                            AdvancementType.TASK,
                            true, true,isHidden
                    ).addCriterion("has_electric_centrifuge", InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.ELECTRIC_CENTRIFUGE))
                    .parent(generating_electricity)
                    .save(saver, ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "start_mixing_stuff"), existingFileHelper);

            AdvancementHolder get_blazed = Advancement.Builder.advancement()
                    .display(
                            ModItems.BLAZING_GOLD_INGOT.get(),
                            Component.translatable("advancements.techrebirth.start_blazing.title"),
                            Component.translatable("advancements.techrebirth.start_blazing.description"),
                            ResourceLocation.withDefaultNamespace("textures/gui/advancements/backgrounds/stone.png"),
                            AdvancementType.TASK,
                            true, true,isHidden
                    ).addCriterion("has_blazing_gold", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.BLAZING_GOLD_INGOT))
                    .parent(start_of_development)
                    .save(saver, ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "getting_blazed_with_ingots"), existingFileHelper);

            AdvancementHolder get_tooled = Advancement.Builder.advancement()
                    .display(
                            ModToolItems.BLAZING_GOLD_PICKAXE,
                            Component.translatable("advancements.techrebirth.get_tooled.title"),
                            Component.translatable("advancements.techrebirth.get_tooled.description"),
                            ResourceLocation.withDefaultNamespace("textures/gui/advancements/backgrounds/stone.png"),
                            AdvancementType.TASK,
                            true, true,isHidden
                    ).addCriterion("has_blazing_gold_tools", InventoryChangeTrigger.TriggerInstance.hasItems(ModToolItems.BLAZING_GOLD_PICKAXE))
                    .parent(get_blazed)
                    .save(saver, ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "get_blazed_tools"), existingFileHelper);

            AdvancementHolder more_less = Advancement.Builder.advancement()
                    .display(
                            ModBlocks.ELECTRIC_PURIFIER,
                            Component.translatable("advancements.techrebirth.more_less.title"),
                            Component.translatable("advancements.techrebirth.more_less.description"),
                            ResourceLocation.withDefaultNamespace("textures/gui/advancements/backgrounds/stone.png"),
                            AdvancementType.CHALLENGE,
                            true, true,isHidden
                    ).addCriterion("has_electric_purifier", InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.ELECTRIC_PURIFIER))
                    .parent(start_of_development)
                    .save(saver, ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "more_less"), existingFileHelper);
        }
    }
}
