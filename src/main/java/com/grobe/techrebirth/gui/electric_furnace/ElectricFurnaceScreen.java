package com.grobe.techrebirth.gui.electric_furnace;

import com.grobe.techrebirth.TechRebirth;
import com.grobe.techrebirth.compat.jei.JEITechRebirthPlugin;
import com.grobe.techrebirth.gui.BaseMachineScreen;
import com.grobe.techrebirth.gui.subscreen.ConfigSubScreen;
import com.grobe.techrebirth.gui.subscreen.UpgradeSubScreen;
import com.grobe.techrebirth.gui.widgets.ConfigButton;
import com.mojang.blaze3d.systems.RenderSystem;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.crafting.SmeltingRecipe;

import java.util.List;

public class ElectricFurnaceScreen extends BaseMachineScreen<ElectricFurnaceMenu> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "textures/gui/electric_furnace_gui.png");

    // Actual texture dimensions
    private static final int TEX_W = 176;
    private static final int TEX_H = 166;

    private static final int PROGRESS_BAR_X = 76;
    private static final int PROGRESS_BAR_Y = 53;
    private static final int PROGRESS_BAR_WIDTH = 8;
    private static final int PROGRESS_BAR_HEIGHT = 16;


    public ElectricFurnaceScreen(ElectricFurnaceMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, TEXTURE, PROGRESS_BAR_X, PROGRESS_BAR_Y, PROGRESS_BAR_WIDTH, PROGRESS_BAR_HEIGHT, true, true);
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
    }

}
