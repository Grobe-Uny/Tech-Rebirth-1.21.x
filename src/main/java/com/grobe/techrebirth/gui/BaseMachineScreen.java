package com.grobe.techrebirth.gui;

import com.grobe.techrebirth.Config;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;


public class BaseMachineScreen<T extends BaseMachineMenu> extends AbstractContainerScreen<T> {

    protected final ResourceLocation TEXTURE;


    protected final int TEX_W = 176;
    protected final int TEX_H = 166;

    private static final int ENERGY_BAR_X = 10;
    private static final int ENERGY_BAR_Y = 18;
    private static final int ENERGY_BAR_WIDTH = 10;
    private static final int ENERGY_BAR_HEIGHT = 50;

    protected final int PROGRESS_BAR_X;
    protected final int PROGRESS_BAR_Y;
    protected final int PROGRESS_BAR_WIDTH;
    protected final int PROGRESS_BAR_HEIGHT;

    protected final boolean isProgressBarVertical;
    protected final boolean hasProgressBar;

    protected final ProgressBarArea progressBarArea;
    public BaseMachineScreen(T menu, Inventory playerInventory, Component title, ResourceLocation texture,int pX, int pY, int pW, int pH, boolean isVertical, boolean hasProgressBar) {
        super(menu, playerInventory, title);
        this.TEXTURE = texture;
        this.imageWidth = TEX_W;
        this.imageHeight = TEX_H;
        this.PROGRESS_BAR_X = pX;
        this.PROGRESS_BAR_Y = pY;
        this.PROGRESS_BAR_WIDTH = pW;
        this.PROGRESS_BAR_HEIGHT = pH;
        this.isProgressBarVertical = isVertical;
        this.progressBarArea = new ProgressBarArea();
        this.hasProgressBar = hasProgressBar;
    }
    @Override
    protected void init(){
        super.init();
        this.inventoryLabelY = 10000;
        this.titleLabelY = 10000;
        if (this.progressBarArea != null && this.hasProgressBar) {
            this.progressBarArea.updateScreenCoords(this.leftPos, this.topPos);
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight, TEX_W, TEX_H);
        renderEnergyBar(guiGraphics, x, y);
        if(hasProgressBar){
            renderProgressBar(guiGraphics, x, y, mouseX, mouseY);
        }

    }

    protected void renderLabels(GuiGraphics guiGraphics, int MouseX, int MouseY){
        String title = this.title.getString();
        int titleX = (this.imageWidth - this.font.width(title)) / 2;
        int titleY = 6;

        guiGraphics.drawString(this.font, title, titleX, titleY, 0x404040, false);
    }
    protected void renderEnergyBar(GuiGraphics guiGraphics, int x, int y) {
        int ex = x + ENERGY_BAR_X;
        int ey = y + ENERGY_BAR_Y;
        int border = 0xFF202020;
        guiGraphics.fill(ex - 1, ey - 1, ex + ENERGY_BAR_WIDTH + 1, ey + ENERGY_BAR_HEIGHT + 1, border);
        int bg = 0xFF101010;
        guiGraphics.fill(ex, ey, ex + ENERGY_BAR_WIDTH, ey + ENERGY_BAR_HEIGHT, bg);
        int filled = menu.getEnergyScaled(ENERGY_BAR_HEIGHT);
        if (filled > 0) {
            int fy = ey + (ENERGY_BAR_HEIGHT - filled);
            int color = 0xFFCC2B2B;
            guiGraphics.fill(ex, fy, ex + ENERGY_BAR_WIDTH, ey + ENERGY_BAR_HEIGHT, color);
        }
    }


