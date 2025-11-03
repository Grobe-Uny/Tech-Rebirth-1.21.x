package com.grobe.techrebirth.compat.jei;

import com.grobe.techrebirth.TechRebirth;
import com.grobe.techrebirth.block.ModBlocks;
import com.grobe.techrebirth.recipe.CentrifugeRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class CentrifugeRecipeCategory implements IRecipeCategory<CentrifugeRecipe> {
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "centrifuging");
    public static final RecipeType<CentrifugeRecipe> TYPE = new RecipeType<>(UID, CentrifugeRecipe.class);
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable slotDrawable;

    public CentrifugeRecipeCategory(IGuiHelper helper) {
        this.background = helper.createBlankDrawable(176, 85);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.ELECTRIC_CENTRIFUGE.get()));
        this.slotDrawable = helper.getSlotDrawable();
    }

    @Override
    public RecipeType<CentrifugeRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.techrebirth.electric_centrifuge");
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
    public void setRecipe(IRecipeLayoutBuilder builder, CentrifugeRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 44, 35).addIngredients(recipe.input());
        builder.addSlot(RecipeIngredientRole.CATALYST, 18, 52).addIngredients(recipe.catalyst());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 116, 35).addItemStack(recipe.output());
    }

    @Override
    public void draw(CentrifugeRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics gg, double mouseX, double mouseY) {
        // Render catalyst amount
        Font font = Minecraft.getInstance().font;
        String catalystAmount = recipe.catalystAmount() + " mB";
        gg.drawString(font, catalystAmount, 18, 40, 0xFFFFFF, false);
        IRecipeCategory.super.draw(recipe, recipeSlotsView, gg, mouseX, mouseY);
    }
}
