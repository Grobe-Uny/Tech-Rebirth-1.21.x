package com.grobe.techrebirth.gui.generator;

import com.grobe.techrebirth.TechRebirth;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class GeneratorScreen extends AbstractContainerScreen<GeneratorMenu> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "textures/gui/generator_gui.png");

    // Actual texture dimensions
    private static final int TEX_W = 176;
    private static final int TEX_H = 166;

    public GeneratorScreen(GeneratorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        // Ensure GUI size matches our texture
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
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // Important: pass texture size so UVs are correct for non-256 textures
        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight, TEX_W, TEX_H);

        // Render energy bar on the left
        renderEnergyBar(guiGraphics, x, y);
        // Render an animated burn bar
        renderBurnBar(guiGraphics, x, y);
        // Render info text above the fuel slot
        renderInfoText(guiGraphics, x, y);
    }

    private void renderBurnBar(GuiGraphics guiGraphics, int x, int y) {
        if (menu.isBurning()) {
            int h = menu.getScaledBurnProgress(); // 0..14
            // Draw inside the fuel slot (slot at 80,35). Use a 14x14 area inset by 2px from the slot edges.
            int barW = 14;
            int barH = h;
            int slotX = x + 80;
            int slotY = y + 35;
            int bx = slotX + 2;           // 82
            int by = slotY + 2 + (14 - barH); // bottom-up fill within 14x14 area
            int flameColor = 0xFFFFA000; // orange
            // Only draw the flame fill, rely on the GUI texture/slot frame for visuals
            guiGraphics.fill(bx, by, bx + barW, by + barH, flameColor);
        }
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

    private void renderInfoText(GuiGraphics guiGraphics, int x, int y) {
        int seconds = menu.getBurnSecondsRemaining();
        int gen = menu.getGenPerTick();
        String text = "" + seconds + "s left, " + gen + " RF/t";
        // Above the fuel slot (slot at 80,35), place text centered over it
        int tx = x + 80 - (this.font.width(text) / 2);
        int ty = y + 24; // a bit above slot y=35
        guiGraphics.drawString(this.font, text, tx, ty, 0xFFFFFF, false);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics, mouseX, mouseY, delta);
        super.render(guiGraphics, mouseX, mouseY, delta);

        // Tooltip for energy bar on hover
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        int ex = x + ENERGY_BAR_X;
        int ey = y + ENERGY_BAR_Y;
        if (mouseX >= ex && mouseX < ex + ENERGY_BAR_WIDTH && mouseY >= ey && mouseY < ey + ENERGY_BAR_HEIGHT) {
            int energy = menu.getEnergyStored();
            int max = menu.getMaxEnergyStored();
            guiGraphics.renderTooltip(this.font,
                    net.minecraft.network.chat.Component.literal(energy + " / " + max + " RF"),
                    mouseX, mouseY);
        }

        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