    protected void renderProgressBar(GuiGraphics gg, int x, int y, int mouseX, int mouseY) {
        int progress = menu.getProgress();
        int maxProgress = menu.getMaxProgress();

        int px = x + PROGRESS_BAR_X;
        int py = y + PROGRESS_BAR_Y;

        // Background
        gg.fill(px - 1, py - 1, px + PROGRESS_BAR_WIDTH + 1, py + PROGRESS_BAR_HEIGHT + 1, 0xFF404040);
        gg.fill(px, py, px + PROGRESS_BAR_WIDTH, py + PROGRESS_BAR_HEIGHT, 0xFF202020);

        // Progress fill
        if (maxProgress > 0 && progress > 0) {
            if(isProgressBarVertical){
                int progressHeight = menu.getVerticalScaledProgress(PROGRESS_BAR_HEIGHT);
                if (progressHeight > 0) {
                    int fillY = py + (PROGRESS_BAR_HEIGHT - progressHeight);
                    gg.fill(px, fillY, px + PROGRESS_BAR_WIDTH, py + PROGRESS_BAR_HEIGHT, 0xFF2B93CC);
                }
            }else{
                int progressWidth = menu.getHorizontalScaledProgress(PROGRESS_BAR_WIDTH);
                if (progressWidth > 0) {
                    int fillX = px + (PROGRESS_BAR_WIDTH - progressWidth);
                    gg.fill(fillX, py, px + progressWidth, py + PROGRESS_BAR_HEIGHT, 0xFF2B93CC);
                }
            }
        }

        if ( Config.isHighlightEnabled() && progressBarArea.isMouseOver(mouseX, mouseY)) {
            int highlightColor = Config.getHighlightColor();
            // Nacrtaj zlatni outline kada je miš iznad
            gg.fill(px - 1, py - 1, px + PROGRESS_BAR_WIDTH + 1, py, highlightColor); // gornja linija
            gg.fill(px - 1, py + PROGRESS_BAR_HEIGHT, px + PROGRESS_BAR_WIDTH + 1, py + PROGRESS_BAR_HEIGHT + 1, highlightColor); // donja linija
            gg.fill(px - 1, py, px, py + PROGRESS_BAR_HEIGHT, highlightColor); // lijeva linija
            gg.fill(px + PROGRESS_BAR_WIDTH, py, px + PROGRESS_BAR_WIDTH + 1, py + PROGRESS_BAR_HEIGHT, highlightColor); // desna linija
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

        // Energy bar tooltip
        int ex = x + ENERGY_BAR_X;
        int ey = y + ENERGY_BAR_Y;
        if (mouseX >= ex && mouseX < ex + ENERGY_BAR_WIDTH && mouseY >= ey && mouseY < ey + ENERGY_BAR_HEIGHT) {
            int energy = menu.getEnergy();
            int max = menu.getMaxEnergy();
            guiGraphics.renderTooltip(this.font,
                    Component.literal(energy + " / " + max + " RF"),
                    mouseX, mouseY);
        }

        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    protected String getProgressStatus() {
        int progress = menu.getProgress();
        int maxProgress = menu.getMaxProgress();

        if (maxProgress > 0) {
            float percent = (float) progress / maxProgress * 100;
            int secondsLeft = (maxProgress - progress) / 20;
            return progress > 0 ?
                    String.format("Progress: %d/%d (%.1f%%) - %ds left", progress, maxProgress, percent, secondsLeft) :
                    "Ready to process";
        }
        return "No active recipe";
    }
    // Helper class za progress bar area
    public class ProgressBarArea {
        private int screenX, screenY;
        private final int width = PROGRESS_BAR_WIDTH;
        private final int height = PROGRESS_BAR_HEIGHT;

        public void updateScreenCoords(int guiLeft, int guiTop) {
            this.screenX = guiLeft + PROGRESS_BAR_X;
            this.screenY = guiTop + PROGRESS_BAR_Y;
        }

        public boolean isMouseOver(double mouseX, double mouseY) {
            return mouseX >= screenX && mouseX < screenX + width &&
                    mouseY >= screenY && mouseY < screenY + height;
        }
    }
}
