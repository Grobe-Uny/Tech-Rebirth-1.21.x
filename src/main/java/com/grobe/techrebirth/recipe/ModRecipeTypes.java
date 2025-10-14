package com.grobe.techrebirth.recipe;

import com.grobe.techrebirth.TechRebirth;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipeTypes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, TechRebirth.MODID);
    public static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, TechRebirth.MODID);

    public static final ResourceLocation CRUSHING_ID = ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "crushing");

    public static final ResourceLocation GENERATOR_FUEL_ID = ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "generator_fuel");

    public static final ResourceLocation ALLOY_SMELTER_ID = ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "alloy_smelting");

    public static final DeferredHolder<RecipeType<?>, RecipeType<GeneratorFuelRecipe>> GENERATOR_FUEL_TYPE =
            TYPES.register("generator_fuel", () -> new RecipeType<GeneratorFuelRecipe>() {
                public String toString() { return GENERATOR_FUEL_ID.toString(); }
            });

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<GeneratorFuelRecipe>> GENERATOR_FUEL_SERIALIZER =
            SERIALIZERS.register("generator_fuel", GeneratorFuelRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<AlloySmeltingRecipe>> ALLOY_SMELTING_TYPE =
            TYPES.register("alloy_smelting", () -> new RecipeType<AlloySmeltingRecipe>() {
                public String toString() { return ALLOY_SMELTER_ID.toString(); }
            });

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AlloySmeltingRecipe>> ALLOY_SMELTING_SERIALIZER =
            SERIALIZERS.register("alloy_smelting", AlloySmeltingRecipe.Serializer::new);


    public static final DeferredHolder<RecipeType<?>, RecipeType<CrushingRecipe>> CRUSHING_TYPE =
            TYPES.register("crushing", () -> new RecipeType<CrushingRecipe>() {
                public String toString() { return CRUSHING_ID.toString(); }
            });

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CrushingRecipe>> CRUSHING_SERIALIZER =
            SERIALIZERS.register("crushing", CrushingRecipe.Serializer::new);

    public static void register(IEventBus bus) {
        SERIALIZERS.register(bus);
        TYPES.register(bus);
    }
}
