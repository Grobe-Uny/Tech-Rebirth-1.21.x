package com.grobe.techrebirth.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.ArrayList;
import java.util.List;

public class MultiItemRecipeInput implements RecipeInput {
    private final List<ItemStack> items;

    public MultiItemRecipeInput(ItemStack... items) {
        this.items = new ArrayList<>();
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                this.items.add(stack);
            }
        }
    }

    @Override
    public ItemStack getItem(int index) {
        return index >= 0 && index < items.size() ? items.get(index) : ItemStack.EMPTY;
    }

    @Override
    public int size() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        return items.isEmpty();
    }

    public List<ItemStack> getItems() {
        return items;
    }
}