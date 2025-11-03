package com.grobe.techrebirth.recipe;

import com.grobe.techrebirth.block.ModBlocks;
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
        return CentrifugeRecipeSerializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.CENTRIFUGE_TYPE.get();
    }

    public static class CentrifugeRecipeSerializer {
        public static final CentrifugeRecipeSerializer INSTANCE = new CentrifugeRecipeSerializer();
        public static final MapCodec<CentrifugeRecipe> CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(CentrifugeRecipe::input),
                Ingredient.CODEC_NONEMPTY.fieldOf("catalyst").forGetter(CentrifugeRecipe::catalyst),
                Codec.INT.fieldOf("catalystAmount").forGetter(CentrifugeRecipe::catalystAmount),
                ItemStack.STRICT_CODEC.fieldOf("output").forGetter(CentrifugeRecipe::output),
                Codec.INT.fieldOf("processingTime").forGetter(CentrifugeRecipe::processingTime)
            ).apply(builder, CentrifugeRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, CentrifugeRecipe> STREAM_CODEC = StreamCodec.of(
            CentrifugeRecipeSerializer::toNetwork,
            CentrifugeRecipeSerializer::fromNetwork
        );

        private static void toNetwork(RegistryFriendlyByteBuf buf, CentrifugeRecipe recipe) {
            recipe.input.toNetwork(buf);
            recipe.catalyst.toNetwork(buf);
            buf.writeInt(recipe.catalystAmount);
            ItemStack.STREAM_CODEC.encode(buf, recipe.output);
            buf.writeInt(recipe.processingTime);
        }

        private static CentrifugeRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
            Ingredient input = Ingredient.fromNetwork(buf);
            Ingredient catalyst = Ingredient.fromNetwork(buf);
            int catalystAmount = buf.readInt();
            ItemStack output = ItemStack.STREAM_CODEC.decode(buf);
            int processingTime = buf.readInt();
            return new CentrifugeRecipe(input, catalyst, catalystAmount, output, processingTime);
        }
    }
}
