package com.grobe.techrebirth.gui.alloy_smelter;

import com.grobe.techrebirth.Config;
import com.grobe.techrebirth.TechRebirth;
import com.grobe.techrebirth.compat.jei.JEITechRebirthPlugin;
import com.grobe.techrebirth.item.ModItems;
import com.grobe.techrebirth.item.custom.UpgradeItem;
import com.grobe.techrebirth.recipe.AlloySmeltingRecipe;
import com.mojang.blaze3d.systems.RenderSystem;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class AlloySmelterScreen extends AbstractContainerScreen<AlloySmelterMenu> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "textures/gui/alloy_smelter_gui.png");

    private static final int TEX_W = 176;
    private static final int TEX_H = 166;

    private static final int ENERGY_BAR_X = 10;
    private static final int ENERGY_BAR_Y = 18;
    private static final int ENERGY_BAR_WIDTH = 10;
    private static final int ENERGY_BAR_HEIGHT = 50;

    private static final int PROGRESS_BAR_X = 104;
    private static final int PROGRESS_BAR_Y = 60;
    private static final int PROGRESS_BAR_WIDTH = 8;
    private static final int PROGRESS_BAR_HEIGHT = 16;

    private final ProgressBarArea progressBarArea = new ProgressBarArea();

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
        progressBarArea.updateScreenCoords(this.leftPos, this.topPos);
    }
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight, TEX_W, TEX_H);
        renderProgressBar(guiGraphics, x, y, pMouseX, pMouseY);
        renderEnergyBar(guiGraphics, x, y);
    }
    protected void renderLabels(GuiGraphics guiGraphics, int MouseX, int MouseY){
        String title = this.title.getString();
        int titleX = (this.imageWidth - this.font.width(title)) / 2;
        int titleY = 6;

        guiGraphics.drawString(this.font, title, titleX, titleY, 0x404040, false);
    }
    private void renderEnergyBar(GuiGraphics guiGraphics, int x, int y) {
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
    private void renderProgressBar(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        int progress = menu.getProgress();
        int maxProgress = menu.getMaxProgress();

        int px = x + PROGRESS_BAR_X;
        int py = y + PROGRESS_BAR_Y;

        // Background
        guiGraphics.fill(px - 1, py - 1, px + PROGRESS_BAR_WIDTH + 1, py + PROGRESS_BAR_HEIGHT + 1, 0xFF404040);
        guiGraphics.fill(px, py, px + PROGRESS_BAR_WIDTH, py + PROGRESS_BAR_HEIGHT, 0xFF202020);

        // Progress fill
        if (maxProgress > 0 && progress > 0) {
            int progressHeight = menu.getProgressScaled(PROGRESS_BAR_HEIGHT);
            if (progressHeight > 0) {
                int fillY = py + (PROGRESS_BAR_HEIGHT - progressHeight);
                guiGraphics.fill(px, fillY, px + PROGRESS_BAR_WIDTH, py + PROGRESS_BAR_HEIGHT, 0xFF2B93CC);
            }
        }
        if ( Config.isHighlightEnabled() && progressBarArea.isMouseOver(mouseX, mouseY)) {
            int highlightColor = Config.getHighlightColor();
            // Nacrtaj zlatni outline kada je miš iznad
            guiGraphics.fill(px - 1, py - 1, px + PROGRESS_BAR_WIDTH + 1, py, highlightColor); // gornja linija
            guiGraphics.fill(px - 1, py + PROGRESS_BAR_HEIGHT, px + PROGRESS_BAR_WIDTH + 1, py + PROGRESS_BAR_HEIGHT + 1, highlightColor); // donja linija
            guiGraphics.fill(px - 1, py, px, py + PROGRESS_BAR_HEIGHT, highlightColor); // lijeva linija
            guiGraphics.fill(px + PROGRESS_BAR_WIDTH, py, px + PROGRESS_BAR_WIDTH + 1, py + PROGRESS_BAR_HEIGHT, highlightColor); // desna linija
        }
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
            RecipeType<AlloySmeltingRecipe> recipeType = RecipeType.create(
                    TechRebirth.MODID, "alloy_smelting", AlloySmeltingRecipe.class
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

        // Energy bar tooltip
        int ex = x + ENERGY_BAR_X;
        int ey = y + ENERGY_BAR_Y;
        if (mouseX >= ex && mouseX < ex + ENERGY_BAR_WIDTH && mouseY >= ey && mouseY < ey + ENERGY_BAR_HEIGHT) {
            int energy = menu.getEnergy();
            int max = menu.getMaxEnergy();
            
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.literal(energy + " / " + max + " RF"));
            
            // Calculate estimated usage
            int speedUpgrades = 0;
            int efficiencyUpgrades = 0;
            
            // Check slots 4 and 5 (upgrade slots for alloy smelter)
            // Note: TE slots start at 36. Alloy Smelter has 6 slots.
            // Slots: 0,1,2 (input), 3 (output), 4,5 (upgrades)
            for (int i = 36 + 4; i <= 36 + 5; i++) { 
                if (i < menu.slots.size()) {
                    ItemStack stack = menu.slots.get(i).getItem();
                    if (!stack.isEmpty()) {
                        if (stack.is(ModItems.SPEED_UPGRADE.get())) {
                            speedUpgrades += stack.getCount();
                        } else if (stack.is(ModItems.EFFICIENCY_UPGRADE.get())) {
                            efficiencyUpgrades += stack.getCount();
                        }
                    }
                }
            }
            
            // Match logic from AlloySmelterBlockEntity
            float speedMultiplier = 1.0f + (0.5f * speedUpgrades);
            float efficiencyMultiplier = 1.0f / (1.0f + (0.1f * efficiencyUpgrades));
            int baseCost = 160;
            int estimatedCost = Math.max(1, (int) (baseCost * speedMultiplier * efficiencyMultiplier));
            
            tooltip.add(Component.literal("Usage: " + estimatedCost + " RF/t").withStyle(net.minecraft.ChatFormatting.GRAY));
            
            guiGraphics.renderTooltip(this.font, tooltip, java.util.Optional.empty(), mouseX, mouseY);
        }

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

    private String getProgressStatus() {
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
    private static class ProgressBarArea {
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
