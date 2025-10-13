package com.grobe.techrebirth.recipe;

import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.LinkedHashMap;
import java.util.Map;

public class GeneratorFuelRecipeBuilder implements RecipeBuilder {

    private final Ingredient ingredient;
    private final int burn;
    private final int perTick;
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    private GeneratorFuelRecipeBuilder(Ingredient ingredient, int burnTime, int powerPerTick) {
        this.ingredient = ingredient;
        this.burn = burnTime;
        this.perTick = powerPerTick;
    }

    public static GeneratorFuelRecipeBuilder fuel(Ingredient ingredient, int burnTime, int powerPerTick) {
        return new GeneratorFuelRecipeBuilder(ingredient, burnTime, powerPerTick);
    }

    @Override
    public GeneratorFuelRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        criteria.put(name, criterion);
        return this;
    }

    @Override
    public void save(RecipeOutput out, ResourceLocation id) {
        out.accept(id, new GeneratorFuelRecipe(ingredient, burn, perTick), null);
    }

    @Override
    public GeneratorFuelRecipeBuilder group(String group) { return this; }

    @Override
    public net.minecraft.world.item.Item getResult() { return net.minecraft.world.item.Items.AIR; }
}
