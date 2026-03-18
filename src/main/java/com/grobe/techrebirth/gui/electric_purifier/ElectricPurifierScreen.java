package com.grobe.techrebirth.gui.electric_purifier;

import com.grobe.techrebirth.TechRebirth;
import com.grobe.techrebirth.compat.jei.JEITechRebirthPlugin;
import com.grobe.techrebirth.gui.BaseMachineScreen;
import com.grobe.techrebirth.recipe.PurifierRecipe;
import com.mojang.blaze3d.systems.RenderSystem;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public class ElectricPurifierScreen extends BaseMachineScreen<ElectricPurifierMenu> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "textures/gui/electric_purifier_gui.png");


    private static final int PROGRESS_BAR_X = 75;
    private static final int PROGRESS_BAR_Y = 40;
    private static final int PROGRESS_BAR_WIDTH = 20;
    private static final int PROGRESS_BAR_HEIGHT = 8;


    public ElectricPurifierScreen(ElectricPurifierMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, TEXTURE, PROGRESS_BAR_X, PROGRESS_BAR_Y, PROGRESS_BAR_WIDTH, PROGRESS_BAR_HEIGHT, false, true);
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

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0,imageWidth, imageHeight, TEX_W, TEX_H);

        renderEnergyBar(guiGraphics,x,y);
        renderProgressBar(guiGraphics,x,y,pMouseX, pMouseY);
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
            RecipeType<PurifierRecipe> recipeType = RecipeType.create(
                    TechRebirth.MODID, "purifying", PurifierRecipe.class
            );
            jei.getRecipesGui().showTypes(List.of(recipeType));
        }
    }


    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics, mouseX, mouseY, delta);
        super.render(guiGraphics, mouseX, mouseY, delta);

//        int x = (width - imageWidth) / 2;
//        int y = (height - imageHeight) / 2;
//
//
//        // Progress bar tooltip
//        int px = x + PROGRESS_BAR_X;
//        int py = y + PROGRESS_BAR_Y;
//        if (mouseX >= px && mouseX < px + PROGRESS_BAR_WIDTH && mouseY >= py && mouseY < py + PROGRESS_BAR_HEIGHT) {
//            String status = getProgressStatus();
//            Component tooltip = Component.literal(status + "\n§aClick to view recipes in JEI");
//            guiGraphics.renderTooltip(this.font, tooltip, mouseX, mouseY);
//        }
//
//        // Energy bar tooltip with usage
//        int ex = x + ENERGY_BAR_X;
//        int ey = y + ENERGY_BAR_Y;
//        if (mouseX >= ex && mouseX < ex + ENERGY_BAR_WIDTH && mouseY >= ey && mouseY < ey + ENERGY_BAR_HEIGHT) {
//            int energy = menu.getEnergy();
//            int max = menu.getMaxEnergy();
//
//            List<Component> tooltip = new ArrayList<>();
//            tooltip.add(Component.literal(energy + " / " + max + " RF"));
//
//            // Calculate estimated usage
//            int speedUpgrades = 0;
//            int efficiencyUpgrades = 0;
//
//            // Check slots 2 and 3 (upgrade slots for purifier)
//            for (int i = 36 + 2; i <= 36 + 3; i++) {
//                if (i < menu.slots.size()) {
//                    ItemStack stack = menu.slots.get(i).getItem();
//                    if (!stack.isEmpty()) {
//                        if (stack.is(ModItems.SPEED_UPGRADE.get())) {
//                            speedUpgrades += stack.getCount();
//                        } else if (stack.is(ModItems.EFFICIENCY_UPGRADE.get())) {
//                            efficiencyUpgrades += stack.getCount();
//                        }
//                    }
//                }
//            }
//
//            float speedMultiplier = 1.0f + (0.5f * speedUpgrades);
//            float efficiencyMultiplier = 1.0f / (1.0f + (0.1f * efficiencyUpgrades));
//            int baseCost = 40;
//            int estimatedCost = Math.max(1, (int) (baseCost * speedMultiplier * efficiencyMultiplier));
//
//            tooltip.add(Component.literal("Usage: " + estimatedCost + " RF/t").withStyle(net.minecraft.ChatFormatting.GRAY));
//
//            guiGraphics.renderTooltip(this.font, tooltip, java.util.Optional.empty(), mouseX, mouseY);
//        }
//
//        renderTooltip(guiGraphics, mouseX, mouseY);
    }

}
