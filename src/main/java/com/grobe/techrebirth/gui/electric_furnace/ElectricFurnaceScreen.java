package com.grobe.techrebirth.gui.electric_furnace;

import com.grobe.techrebirth.Config;
import com.grobe.techrebirth.TechRebirth;
import com.grobe.techrebirth.compat.jei.JEITechRebirthPlugin;
import com.grobe.techrebirth.gui.BaseMachineScreen;
import com.grobe.techrebirth.gui.electric_crusher.ElectricCrusherScreen;
import com.grobe.techrebirth.gui.subscreen.ConfigSubScreen;
import com.grobe.techrebirth.gui.subscreen.UpgradeSubScreen;
import com.grobe.techrebirth.gui.widgets.ConfigButton;
import com.grobe.techrebirth.item.ModItems;
import com.grobe.techrebirth.item.custom.UpgradeItem;
import com.grobe.techrebirth.recipe.CrushingRecipe;
import com.mojang.blaze3d.systems.RenderSystem;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SmeltingRecipe;

import java.util.ArrayList;
import java.util.List;

public class ElectricFurnaceScreen extends BaseMachineScreen<ElectricFurnaceMenu> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "textures/gui/electric_furnace_gui.png");

    // Actual texture dimensions
    private static final int TEX_W = 176;
    private static final int TEX_H = 166;

    private static final int ENERGY_BAR_X = 10; // relative to GUI x
    private static final int ENERGY_BAR_Y = 18; // relative to GUI y
    private static final int ENERGY_BAR_WIDTH = 10;
    private static final int ENERGY_BAR_HEIGHT = 50;

    private static final int PROGRESS_BAR_X = 76;
    private static final int PROGRESS_BAR_Y = 53;
    private static final int PROGRESS_BAR_WIDTH = 8;
    private static final int PROGRESS_BAR_HEIGHT = 16;

    private final ProgressBarArea progressBarArea = new ProgressBarArea();

    public ElectricFurnaceScreen(ElectricFurnaceMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, TEXTURE);
        this.imageWidth = TEX_W;
        this.imageHeight = TEX_H;
    }

    private ConfigSubScreen configScreen;
    private UpgradeSubScreen upgradeScreen;
    private AbstractWidget configButton, upgradeButton;


    @Override
    protected void init() {
        super.init();
        this.inventoryLabelY = 10000;
        this.titleLabelY = 10000;

        int buttonX = this.leftPos + this.imageWidth + 10; // 10 piksela desno od GUI-a
        int buttonY = this.topPos + 10; // 10 piksela od vrha


        // Config button
        configButton = addRenderableWidget(new ConfigButton(
                buttonX, buttonY, 20, 20,
                ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "textures/gui/buttons/config.png"),
                0, 0,
                Component.translatable("gui.techrebirth.config"),
                button -> openConfigScreen()
        ));

        // Upgrade button
        upgradeButton = addRenderableWidget(new ConfigButton(
                buttonX, buttonY + 25, 20, 20,
                ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "textures/gui/buttons/upgrade.png"),
                0, 0,
                Component.translatable("gui.techrebirth.upgrades"),
                button -> openUpgradeScreen()
        ));

        // Inicijaliziraj sub-screens
        configScreen = new ConfigSubScreen(this, menu.blockEntity);
        upgradeScreen = new UpgradeSubScreen(this, menu.blockEntity);


        progressBarArea.updateScreenCoords(this.leftPos, this.topPos);
    }


    private void openConfigScreen() {
        configScreen.setVisible(true);
        configScreen.bringToFront();
    }

    private void openUpgradeScreen() {
        upgradeScreen.setVisible(true);
        upgradeScreen.bringToFront();
    }
    @Override
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

    private void renderProgressBar(GuiGraphics gg, int x, int y, int mouseX, int mouseY) {
        int progress = menu.getProgress();
        int maxProgress = menu.getMaxProgress();

        int px = x + PROGRESS_BAR_X;
        int py = y + PROGRESS_BAR_Y;

        // Background
        gg.fill(px - 1, py - 1, px + PROGRESS_BAR_WIDTH + 1, py + PROGRESS_BAR_HEIGHT + 1, 0xFF404040);
        gg.fill(px, py, px + PROGRESS_BAR_WIDTH, py + PROGRESS_BAR_HEIGHT, 0xFF202020);

        // Progress fill
        if (maxProgress > 0 && progress > 0) {
            int progressHeight = menu.getScaledProgress(PROGRESS_BAR_HEIGHT);
            if (progressHeight > 0) {
                int fillY = py + (PROGRESS_BAR_HEIGHT - progressHeight);
                gg.fill(px, fillY, px + PROGRESS_BAR_WIDTH, py + PROGRESS_BAR_HEIGHT, 0xFF2B93CC);
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
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        // Prvo provjeri sub-screens
        if (configScreen.isVisible() && configScreen.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (upgradeScreen.isVisible() && upgradeScreen.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        if (button == 0 && progressBarArea.isMouseOver(mouseX, mouseY)) {
            showJEIRecipes();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    private void showJEIRecipes() {
        IJeiRuntime jei = JEITechRebirthPlugin.getJeiRuntime();
        if (jei != null) {
            RecipeType<SmeltingRecipe> recipeType = RecipeType.create(
                    TechRebirth.MODID, "electric_smelting", SmeltingRecipe.class
            );
            jei.getRecipesGui().showTypes(List.of(recipeType));
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        // Prvo sub-screens
        if (configScreen.isVisible() && configScreen.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
            return true;
        }
        if (upgradeScreen.isVisible() && upgradeScreen.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
            return true;
        }

        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

//
//    private void renderEnergyBar(GuiGraphics guiGraphics, int x, int y) {
//        int ex = x + ENERGY_BAR_X;
//        int ey = y + ENERGY_BAR_Y;
//        // Draw background (dark gray border)
//        int border = 0xFF202020;
//        guiGraphics.fill(ex - 1, ey - 1, ex + ENERGY_BAR_WIDTH + 1, ey + ENERGY_BAR_HEIGHT + 1, border);
//        // Draw an inner background (almost black)
//        int bg = 0xFF101010;
//        guiGraphics.fill(ex, ey, ex + ENERGY_BAR_WIDTH, ey + ENERGY_BAR_HEIGHT, bg);
//        // Draw energy amount (red, from bottom up)
//        int filled = menu.getScaledEnergy(ENERGY_BAR_HEIGHT);
//        if (filled > 0) {
//            int fy = ey + (ENERGY_BAR_HEIGHT - filled);
//            int color = 0xFFCC2B2B; // red
//            guiGraphics.fill(ex, fy, ex + ENERGY_BAR_WIDTH, ey + ENERGY_BAR_HEIGHT, color);
//        }
//    }
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics, mouseX, mouseY, delta);
        super.render(guiGraphics, mouseX, mouseY, delta);

        // Render sub-screens iznad
        if (configScreen.isVisible()) {
            configScreen.render(guiGraphics, mouseX, mouseY, delta);
        }
        if (upgradeScreen.isVisible()) {
            upgradeScreen.render(guiGraphics, mouseX, mouseY, delta);
        }

        // Tooltip for energy bar on hover (same as generator)
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        int ex = x + ENERGY_BAR_X;
        int ey = y + ENERGY_BAR_Y;
        if (mouseX >= ex && mouseX < ex + ENERGY_BAR_WIDTH && mouseY >= ey && mouseY < ey + ENERGY_BAR_HEIGHT) {
            int energy = menu.getEnergyStored();
            int max = menu.getMaxEnergyStored();
            
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.literal(energy + " / " + max + " RF"));
            
            // Calculate estimated usage
            int speedUpgrades = 0;
            int efficiencyUpgrades = 0;
            
            // Check slots 2 and 3 (upgrade slots)
            // Note: We access the menu's slots directly, which are synced to client
            for (int i = 36 + 2; i <= 36 + 3; i++) { // TE slots start at 36
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
            
            // Match logic from ElectricFurnaceBlockEntity
            // Speed: +50% speed, +50% power (Linear)
            // Efficiency: -10% power (Hyperbolic)
            
            float speedPenalty = 1.0f + (0.5f * speedUpgrades);
            float efficiencyBonus = 1.0f / (1.0f + (0.1f * efficiencyUpgrades));
            
            int baseCost = 20;
            int estimatedCost = Math.max(1, (int) (baseCost * speedPenalty * efficiencyBonus));
            
            tooltip.add(Component.literal("Usage: " + estimatedCost + " RF/t").withStyle(net.minecraft.ChatFormatting.GRAY));

            guiGraphics.renderTooltip(this.font, tooltip, java.util.Optional.empty(), mouseX, mouseY);
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
