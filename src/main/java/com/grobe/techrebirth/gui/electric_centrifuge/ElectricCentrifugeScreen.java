package com.grobe.techrebirth.gui.electric_centrifuge;

import com.grobe.techrebirth.Config;
import com.grobe.techrebirth.TechRebirth;
import com.grobe.techrebirth.compat.jei.JEITechRebirthPlugin;
import com.grobe.techrebirth.gui.BaseMachineScreen;
import com.grobe.techrebirth.gui.alloy_smelter.AlloySmelterScreen;
import com.grobe.techrebirth.recipe.AlloySmeltingRecipe;
import com.grobe.techrebirth.recipe.CentrifugeRecipe;
import com.mojang.blaze3d.systems.RenderSystem;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;
import java.util.Optional;

public class ElectricCentrifugeScreen extends BaseMachineScreen<ElectricCentrifugeMenu> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "textures/gui/electric_centrifuge_gui.png");

    public ElectricCentrifugeScreen(ElectricCentrifugeMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, TEXTURE, PROGRESS_BAR_X, PROGRESS_BAR_Y, PROGRESS_BAR_WIDTH, PROGRESS_BAR_HEIGHT, false);
        this.imageWidth = TEX_W;
        this.imageHeight = TEX_H;
    }


//    private static final int TEX_W = 176;
//    private static final int TEX_H = 166;
//
//    private static final int ENERGY_BAR_X = 10;
//    private static final int ENERGY_BAR_Y = 18;
//    private static final int ENERGY_BAR_WIDTH = 10;
//    private static final int ENERGY_BAR_HEIGHT = 50;

    private static final int PROGRESS_BAR_X = 82;
    private static final int PROGRESS_BAR_Y = 40;
    private static final int PROGRESS_BAR_WIDTH = 16;
    private static final int PROGRESS_BAR_HEIGHT = 8;

    private static final int CATALYST_BAR_WIDTH = 5;
    private static final int CATALYST_BAR_HEIGHT = 20;



    private final ProgressBarArea progressBarArea = new ProgressBarArea();

    @Override
    protected void init() {
        super.init();
        this.inventoryLabelY = 10000;
        this.titleLabelY = 10000;
        progressBarArea.updateScreenCoords(this.leftPos, this.topPos);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        super.renderBg(guiGraphics, pPartialTick, pMouseX, pMouseY);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight,TEX_W,TEX_H );

        renderProgressBar(guiGraphics, x, y, pMouseX, pMouseY);
        renderCatalystBar(guiGraphics, x, y);
        renderEnergyBar(guiGraphics,x,y);
    }

