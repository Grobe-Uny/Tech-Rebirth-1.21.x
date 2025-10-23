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

public class CrushingRecipeBuilder implements RecipeBuilder {

    private final Ingredient ingredient;
    private final ItemStack result;
    private int time = 72;
    private ItemStack chanceOutput = ItemStack.EMPTY;  // ← NOVO
    private float chanceRate = 0.0f;

    // store unlock criteria like vanilla builders
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    private CrushingRecipeBuilder(Ingredient ingredient, ItemStack result){
        this.ingredient = ingredient;
        this.result = result;
    }

    public static CrushingRecipeBuilder crushing(Ingredient ingredient, ItemStack result) {
        return new CrushingRecipeBuilder(ingredient, result);
    }

    public CrushingRecipeBuilder time(int ticks) {
        this.time = ticks;
        return this;
    }
    public CrushingRecipeBuilder chanceOutput(ItemStack output, float rate){
        this.chanceOutput = (output == null) ? ItemStack.EMPTY : output.copy();
        this.chanceRate = rate;
        return this;
    }
    public CrushingRecipeBuilder chanceOutput(Item output, float rate) {
        return chanceOutput(new ItemStack(output), rate);
    }


    @Override
    public CrushingRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    @Override
    public void save(RecipeOutput output, ResourceLocation id){
        // Construct the concrete recipe instance (uses your codec serializer)

        ItemStack finalChanceOutput = (chanceOutput == null || chanceOutput.isEmpty()) ? ItemStack.EMPTY : chanceOutput.copy();
        CrushingRecipe recipe = new CrushingRecipe(ingredient, result.copy(), time, finalChanceOutput, chanceRate);
        // Publish the recipe without an advancement (keep simple/minimal)
        output.accept(id, recipe, null);
    }

    // Optional grouping API required by RecipeBuilder in 1.21
    @Override
    public CrushingRecipeBuilder group(@Nullable String groupName) {
        // No-op grouping; return this for chaining
        return this;
    }

    @Override
    public Item getResult() {
        return this.result.getItem();
    }
}
