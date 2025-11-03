package com.grobe.techrebirth.compat.jei;

import com.grobe.techrebirth.TechRebirth;
import com.grobe.techrebirth.block.ModBlocks;
import com.grobe.techrebirth.recipe.ModRecipeTypes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SmeltingRecipe;

import java.util.List;
import java.util.Objects;

@JeiPlugin
public class JEITechRebirthPlugin implements IModPlugin {
    private static IJeiRuntime jeiRuntime;

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "jei_plugin");
    }
    @Override
    public void onRuntimeAvailable(IJeiRuntime runtime){
        jeiRuntime = runtime;
    }
    public static IJeiRuntime getJeiRuntime(){
        return  jeiRuntime;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        var gui = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new ElectricFurnaceRecipeCategory(gui));
        registration.addRecipeCategories(new ElectricCrusherRecipeCategory(gui));
        registration.addRecipeCategories(new AlloySmelterRecipeCategory(gui));
        registration.addRecipeCategories(new CentrifugeRecipeCategory(gui));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();
        List<SmeltingRecipe> smeltingRecipes = recipeManager.getAllRecipesFor(net.minecraft.world.item.crafting.RecipeType.SMELTING).stream()
                .map(holder -> holder.value())
                .toList();
        registration.addRecipes(ElectricFurnaceRecipeCategory.TYPE, smeltingRecipes);

        // Crusher recipes
        var crushing = recipeManager.getAllRecipesFor(ModRecipeTypes.CRUSHING_TYPE.get()).stream()
                .map(holder -> holder.value())
                .toList();
        registration.addRecipes(ElectricCrusherRecipeCategory.TYPE, crushing);
        // Alloy recipes
        var alloying = recipeManager.getAllRecipesFor(ModRecipeTypes.ALLOY_SMELTING_TYPE.get()).stream()
                .map(holder -> holder.value())
                .toList();
        registration.addRecipes(AlloySmelterRecipeCategory.TYPE, alloying);

        // Centrifuge recipes
        var centrifuging = recipeManager.getAllRecipesFor(ModRecipeTypes.CENTRIFUGE_TYPE.get()).stream()
                .map(holder -> holder.value())
                .toList();
        registration.addRecipes(CentrifugeRecipeCategory.TYPE, centrifuging);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.ELECTRIC_FURNACE.get()), ElectricFurnaceRecipeCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.ELECTRIC_CRUSHER.get()), ElectricCrusherRecipeCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.ALLOY_SMELTER.get()),    AlloySmelterRecipeCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.ELECTRIC_CENTRIFUGE.get()), CentrifugeRecipeCategory.TYPE);
    }
}