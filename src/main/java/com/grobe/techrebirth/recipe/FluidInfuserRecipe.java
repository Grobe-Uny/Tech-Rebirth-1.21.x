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
import net.neoforged.neoforge.fluids.FluidStack;

public class FluidInfuserRecipe implements Recipe<SingleRecipeInput> {
    private final Ingredient ingredient;
    private final FluidStack fluidInput;
    private final ItemStack result;
    private final int time;

    public FluidInfuserRecipe(Ingredient ingredient, FluidStack fluidInput, ItemStack result, int time) {
        this.ingredient = ingredient;
        this.fluidInput = fluidInput;
        this.result = result;
        this.time = time;
    }

    public Ingredient getIngredient() { return ingredient; }
    public FluidStack getFluidInput() { return fluidInput; }
    public ItemStack getResult() { return result; }
    public int getTime() { return time; }

    @Override
    public boolean matches(SingleRecipeInput container, Level level) {
        return ingredient.test(container.item());
    }

    @Override
    public ItemStack assemble(SingleRecipeInput container, HolderLookup.Provider provider) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) { return true; }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) { return result; }

    @Override
    public RecipeSerializer<?> getSerializer() { return ModRecipeTypes.INFUSER_SERIALIZER.get(); }

    @Override
    public RecipeType<?> getType() { return ModRecipeTypes.INFUSER_TYPE.get(); }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(ingredient);
        return list;
    }

    public static class Serializer implements RecipeSerializer<FluidInfuserRecipe> {
        @Override
        public MapCodec<FluidInfuserRecipe> codec() {
            return RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(r -> r.ingredient),
                    FluidStack.CODEC.fieldOf("fluid_input").forGetter(r -> r.fluidInput),
                    ItemStack.CODEC.fieldOf("result").forGetter(r -> r.result),
                    Codec.INT.optionalFieldOf("time", 100).forGetter(r -> r.time)
            ).apply(instance, FluidInfuserRecipe::new));
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, FluidInfuserRecipe> streamCodec() {
            return StreamCodec.composite(
                    Ingredient.CONTENTS_STREAM_CODEC, (FluidInfuserRecipe r) -> r.ingredient,
                    FluidStack.STREAM_CODEC, (FluidInfuserRecipe r) -> r.fluidInput,
                    ItemStack.STREAM_CODEC, (FluidInfuserRecipe r) -> r.result,
                    ByteBufCodecs.VAR_INT, (FluidInfuserRecipe r) -> r.time,
                    FluidInfuserRecipe::new
            );
        }
    }
}
