package com.grobe.techrebirth.recipe;

import com.grobe.techrebirth.block.ModBlocks;
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

public record CentrifugeRecipe(
    Ingredient input,
    Ingredient catalyst,
    int catalystAmount,
    ItemStack output,
    int processingTime
) implements Recipe<SingleRecipeInput> {

    @Override
    public boolean matches(SingleRecipeInput pContainer, Level pLevel) {
        return this.input.test(pContainer.getItem(0));
    }

    @Override
    public ItemStack assemble(SingleRecipeInput pContainer, HolderLookup.Provider pRegistries) {
        return this.output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider pRegistries) {
        return this.output.copy();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.createWithCapacity(2);
        ingredients.add(this.input);
        ingredients.add(this.catalyst);
        return ingredients;
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(ModBlocks.ELECTRIC_CENTRIFUGE.get());
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.CENTRIFUGE_SERIALIZER.get();

    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.CENTRIFUGE_TYPE.get();
    }


    public static class Serializer implements RecipeSerializer<CentrifugeRecipe> {
        @Override
        public MapCodec<CentrifugeRecipe> codec() {
            return RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(r -> r.input),
                    Ingredient.CODEC_NONEMPTY.fieldOf("catalyst").forGetter(r -> r.catalyst),
                    Codec.INT.fieldOf("catalyst_amount").forGetter(r -> r.catalystAmount),
                    ItemStack.CODEC.fieldOf("result").forGetter(r -> r.output),
                    Codec.INT.optionalFieldOf("time", 200).forGetter(r -> r.processingTime)
            ).apply(instance, CentrifugeRecipe::new));
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CentrifugeRecipe> streamCodec() {
            return StreamCodec.composite(
                    Ingredient.CONTENTS_STREAM_CODEC, (CentrifugeRecipe r) -> r.input,
                    Ingredient.CONTENTS_STREAM_CODEC, (CentrifugeRecipe r) -> r.catalyst,
                    ByteBufCodecs.INT, (CentrifugeRecipe r) -> r.catalystAmount,
                    ItemStack.STREAM_CODEC, (CentrifugeRecipe r) -> r.output,
                    ByteBufCodecs.VAR_INT, (CentrifugeRecipe r) -> r.processingTime,
                    CentrifugeRecipe::new
            );
        }
    }
}

