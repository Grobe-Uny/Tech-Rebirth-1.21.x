package com.grobe.techrebirth.recipe;

import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public class PurifierRecipeBuilder implements RecipeBuilder {

    private final Ingredient ingredient;
    private final ItemStack result;
    private int time = 100;

    // store unlock criteria like vanilla builders
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    private PurifierRecipeBuilder(Ingredient ingredient, ItemStack result){
        this.ingredient = ingredient;
        this.result = result;
    }

    public static PurifierRecipeBuilder purifying(Ingredient ingredient, ItemStack result) {
        return new PurifierRecipeBuilder(ingredient, result);
    }

    public PurifierRecipeBuilder time(int ticks) {
        this.time = ticks;
        return this;
    }


    @Override
    public PurifierRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    @Override
    public void save(RecipeOutput output, ResourceLocation id){
        // Construct the concrete recipe instance (uses your codec serializer)

        PurifierRecipe recipe = new PurifierRecipe(ingredient, result.copy(), time);
        // Publish the recipe without an advancement (keep simple/minimal)
        output.accept(id, recipe, null);
    }

    // Optional grouping API required by RecipeBuilder in 1.21
    @Override
    public PurifierRecipeBuilder group(@Nullable String groupName) {
        // No-op grouping; return this for chaining
        return this;
    }

    @Override
    public Item getResult() {
        return this.result.getItem();
    }
}
