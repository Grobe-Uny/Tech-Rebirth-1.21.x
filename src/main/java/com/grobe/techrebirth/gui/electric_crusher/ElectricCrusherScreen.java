package com.grobe.techrebirth.gui.electric_crusher;

import com.grobe.techrebirth.TechRebirth;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ElectricCrusherScreen extends AbstractContainerScreen<ElectricCrusherMenu> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "textures/gui/electric_crusher_gui.png");

    // Actual texture dimensions
    private static final int TEX_W = 176;
    private static final int TEX_H = 166;

    public ElectricCrusherScreen(ElectricCrusherMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = TEX_W;
        this.imageHeight = TEX_H;
    }

    @Override
    protected void init() {
        super.init();
        this.inventoryLabelY = 10000;
        this.titleLabelY = 10000;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0,imageWidth, imageHeight, TEX_W, TEX_H);

       renderEnergyBar(guiGraphics,x,y);
    }
    private static final int ENERGY_BAR_X = 10; // relative to GUI x
    private static final int ENERGY_BAR_Y = 18; // relative to GUI y
    private static final int ENERGY_BAR_WIDTH = 10;
    private static final int ENERGY_BAR_HEIGHT = 50;

    private void renderEnergyBar(GuiGraphics guiGraphics, int x, int y) {
        int ex = x + ENERGY_BAR_X;
        int ey = y + ENERGY_BAR_Y;
        // Draw background (dark gray border)
        int border = 0xFF202020;
        guiGraphics.fill(ex - 1, ey - 1, ex + ENERGY_BAR_WIDTH + 1, ey + ENERGY_BAR_HEIGHT + 1, border);
        // Draw an inner background (almost black)
        int bg = 0xFF101010;
        guiGraphics.fill(ex, ey, ex + ENERGY_BAR_WIDTH, ey + ENERGY_BAR_HEIGHT, bg);
        // Draw energy amount (red, from bottom up)
        int filled = menu.getScaledEnergy(ENERGY_BAR_HEIGHT);
        if (filled > 0) {
            int fy = ey + (ENERGY_BAR_HEIGHT - filled);
            int color = 0xFFCC2B2B; // red
            guiGraphics.fill(ex, fy, ex + ENERGY_BAR_WIDTH, ey + ENERGY_BAR_HEIGHT, color);
        }
    }


    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics, mouseX, mouseY, delta);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
