package com.grobe.techrebirth.datagen;

import com.grobe.techrebirth.TechRebirth;
import com.grobe.techrebirth.item.ModItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
            Advancement.Builder builder = Advancement.Builder.advancement();
            AdvancementHolder root = builder.display(
                                    ModItems.TIN_INGOT.get(),
                                    Component.translatable("advancements.techrebirth.rebirth_of_technology.title"),
                                    Component.translatable("advancements.techrebirth.rebirth_of_technology.description"),
                                    ResourceLocation.withDefaultNamespace("textures/gui/advancements/backgrounds/stone.png"),
                                    AdvancementType.TASK,
                                    true,true,false
                            ).addCriterion("has_tin", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.TIN_INGOT))
                    .save(saver, ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "tech_rebirth_start"), existingFileHelper);
        }
    }
}
