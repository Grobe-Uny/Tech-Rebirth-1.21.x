package com.grobe.techrebirth.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.fluids.FluidStack;

public class FluidInfuserRecipeInput implements RecipeInput {
    private final ItemStack item;
    private final FluidStack fluid;

    public FluidInfuserRecipeInput(ItemStack item, FluidStack fluid) {
        this.item = item;
        this.fluid = fluid;
    }

    @Override
    public ItemStack getItem(int index) {
        return index == 0 ? item : ItemStack.EMPTY;
    }

    @Override
    public int size() { return 1; }

    public ItemStack getItemStack() { return item; }
    public FluidStack getFluidStack() { return fluid; }
}