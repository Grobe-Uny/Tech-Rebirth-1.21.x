package com.grobe.techrebirth.recipe;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;


public class GeneratorFuelRecipe implements Recipe<SingleRecipeInput> {

    private final Ingredient ingredient; //fuel
    private final int burnTime;          // ticks to burn (e.g. coal = 1600 ticks)
    private final int powerPerTick;      // how much power to generate per tick


    public GeneratorFuelRecipe(Ingredient ingredient, int burnTime, int powerPerTick){
        this.ingredient = ingredient;
        this.burnTime = burnTime;
        this.powerPerTick = powerPerTick;
    }
    public Ingredient ingredient(){return  ingredient;}
    public int burnTime() {return burnTime;}
    public int powerPerTick() {return powerPerTick;}


    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return ingredient.test(input.item());
    }

    public ItemStack assemble(SingleRecipeInput container, HolderLookup.Provider provider){
        return ItemStack.EMPTY;
    }
    @Override
    public boolean canCraftInDimensions(int width, int height) { return true; }

    @Override
    public ItemStack getResultItem(net.minecraft.core.HolderLookup.Provider provider) { return ItemStack.EMPTY; }

    @Override
    public RecipeSerializer<?> getSerializer() { return ModRecipeTypes.GENERATOR_FUEL_SERIALIZER.get(); }

    @Override
    public RecipeType<?> getType() { return ModRecipeTypes.GENERATOR_FUEL_TYPE.get(); }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(ingredient);
        return list;
    }
    public static class Serializer implements RecipeSerializer<GeneratorFuelRecipe> {
        @Override
        public com.mojang.serialization.MapCodec<GeneratorFuelRecipe> codec() {
            return RecordCodecBuilder.mapCodec(inst -> inst.group(
                    Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(GeneratorFuelRecipe::ingredient),
                    com.mojang.serialization.Codec.INT.fieldOf("burn_time").forGetter(GeneratorFuelRecipe::burnTime),
                    com.mojang.serialization.Codec.INT.fieldOf("power_per_tick").forGetter(GeneratorFuelRecipe::powerPerTick)
            ).apply(inst, GeneratorFuelRecipe::new));
        }

        @Override
        public net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, GeneratorFuelRecipe> streamCodec() {
            return net.minecraft.network.codec.StreamCodec.composite(
                    Ingredient.CONTENTS_STREAM_CODEC, GeneratorFuelRecipe::ingredient,
                    net.minecraft.network.codec.ByteBufCodecs.VAR_INT, GeneratorFuelRecipe::burnTime,
                    net.minecraft.network.codec.ByteBufCodecs.VAR_INT, GeneratorFuelRecipe::powerPerTick,
                    GeneratorFuelRecipe::new
            );
        }
    }
}
