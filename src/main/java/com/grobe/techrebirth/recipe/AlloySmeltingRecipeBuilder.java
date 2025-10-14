package com.grobe.techrebirth.recipe;

import net.minecraft.advancements.Criterion;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public class AlloySmeltingRecipeBuilder implements RecipeBuilder {
    protected final NonNullList<Ingredient> ingredients;
    private final ItemStack result;
    private int cookingTime;

    // store unlock criteria like vanilla builders
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    private AlloySmeltingRecipeBuilder(NonNullList<Ingredient> ingredient, ItemStack result){
        this.ingredients = ingredient;
        this.result = result;
    }

    public static AlloySmeltingRecipeBuilder AlloySmelting(NonNullList<Ingredient> ingredient, ItemStack result) {
        return new AlloySmeltingRecipeBuilder(ingredient, result);
    }

    public AlloySmeltingRecipeBuilder time(int ticks) {
        this.cookingTime = ticks;
        return this;
    }

    @Override
    public AlloySmeltingRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    @Override
    public void save(RecipeOutput output, ResourceLocation id){
        // Construct the concrete recipe instance (uses your codec serializer)
        AlloySmeltingRecipe recipe = new AlloySmeltingRecipe(ingredients, result.copy(), cookingTime);
        // Publish the recipe without an advancement (keep simple/minimal)
        output.accept(id, recipe, null);
    }

    // Optional grouping API required by RecipeBuilder in 1.21
    @Override
    public AlloySmeltingRecipeBuilder group(@Nullable String groupName) {
        // No-op grouping; return this for chaining
        return this;
    }

    @Override
    public Item getResult() {
        return this.result.getItem();
    }
}
