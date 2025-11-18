package com.grobe.techrebirth.compat.jei;

import com.grobe.techrebirth.TechRebirth;
import com.grobe.techrebirth.block.ModBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import com.grobe.techrebirth.util.ModTags;

public class ElectricFurnaceRecipeCategory implements IRecipeCategory<SmeltingRecipe> {
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "electric_smelting");
    public static final RecipeType<SmeltingRecipe> TYPE = new RecipeType<>(UID, SmeltingRecipe.class);
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


    public ElectricFurnaceRecipeCategory(IGuiHelper helper) {
        // Use a clean blank background to avoid cropping/stretching issues
        this.background = helper.createBlankDrawable(BG_W, BG_H);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.ELECTRIC_FURNACE.get()));
    }

    @Override
    public RecipeType<SmeltingRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.techrebirth.electric_furnace");
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
    public void setRecipe(IRecipeLayoutBuilder builder, SmeltingRecipe recipe, IFocusGroup focuses) {
        // Input and output only; no upgrade slots in JEI view
        builder.addSlot(RecipeIngredientRole.INPUT, 32, 38)
               .addIngredients(recipe.getIngredients().get(0));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 92, 38)
               .addItemStack(recipe.getResultItem(null));
    }

    @Override
    public void draw(SmeltingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics gg, double mouseX, double mouseY) {
        // Draw energy bar border and filled bar (display full for clarity in JEI)
        int ex = ENERGY_BAR_X;
        int ey = ENERGY_BAR_Y;
        // Border
        gg.fill(ex - 1, ey - 1, ex + ENERGY_BAR_WIDTH + 1, ey + ENERGY_BAR_HEIGHT + 1, 0xFF202020);
        // Background
        gg.fill(ex, ey, ex + ENERGY_BAR_WIDTH, ey + ENERGY_BAR_HEIGHT, 0xFF101010);
        // Filled energy (full to indicate power usage context)
        gg.fill(ex, ey, ex + ENERGY_BAR_WIDTH, ey + ENERGY_BAR_HEIGHT, 0xFFCC2B2B);

        // Optional simple progress arrow box between slots
        int ax = 64, ay = 30, aw = 14, ah = 8;
        gg.fill(ax, ay, ax + aw, ay + ah, 0xFF606060);
        gg.fill(ax + 1, ay + 1, ax + aw - 1, ay + ah - 1, 0xFFA0A0A0);
        // Compute time and RF/op from the same rules as the block entity
        int vanilla = Math.max(1, recipe.getCookingTime());

        // Determine if the first displayed ingredient belongs to the heavy category
        boolean isHeavy = false;
        if (!recipe.getIngredients().isEmpty()) {
            var stacks = recipe.getIngredients().get(0).getItems();
            if (stacks.length > 0) {
                ItemStack s = stacks[0];
                isHeavy = s.is(ModTags.Items.FURNACE_HEAVY_D.neoforge()) || s.is(ModTags.Items.FURNACE_HEAVY_D.common());
            }
        }

        // Compute base cook and RF using same constants as the block entity
        float machineSpeed = 0.18f;
        float heavyTimeMult = 1.30f;
        float heavyRfMult = 1.15f;

        int baseCookA = Math.max(1, Math.round(vanilla * machineSpeed * (isHeavy ? heavyTimeMult : 1.0f)));
        int rfPerTickA = Math.round(128f * (isHeavy ? heavyRfMult : 1.0f)); // base, no upgrades in JEI
        int rfPerOpA = rfPerTickA * baseCookA;
        float secondsA = baseCookA / 20f;

        // Text info: RF per operation and time (shadowed white for contrast)
        Font font = Minecraft.getInstance().font;
        gg.drawString(font, Component.literal("RF/op: " + String.format("%,d", rfPerOpA)), 22, 4, 0xFFFFFFFF, true);
        gg.drawString(font, Component.literal(String.format("Time: %.1f s", secondsA)), 22, 16, 0xFFFFFFFF, true);
    }
}