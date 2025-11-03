package com.grobe.techrebirth.gui.electric_centrifuge;

import com.grobe.techrebirth.TechRebirth;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.Optional;

public class ElectricCentrifugeScreen extends AbstractContainerScreen<ElectricCentrifugeMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "textures/gui/electric_centrifuge_gui.png");

    public ElectricCentrifugeScreen(ElectricCentrifugeMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        renderProgressArrow(guiGraphics, x, y);
        renderEnergyBar(guiGraphics, x, y);
        renderCatalystBar(guiGraphics, x, y);
    }

    private void renderProgressArrow(GuiGraphics guiGraphics, int x, int y) {
        if (menu.isCrafting()) {
            guiGraphics.blit(TEXTURE, x + 79, y + 34, 176, 0, menu.getScaledProgress(), 17);
        }
    }

    private void renderEnergyBar(GuiGraphics guiGraphics, int x, int y) {
        int energy = menu.getEnergy();
        int maxEnergy = menu.getMaxEnergy();
        int height = (int) (52 * ((float) energy / maxEnergy));
        guiGraphics.blit(TEXTURE, x + 152, y + 17 + (52 - height), 176, 17, 16, height);
    }

    private void renderCatalystBar(GuiGraphics guiGraphics, int x, int y) {
        int catalyst = menu.getCatalystAmount();
        int maxCatalyst = 1000; // This should be synced from the BE
        int height = (int) (52 * ((float) catalyst / maxCatalyst));
        guiGraphics.blit(TEXTURE, x + 8, y + 17 + (52 - height), 192, 17, 16, height);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics, mouseX, mouseY, delta);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int pMouseX, int pMouseY) {
        if (isHovering(152, 17, 16, 52, pMouseX, pMouseY)) {
            guiGraphics.renderTooltip(this.font, Component.literal(menu.getEnergy() + " / " + menu.getMaxEnergy() + " RF"),
                    Optional.empty(), pMouseX, pMouseY);
        }
        if (isHovering(8, 17, 16, 52, pMouseX, pMouseY)) {
            guiGraphics.renderTooltip(this.font, Component.literal(menu.getCatalystItem().getDisplayName().getString() + " " + menu.getCatalystAmount() + " / 1000 mB"),
                    Optional.empty(), pMouseX, pMouseY);
        }
        if (isHovering(79, 34, 24, 17, pMouseX, pMouseY)) {
            if (menu.isCrafting()) {
                float timeRemaining = (menu.getMaxProgress() - menu.getProgress()) / 20f;
                guiGraphics.renderTooltip(this.font, Component.literal(String.format("%.2f s", timeRemaining)),
                        Optional.empty(), pMouseX, pMouseY);
            }
        }
        super.renderTooltip(guiGraphics, pMouseX, pMouseY);
    }
}
