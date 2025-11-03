package com.grobe.techrebirth.recipe;

import com.grobe.techrebirth.block.ModBlocks;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;

public class CentrifugeRecipeBuilder implements RecipeBuilder {
    private final Ingredient input;
    private final Ingredient catalyst;
    private final int catalystAmount;
    private final ItemStack output;
    private final int processingTime;
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    private CentrifugeRecipeBuilder(Ingredient input, Ingredient catalyst, int catalystAmount, ItemStack output, int processingTime) {
        this.input = input;
        this.catalyst = catalyst;
        this.catalystAmount = catalystAmount;
        this.output = output;
        this.processingTime = processingTime;
    }

    public static CentrifugeRecipeBuilder centrifuging(Ingredient input, Ingredient catalyst, int catalystAmount, ItemStack output, int processingTime) {
        return new CentrifugeRecipeBuilder(input, catalyst, catalystAmount, output, processingTime);
    }

    @Override
    public RecipeBuilder unlockedBy(String name, Criterion criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    @Override
    public RecipeBuilder group(@Nullable String groupName) {
        return this;
    }

    @Override
    public Item getResult() {
        return this.output.getItem();
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceLocation id) {
        Advancement.Builder advancement = recipeOutput.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(advancement::addCriterion);

        recipeOutput.accept(id, new CentrifugeRecipe(this.input, this.catalyst, this.catalystAmount, this.output, this.processingTime),
                advancement.build(id.withPrefix("recipes/")));
    }
}