//    private void renderProgressBar(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
//        int progress = menu.getProgress();
//        int maxProgress = menu.getMaxProgress();
//
//        int px = x + PROGRESS_BAR_X;
//        int py = y + PROGRESS_BAR_Y;
//
//        // Background
//        guiGraphics.fill(px - 1, py - 1, px + PROGRESS_BAR_WIDTH + 1, py + PROGRESS_BAR_HEIGHT + 1, 0xFF404040);
//        guiGraphics.fill(px, py, px + PROGRESS_BAR_WIDTH, py + PROGRESS_BAR_HEIGHT, 0xFF202020);
//
//        // Progress fill
//        if (maxProgress > 0 && progress > 0) {
//            int progressWidth = menu.getScaledProgress(PROGRESS_BAR_WIDTH);
//            if (progressWidth > 0) {
//                int fillX = px;
//                guiGraphics.fill(fillX, py, px + progressWidth, py + PROGRESS_BAR_HEIGHT, 0xFF2B93CC);
//            }
//        }
//        if ( Config.isHighlightEnabled() && progressBarArea.isMouseOver(mouseX, mouseY)) {
//            int highlightColor = Config.getHighlightColor();
//            // Nacrtaj zlatni outline kada je miš iznad
//            guiGraphics.fill(px - 1, py - 1, px + PROGRESS_BAR_WIDTH + 1, py, highlightColor); // gornja linija
//            guiGraphics.fill(px - 1, py + PROGRESS_BAR_HEIGHT, px + PROGRESS_BAR_WIDTH + 1, py + PROGRESS_BAR_HEIGHT + 1, highlightColor); // donja linija
//            guiGraphics.fill(px - 1, py, px, py + PROGRESS_BAR_HEIGHT, highlightColor); // lijeva linija
//            guiGraphics.fill(px + PROGRESS_BAR_WIDTH, py, px + PROGRESS_BAR_WIDTH + 1, py + PROGRESS_BAR_HEIGHT, highlightColor); // desna linija
//        }
//    }
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && progressBarArea.isMouseOver(mouseX, mouseY)) {
            showJEIRecipes();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    private void showJEIRecipes() {
        IJeiRuntime jei = JEITechRebirthPlugin.getJeiRuntime();
        if (jei != null) {
            RecipeType<CentrifugeRecipe> recipeType = RecipeType.create(
                    TechRebirth.MODID, "centrifuging", CentrifugeRecipe.class
            );
            jei.getRecipesGui().showTypes(List.of(recipeType));
        }
    }
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics, mouseX, mouseY, delta);
        super.render(guiGraphics, mouseX, mouseY, delta);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // Progress bar tooltip
        int px = x + PROGRESS_BAR_X;
        int py = y + PROGRESS_BAR_Y;
        if (mouseX >= px && mouseX < px + PROGRESS_BAR_WIDTH && mouseY >= py && mouseY < py + PROGRESS_BAR_HEIGHT) {
            String status = getProgressStatus();
            Component tooltip = Component.literal(status + "\n§aClick to view recipes in JEI");
            guiGraphics.renderTooltip(this.font, tooltip, mouseX, mouseY);
        }

        renderTooltip(guiGraphics, mouseX, mouseY);
    }

//    private String getProgressStatus() {
//        int progress = menu.getProgress();
//        int maxProgress = menu.getMaxProgress();
//
//        if (maxProgress > 0) {
//            float percent = (float) progress / maxProgress * 100;
//            int secondsLeft = (maxProgress - progress) / 20;
//            return progress > 0 ?
//                    String.format("Progress: %d/%d (%.1f%%) - %ds left", progress, maxProgress, percent, secondsLeft) :
//                    "Ready to process";
//        }
//        return "No active recipe";
//    }

//    // Helper class za progress bar area
//    private static class ProgressBarArea {
//        private int screenX, screenY;
//        private final int width = PROGRESS_BAR_WIDTH;
//        private final int height = PROGRESS_BAR_HEIGHT;
//
//        public void updateScreenCoords(int guiLeft, int guiTop) {
//            this.screenX = guiLeft + PROGRESS_BAR_X;
//            this.screenY = guiTop + PROGRESS_BAR_Y;
//        }
//
//        public boolean isMouseOver(double mouseX, double mouseY) {
//            return mouseX >= screenX && mouseX < screenX + width &&
//                    mouseY >= screenY && mouseY < screenY + height;
//        }
//    }

    private void renderCatalystBar(GuiGraphics guiGraphics, int x, int y) {

        int catalystSlotX = 26;
        int catalystSlotY = 58;
        int catalystSlotWidth = 16;

        int barCenterX = catalystSlotX + (catalystSlotWidth / 2);
        int cx = x + barCenterX - (CATALYST_BAR_WIDTH / 2);
        int cy = y + catalystSlotY - CATALYST_BAR_HEIGHT - 6;

        // Background
        int border = 0xFF404040;
        guiGraphics.fill(cx - 1, cy - 1, cx + CATALYST_BAR_WIDTH + 1, cy + CATALYST_BAR_HEIGHT + 1, border);

        int bg = 0xFF202020;
        guiGraphics.fill(cx, cy, cx + CATALYST_BAR_WIDTH, cy + CATALYST_BAR_HEIGHT, bg);

        // Progress fill - PUNI SE OD DOLJE PREMA GORE
        int filled = menu.getCatalystScaled(CATALYST_BAR_HEIGHT);
        if (filled > 0) {
            int fillY = cy + (CATALYST_BAR_HEIGHT - filled); // Počinje od dna
            int color = 0xFF32CD32;
            guiGraphics.fill(cx, fillY, cx + CATALYST_BAR_WIDTH, cy + CATALYST_BAR_HEIGHT, color);
        }
    }
}
