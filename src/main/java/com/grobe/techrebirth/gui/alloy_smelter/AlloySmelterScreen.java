package com.grobe.techrebirth.gui.alloy_smelter;

import com.grobe.techrebirth.TechRebirth;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;


public class AlloySmelterScreen extends AbstractContainerScreen<AlloySmelterMenu> {
    private static final ResourceLocation TEXTURE =
        ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "textures/gui/alloy_smelter_gui.png");

    // Actual texture dimensions
    private static final int TEX_W = 176;
    private static final int TEX_H = 166;

    public AlloySmelterScreen(AlloySmelterMenu pMenu, Inventory pInventory, Component pTitle){
        super(pMenu, pInventory, pTitle);
        this.imageWidth = TEX_W;
        this.imageHeight = TEX_H;
    }
    @Override
    protected void init() {
        super.init();
        this.inventoryLabelY = 10000;
        this.titleLabelY = 10000;
    }
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight, TEX_W, TEX_H);



        renderEnergyBar(guiGraphics, x, y);
        renderProgressBar(guiGraphics, x,y);
    }


    private static final int ENERGY_BAR_X = 10; // relative to GUI x
    private static final int ENERGY_BAR_Y = 18; // relative to GUI y
    private static final int ENERGY_BAR_WIDTH = 10;
    private static final int ENERGY_BAR_HEIGHT = 50;

    private static final int PROGRESS_BAR_X = 104;
    private static final int PROGRESS_BAR_Y = 60;
    private static final int PROGRESS_BAR_WIDTH = 8;
    private static final int PROGRESS_BAR_HEIGHT = 16;

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
        int filled = menu.getEnergyScaled(ENERGY_BAR_HEIGHT);
        if (filled > 0) {
            int fy = ey + (ENERGY_BAR_HEIGHT - filled);
            int color = 0xFFCC2B2B; // red
            guiGraphics.fill(ex, fy, ex + ENERGY_BAR_WIDTH, ey + ENERGY_BAR_HEIGHT, color);
        }
    }
    private void renderProgressBar(GuiGraphics guiGraphics, int x, int y) {
        int progress = menu.getProgress();
        int maxProgress = menu.getMaxProgress();

        int px = x + PROGRESS_BAR_X;
        int py = y + PROGRESS_BAR_Y;

        // Iscrtaj pozadinu progress bara (tamno siva) - UVIJEK VIDLJIVA
        int bgBorder = 0xFF404040;
        guiGraphics.fill(px - 1, py - 1, px + PROGRESS_BAR_WIDTH + 1, py + PROGRESS_BAR_HEIGHT + 1, bgBorder);

        int bg = 0xFF202020;
        guiGraphics.fill(px, py, px + PROGRESS_BAR_WIDTH, py + PROGRESS_BAR_HEIGHT, bg);

        // Iscrtaj puni dio progress bara (plavi) - OD DOlJE PREMA GORE
        if (maxProgress > 0 && progress > 0) {
            int progressHeight = menu.getProgressScaled(PROGRESS_BAR_HEIGHT);
            if (progressHeight > 0) {
                int fillY = py + (PROGRESS_BAR_HEIGHT - progressHeight); // Počni od dna
                int color = 0xFF2B93CC; // Plava boja
                guiGraphics.fill(px, fillY, px + PROGRESS_BAR_WIDTH, py + PROGRESS_BAR_HEIGHT, color);
            }
        }
        // Iscrtaj border (svijetlo sivi)
        int border = 0xFF606060;
        guiGraphics.fill(px - 1, py - 1, px + PROGRESS_BAR_WIDTH + 1, py, border); // gornji border
        guiGraphics.fill(px - 1, py + PROGRESS_BAR_HEIGHT, px + PROGRESS_BAR_WIDTH + 1, py + PROGRESS_BAR_HEIGHT + 1, border); // donji border
        guiGraphics.fill(px - 1, py, px, py + PROGRESS_BAR_HEIGHT, border); // lijevi border
        guiGraphics.fill(px + PROGRESS_BAR_WIDTH, py, px + PROGRESS_BAR_WIDTH + 1, py + PROGRESS_BAR_HEIGHT, border);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics, mouseX, mouseY, delta);
        super.render(guiGraphics, mouseX, mouseY, delta);

        // Tooltip for energy bar on hover (same as generator)
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        int ex = x + ENERGY_BAR_X;
        int ey = y + ENERGY_BAR_Y;
        if (mouseX >= ex && mouseX < ex + ENERGY_BAR_WIDTH && mouseY >= ey && mouseY < ey + ENERGY_BAR_HEIGHT) {
            int energy = menu.getEnergy();
            int max = menu.getMaxEnergy();
            guiGraphics.renderTooltip(this.font,
                    net.minecraft.network.chat.Component.literal(energy + " / " + max + " RF"),
                    mouseX, mouseY);
        }

        // Tooltip za PROGRESS BAR - UVIJEK aktivan
        int px = x + PROGRESS_BAR_X;
        int py = y + PROGRESS_BAR_Y;
        if (mouseX >= px && mouseX < px + PROGRESS_BAR_WIDTH && mouseY >= py && mouseY < py + PROGRESS_BAR_HEIGHT) {
            int progress = menu.getProgress();
            int maxProgress = menu.getMaxProgress();

            if (maxProgress > 0) {
                float percent = (float) progress / maxProgress * 100;
                int secondsLeft = (maxProgress - progress) / 20;

                String status = progress > 0 ?
                        String.format("Progress: %d/%d (%.1f%%) - %ds left", progress, maxProgress, percent, secondsLeft) :
                        "Ready to process";

                guiGraphics.renderTooltip(this.font,
                        Component.literal(status),
                        mouseX, mouseY);
            } else {
                guiGraphics.renderTooltip(this.font,
                        Component.literal("No active recipe"),
                        mouseX, mouseY);
            }
        }


        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
