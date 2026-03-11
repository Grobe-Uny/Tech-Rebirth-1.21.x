package com.grobe.techrebirth.compat.jei;

import com.grobe.techrebirth.TechRebirth;
import com.grobe.techrebirth.block.ModBlocks;
import com.grobe.techrebirth.recipe.CrushingRecipe;
import com.grobe.techrebirth.recipe.PurifierRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
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

public class ElectricPurifierRecipeCategory implements IRecipeCategory<PurifierRecipe> {
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "purifying");
    public static final RecipeType<PurifierRecipe> TYPE = new RecipeType<>(UID, PurifierRecipe.class);
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable slotDrawable;
    private final IDrawableAnimated progressBar;

    // Compact JEI panel size
    private static final int BG_W = 130;
    private static final int BG_H = 62;

    // Energy bar visuals (match in-game style roughly)
    private static final int ENERGY_BAR_X = 6;
    private static final int ENERGY_BAR_Y = 6;
    private static final int ENERGY_BAR_WIDTH = 10;
    private static final int ENERGY_BAR_HEIGHT = 50;

    // Progress bar visuals (match in-game style roughly)
    private static final int PROGRESS_BAR_X = 60;
    private static final int PROGRESS_BAR_Y = 17;
    private static final int PROGRESS_BAR_WIDTH = 16;
    private static final int PROGRESS_BAR_HEIGHT = 8;
    private static final int PROGRESS_BAR_COLOR = 0xFF2B93CC;

    public ElectricPurifierRecipeCategory(IGuiHelper helper) {
        this.background = helper.createBlankDrawable(BG_W, BG_H);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.ELECTRIC_CRUSHER.get()));
        this.slotDrawable = helper.getSlotDrawable();
        this.progressBar = helper.createAnimatedDrawable(
                helper.createDrawable(ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "textures/gui/widgets.png"), 0, 0, PROGRESS_BAR_WIDTH, PROGRESS_BAR_HEIGHT),
                100, // trajanje animacije
                IDrawableAnimated.StartDirection.LEFT, // Charge towards right
                true
        );
    }

    @Override
    public RecipeType<PurifierRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.techrebirth.electric_purifier");
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
    public void setRecipe(IRecipeLayoutBuilder builder, PurifierRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 36, 13).setBackground(slotDrawable, -1, -1).addIngredients(recipe.getIngredients().get(0));
        //regular output
        builder.addSlot(RecipeIngredientRole.OUTPUT, 84, 13).setBackground(slotDrawable, -1, -1).addItemStack(recipe.getResult());
    }

    @Override
    public void draw(PurifierRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics gg, double mouseX, double mouseY) {
        // Draw energy bar border and filled bar (display full for clarity in JEI)
        int ex = ENERGY_BAR_X;
        int ey = ENERGY_BAR_Y;
        // Border
        gg.fill(ex - 1, ey - 1, ex + ENERGY_BAR_WIDTH + 1, ey + ENERGY_BAR_HEIGHT + 1, 0xFF202020);
        // Background
        gg.fill(ex, ey, ex + ENERGY_BAR_WIDTH, ey + ENERGY_BAR_HEIGHT, 0xFF101010);
        // Filled energy (full to indicate power usage context)
        gg.fill(ex, ey, ex + ENERGY_BAR_WIDTH, ey + ENERGY_BAR_HEIGHT, 0xFFCC2B2B);

        int px = PROGRESS_BAR_X;
        int py = PROGRESS_BAR_Y;

        //Background
        gg.fill(px - 1, py - 1, px + PROGRESS_BAR_WIDTH + 1,py + PROGRESS_BAR_HEIGHT +1, 0xFF404040);
        gg.fill(px, py, px + PROGRESS_BAR_WIDTH, py + PROGRESS_BAR_HEIGHT, 0xFF202020);

        //animated progress fill
        long gameTime = System.currentTimeMillis() / 50;
        int progressWidth = (int) ((gameTime % PROGRESS_BAR_WIDTH)+1);
        int fillX = px + (PROGRESS_BAR_WIDTH - progressWidth);
        gg.fill(fillX, py , px + progressWidth, py + PROGRESS_BAR_HEIGHT, PROGRESS_BAR_COLOR);

        //progress bar border
        gg.fill(px - 1, py - 1, px + PROGRESS_BAR_WIDTH + 1, py, 0xFF606060);
        gg.fill(px - 1, py + PROGRESS_BAR_HEIGHT, px + PROGRESS_BAR_WIDTH + 1, py + PROGRESS_BAR_HEIGHT + 1, 0xFF606060);
        gg.fill(px - 1, py, px, py + PROGRESS_BAR_HEIGHT, 0xFF606060);
        gg.fill(px + PROGRESS_BAR_WIDTH, py, px + PROGRESS_BAR_WIDTH + 1, py + PROGRESS_BAR_HEIGHT, 0xFF606060);

    }
}
