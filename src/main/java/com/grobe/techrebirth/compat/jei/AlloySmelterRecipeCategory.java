package com.grobe.techrebirth.compat.jei;

import com.grobe.techrebirth.TechRebirth;
import com.grobe.techrebirth.block.ModBlocks;
import com.grobe.techrebirth.recipe.AlloySmeltingRecipe;
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
import net.minecraft.world.item.crafting.Ingredient;
import java.util.List;

public class AlloySmelterRecipeCategory implements IRecipeCategory<AlloySmeltingRecipe> {
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "alloy_smelting");
    public static final RecipeType<AlloySmeltingRecipe> TYPE = new RecipeType<>(UID, AlloySmeltingRecipe.class);
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

    // Progress bar position - DODANO
    private static final int PROGRESS_BAR_X = 72;
    private static final int PROGRESS_BAR_Y = 45;
    private static final int PROGRESS_BAR_WIDTH = 8;
    private static final int PROGRESS_BAR_HEIGHT = 16;
    private static final int PROGRESS_BAR_COLOR = 0xFF2B93CC;

    public AlloySmelterRecipeCategory(IGuiHelper helper) {
        // Use a clean blank background to avoid cropping/stretching issues
        this.background = helper.createBlankDrawable(BG_W, BG_H);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.ALLOY_SMELTER.get()));
        this.slotDrawable = helper.getSlotDrawable();
        this.progressBar = helper.createAnimatedDrawable(
                helper.createDrawable(ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "textures/gui/widgets.png"), 0, 0, PROGRESS_BAR_WIDTH, PROGRESS_BAR_HEIGHT),
                100, // trajanje animacije
                IDrawableAnimated.StartDirection.BOTTOM, // PUNI SE PREMA GORE
                true
        );
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

        List<Ingredient> ingredients = recipe.getIngredients();

        // Rotacija svake 2 sekunde
        long currentTime = System.currentTimeMillis();
        int rotation = (int)((currentTime / 2000) % 3); // 0, 1, 2

        // Uvijek 3 slota, ali rotiraj ingrediente
        Ingredient first = ingredients.size() > 0 ? ingredients.get(0) : Ingredient.EMPTY;
        Ingredient second = ingredients.size() > 1 ? ingredients.get(1) : Ingredient.EMPTY;
        Ingredient third = ingredients.size() > 2 ? ingredients.get(2) : Ingredient.EMPTY;

        // Rotiraj pozicije ingredienata
        switch (rotation) {
            case 0 -> {
                // Original layout
                builder.addSlot(RecipeIngredientRole.INPUT, 36, 17).setBackground(slotDrawable, -1, -1).addIngredients(first);
                builder.addSlot(RecipeIngredientRole.INPUT, 56, 9).setBackground(slotDrawable, -1, -1).addIngredients(second);
                builder.addSlot(RecipeIngredientRole.INPUT, 76, 17).setBackground(slotDrawable, -1, -1).addIngredients(third);
            }
            case 1 -> {
                // Rotiraj za jedan
                builder.addSlot(RecipeIngredientRole.INPUT, 36, 17).setBackground(slotDrawable, -1, -1).addIngredients(third);
                builder.addSlot(RecipeIngredientRole.INPUT, 56, 9).setBackground(slotDrawable, -1, -1).addIngredients(first);
                builder.addSlot(RecipeIngredientRole.INPUT, 76, 17).setBackground(slotDrawable, -1, -1).addIngredients(second);
            }
            case 2 -> {
                // Rotiraj za dva
                builder.addSlot(RecipeIngredientRole.INPUT, 36, 17).setBackground(slotDrawable, -1, -1).addIngredients(second);
                builder.addSlot(RecipeIngredientRole.INPUT, 56, 9).setBackground(slotDrawable, -1, -1).addIngredients(third);
                builder.addSlot(RecipeIngredientRole.INPUT, 76, 17).setBackground(slotDrawable, -1, -1).addIngredients(first);
            }
        }

        builder.addSlot(RecipeIngredientRole.OUTPUT, 56, 45).setBackground(slotDrawable, -1, -1).addItemStack(recipe.getResult())
                .addRichTooltipCallback((iRecipeSlotView, iTooltipBuilder) -> {
                    int cookingTime = recipe.getCookingTime();
                    float seconds = cookingTime / 20.0f;
                    iTooltipBuilder.add(Component.literal("Alloying time: " + seconds + " seconds"));
                    //iTooltipBuilder.add(Component.literal("Energy cost: " +  + " FE"));
                });

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

        int px = PROGRESS_BAR_X;
        int py = PROGRESS_BAR_Y;

        // Progress bar background
        gg.fill(px - 1, py - 1, px + PROGRESS_BAR_WIDTH + 1, py + PROGRESS_BAR_HEIGHT + 1, 0xFF404040);
        gg.fill(px, py, px + PROGRESS_BAR_WIDTH, py + PROGRESS_BAR_HEIGHT, 0xFF202020);

        // Animirani progress fill
        long gameTime = System.currentTimeMillis() / 50;
        int progressHeight = (int) ((gameTime % PROGRESS_BAR_HEIGHT) + 1);
        int fillY = py + (PROGRESS_BAR_HEIGHT - progressHeight);
        gg.fill(px, fillY, px + PROGRESS_BAR_WIDTH, py + PROGRESS_BAR_HEIGHT, PROGRESS_BAR_COLOR);

        // Progress bar border
        gg.fill(px - 1, py - 1, px + PROGRESS_BAR_WIDTH + 1, py, 0xFF606060);
        gg.fill(px - 1, py + PROGRESS_BAR_HEIGHT, px + PROGRESS_BAR_WIDTH + 1, py + PROGRESS_BAR_HEIGHT + 1, 0xFF606060);
        gg.fill(px - 1, py, px, py + PROGRESS_BAR_HEIGHT, 0xFF606060);
        gg.fill(px + PROGRESS_BAR_WIDTH, py, px + PROGRESS_BAR_WIDTH + 1, py + PROGRESS_BAR_HEIGHT, 0xFF606060);
    }
}
