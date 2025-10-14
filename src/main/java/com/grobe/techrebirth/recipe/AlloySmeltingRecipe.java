package com.grobe.techrebirth.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.*;

public class AlloySmeltingRecipe implements Recipe<MultiItemRecipeInput> {
    protected final NonNullList<Ingredient> ingredients;
    protected final ItemStack result;
    protected final int cookingTime;

    public AlloySmeltingRecipe(NonNullList<Ingredient> ingredients, ItemStack result, int cookingTime) {
        this.ingredients = ingredients;
        this.result = result;
        this.cookingTime = cookingTime;
    }

    @Override
    public boolean matches(MultiItemRecipeInput input, Level level) {
        // Prebroji potrebne ingrediente
        Map<Ingredient, Integer> requiredIngredients = new HashMap<>();
        for (Ingredient ingredient : ingredients) {
            requiredIngredients.put(ingredient, requiredIngredients.getOrDefault(ingredient, 0) + 1);
        }

        // Prebroji dostupne iteme
        Map<Ingredient, Integer> availableItems = new HashMap<>();
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) continue;

            for (Ingredient ingredient : ingredients) {
                if (ingredient.test(stack)) {
                    availableItems.put(ingredient, availableItems.getOrDefault(ingredient, 0) + stack.getCount());
                    break;
                }
            }
        }

        // Provjeri ima li dovoljno svakog ingredienta
        for (Map.Entry<Ingredient, Integer> entry : requiredIngredients.entrySet()) {
            Ingredient ingredient = entry.getKey();
            int requiredCount = entry.getValue();
            int availableCount = availableItems.getOrDefault(ingredient, 0);

            if (availableCount < requiredCount) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack assemble(MultiItemRecipeInput input, HolderLookup.Provider provider) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 3;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.ALLOY_SMELTING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.ALLOY_SMELTING_TYPE.get();
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    // Serializer
    public static class Serializer implements RecipeSerializer<AlloySmeltingRecipe> {
        private static final MapCodec<AlloySmeltingRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").xmap(
                                list -> {
                                    NonNullList<Ingredient> nonNullList = NonNullList.create();
                                    nonNullList.addAll(list);
                                    return nonNullList;
                                },
                                nonNullList -> new ArrayList<>(nonNullList)
                        ).forGetter(recipe -> recipe.ingredients),
                        ItemStack.CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
                        Codec.INT.optionalFieldOf("cookingTime", 200).forGetter(recipe -> recipe.cookingTime)
                ).apply(instance, AlloySmeltingRecipe::new)
        );

        private static final StreamCodec<RegistryFriendlyByteBuf, AlloySmeltingRecipe> STREAM_CODEC = StreamCodec.of(
                AlloySmeltingRecipe.Serializer::toNetwork,
                AlloySmeltingRecipe.Serializer::fromNetwork
        );

        @Override
        public MapCodec<AlloySmeltingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, AlloySmeltingRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, AlloySmeltingRecipe recipe) {
            buffer.writeVarInt(recipe.ingredients.size());
            for (Ingredient ingredient : recipe.ingredients) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
            }
            ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
            buffer.writeVarInt(recipe.cookingTime);
        }

        private static AlloySmeltingRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            int ingredientCount = buffer.readVarInt();
            NonNullList<Ingredient> ingredients = NonNullList.withSize(ingredientCount, Ingredient.EMPTY);
            for (int i = 0; i < ingredientCount; i++) {
                ingredients.set(i, Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
            }
            ItemStack result = ItemStack.STREAM_CODEC.decode(buffer);
            int cookingTime = buffer.readVarInt();
            return new AlloySmeltingRecipe(ingredients, result, cookingTime);
        }
    }
}