package com.grobe.techrebirth.gui.electric_centrifuge;

import com.grobe.techrebirth.TechRebirth;
import com.grobe.techrebirth.compat.jei.JEITechRebirthPlugin;
import com.grobe.techrebirth.gui.BaseMachineScreen;
import com.grobe.techrebirth.recipe.CentrifugeRecipe;
import com.mojang.blaze3d.systems.RenderSystem;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public class ElectricCentrifugeScreen extends BaseMachineScreen<ElectricCentrifugeMenu> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "textures/gui/electric_centrifuge_gui.png");

    public ElectricCentrifugeScreen(ElectricCentrifugeMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, TEXTURE, PROGRESS_BAR_X, PROGRESS_BAR_Y, PROGRESS_BAR_WIDTH, PROGRESS_BAR_HEIGHT, false, true);
        this.imageWidth = TEX_W;
        this.imageHeight = TEX_H;
    }


//    private static final int TEX_W = 176;
//    private static final int TEX_H = 166;


    private static final int PROGRESS_BAR_X = 82;
    private static final int PROGRESS_BAR_Y = 40;
    private static final int PROGRESS_BAR_WIDTH = 16;
    private static final int PROGRESS_BAR_HEIGHT = 8;

    private static final int CATALYST_BAR_WIDTH = 5;
    private static final int CATALYST_BAR_HEIGHT = 20;




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


        renderTooltip(guiGraphics, mouseX, mouseY);
    }

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
