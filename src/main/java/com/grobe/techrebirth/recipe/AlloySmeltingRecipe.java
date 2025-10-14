package com.grobe.techrebirth.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class AlloySmeltingRecipe implements Recipe<CraftingInput> {
    protected final NonNullList<Ingredient> ingredients;
    protected final ItemStack result;
    protected final int cookingTime;

    public AlloySmeltingRecipe(NonNullList<Ingredient> ingredients, ItemStack result, int cookingTime) {
        this.ingredients = ingredients;
        this.result = result;
        this.cookingTime = cookingTime;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        // Ovo ćeš implementirati za pattern matching s 3 inputa
        return RecipeUtils.matchesShapeless(input, ingredients);
    }
    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider provider) {
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

    public static class Serializer implements RecipeSerializer<AlloySmeltingRecipe> {
        @Override
        public MapCodec<AlloySmeltingRecipe> codec() {
            return RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(r -> r.ingredients),
                    ItemStack.CODEC.fieldOf("result").forGetter(r -> r.result),
                    Codec.INT.optionalFieldOf("time", 72).forGetter(r -> r.cookingTime)
            ).apply(instance, CrushingRecipe::new));
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, AlloySmeltingRecipe> streamCodec() {
            return StreamCodec.composite(
                    Ingredient.CONTENTS_STREAM_CODEC, (CrushingRecipe r) -> r.getIngredients(),
                    ItemStack.STREAM_CODEC, (CrushingRecipe r) -> r.getResult(),
                    ByteBufCodecs.VAR_INT, (CrushingRecipe r) -> r.getTime(),
                    CrushingRecipe::new
            );
        }
    }

}
