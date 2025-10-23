package com.grobe.techrebirth.recipe;

import com.grobe.techrebirth.TechRebirth;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
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
    private final ItemStack chanceOutput;
    private final float chanceRate;

    public CrushingRecipe(Ingredient ingredient, ItemStack result, int time, ItemStack chanceOutput, float chanceRate) {
        this.ingredient = ingredient;
        this.result = result;
        this.time = time;
        this.chanceOutput = (chanceOutput == null || chanceOutput.isEmpty()) ? ItemStack.EMPTY : chanceOutput;
        this.chanceRate = chanceRate;
    }

    public Ingredient getIngredient() { return ingredient; }
    public ItemStack getResult() { return result; }
    public int getTime() { return time; }
    public ItemStack getChanceOutput() { return chanceOutput; }
    public float getChanceRate() { return chanceRate; }
    public boolean hasChanceOutput() {return chanceOutput != null && !chanceOutput.isEmpty();}

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
                    Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(r -> r.ingredient),
                    ItemStack.CODEC.fieldOf("result").forGetter(r -> r.result),
                    Codec.INT.optionalFieldOf("time", 72).forGetter(r -> r.time),
                    ItemStack.CODEC.optionalFieldOf("chance_output", ItemStack.EMPTY).forGetter(r -> r.chanceOutput),
                    Codec.FLOAT.optionalFieldOf("chance_rate", 0.0f).forGetter(r -> r.chanceRate)
            ).apply(instance, CrushingRecipe::new));
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CrushingRecipe> streamCodec() {
            return StreamCodec.composite(
                    Ingredient.CONTENTS_STREAM_CODEC, (CrushingRecipe r) -> r.ingredient,
                    ItemStack.STREAM_CODEC, (CrushingRecipe r) -> r.result,
                    ByteBufCodecs.VAR_INT, (CrushingRecipe r) -> r.time,
                    ItemStack.OPTIONAL_STREAM_CODEC, (CrushingRecipe r) -> r.chanceOutput,
                    ByteBufCodecs.FLOAT, (CrushingRecipe r) -> r.chanceRate,
                    CrushingRecipe::new
            );
        }
    }
}
