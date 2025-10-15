package com.grobe.techrebirth.compat.jei;

import com.grobe.techrebirth.TechRebirth;
import com.grobe.techrebirth.block.ModBlocks;
import com.grobe.techrebirth.recipe.AlloySmeltingRecipe;
import com.grobe.techrebirth.recipe.CrushingRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SmeltingRecipe;

public class AlloySmelterRecipeCategory implements IRecipeCategory<AlloySmeltingRecipe> {
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "alloy_smelting");
    public static final RecipeType<AlloySmeltingRecipe> TYPE = new RecipeType<>(UID, AlloySmeltingRecipe.class);
    private final IDrawable background;
    private final IDrawable icon;

    // Compact JEI panel size
    private static final int BG_W = 130;
    private static final int BG_H = 62;

    // Energy bar visuals (match in-game style roughly)
    private static final int ENERGY_BAR_X = 6;
    private static final int ENERGY_BAR_Y = 6;
    private static final int ENERGY_BAR_WIDTH = 10;
    private static final int ENERGY_BAR_HEIGHT = 50;

    public AlloySmelterRecipeCategory(IGuiHelper helper) {
        // Use a clean blank background to avoid cropping/stretching issues
        this.background = helper.createBlankDrawable(BG_W, BG_H);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.ALLOY_SMELTER.get()));
    }

    @Override
    public RecipeType<AlloySmeltingRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.techrebirth.alloy_smelter");
    }

    @SuppressWarnings("removal")
    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, AlloySmeltingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 36, 9).addIngredients(recipe.getIngredients().get(0));
        builder.addSlot(RecipeIngredientRole.INPUT, 56, 9).addIngredients(recipe.getIngredients().get(1));
        builder.addSlot(RecipeIngredientRole.INPUT, 76, 9).addIngredients(recipe.getIngredients().get(2));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 56, 45).addItemStack(recipe.getResult());
    }
    @Override
    public void draw(AlloySmeltingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics gg, double mouseX, double mouseY){
        // Draw energy bar border and filled bar (display full for clarity in JEI)
        int ex = ENERGY_BAR_X;
        int ey = ENERGY_BAR_Y;
        // Border
        gg.fill(ex - 1, ey - 1, ex + ENERGY_BAR_WIDTH + 1, ey + ENERGY_BAR_HEIGHT + 1, 0xFF202020);
        // Background
        gg.fill(ex, ey, ex + ENERGY_BAR_WIDTH, ey + ENERGY_BAR_HEIGHT, 0xFF101010);
        // Filled energy (full to indicate power usage context)
        gg.fill(ex, ey, ex + ENERGY_BAR_WIDTH, ey + ENERGY_BAR_HEIGHT, 0xFFCC2B2B);

    }
}
