package com.grobe.techrebirth.recipe;

import com.grobe.techrebirth.TechRebirth;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

public class CrushingRecipe implements Recipe<SingleRecipeInput> {
    private final Ingredient ingredient;
    private final ItemStack result;
    private final int time;

    public CrushingRecipe(Ingredient ingredient, ItemStack result, int time) {
        this.ingredient = ingredient;
        this.result = result;
        this.time = time;
    }

    public Ingredient getIngredient() { return ingredient; }
    public ItemStack getResult() { return result; }
    public int getTime() { return time; }

    @Override
    public boolean matches(SingleRecipeInput container, Level level) {
        return ingredient.test(container.item());
    }

    @Override
    public ItemStack assemble(SingleRecipeInput container, net.minecraft.core.HolderLookup.Provider provider) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) { return true; }

    @Override
    public ItemStack getResultItem(net.minecraft.core.HolderLookup.Provider provider) { return result; }


    @Override
    public RecipeSerializer<?> getSerializer() { return ModRecipeTypes.CRUSHING_SERIALIZER.get(); }

    @Override
    public RecipeType<?> getType() { return ModRecipeTypes.CRUSHING_TYPE.get(); }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(ingredient);
        return list;
    }

    public static class Serializer implements RecipeSerializer<CrushingRecipe> {
        @Override
        public com.mojang.serialization.MapCodec<CrushingRecipe> codec() {
            return RecordCodecBuilder.mapCodec(instance -> instance.group(
                    net.minecraft.world.item.crafting.Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(r -> r.ingredient),
                    net.minecraft.world.item.ItemStack.CODEC.fieldOf("result").forGetter(r -> r.result),
                    com.mojang.serialization.Codec.INT.optionalFieldOf("time", 72).forGetter(r -> r.time)
            ).apply(instance, CrushingRecipe::new));
        }

        @Override
        public net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, CrushingRecipe> streamCodec() {
            return net.minecraft.network.codec.StreamCodec.composite(
                    net.minecraft.world.item.crafting.Ingredient.CONTENTS_STREAM_CODEC, (CrushingRecipe r) -> r.ingredient,
                    net.minecraft.world.item.ItemStack.STREAM_CODEC, (CrushingRecipe r) -> r.result,
                    net.minecraft.network.codec.ByteBufCodecs.VAR_INT, (CrushingRecipe r) -> r.time,
                    CrushingRecipe::new
            );
        }
    }
}
