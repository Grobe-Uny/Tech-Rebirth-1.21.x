package com.grobe.techrebirth.recipe;

import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public class FluidInfuserRecipeBuilder implements RecipeBuilder {
    private final Ingredient ingredient;
    private final FluidStack fluidInput;
    private final ItemStack result;
    private int time;
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    private FluidInfuserRecipeBuilder(Ingredient ingredient, FluidStack fluidInput, ItemStack result) {
        this.ingredient = ingredient;
        this.fluidInput = fluidInput;
        this.result = result;
        this.time = 100;
    }

    public static FluidInfuserRecipeBuilder infusing(Ingredient ingredient, FluidStack fluidInput, ItemStack result) {
        return new FluidInfuserRecipeBuilder(ingredient, fluidInput, result);
    }

    public FluidInfuserRecipeBuilder time(int ticks) {
        this.time = ticks;
        return this;
    }

    @Override
    public FluidInfuserRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    @Override
    public FluidInfuserRecipeBuilder group(@Nullable String groupName) {
        return this;
    }

    @Override
    public Item getResult() {
        return result.getItem();
    }

    @Override
    public void save(RecipeOutput output, ResourceLocation id) {
        FluidInfuserRecipe recipe = new FluidInfuserRecipe(ingredient, fluidInput, result, time);
        output.accept(id, recipe, null);
    }
}
