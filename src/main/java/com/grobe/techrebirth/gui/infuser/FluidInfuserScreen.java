package com.grobe.techrebirth.gui.infuser;

import com.grobe.techrebirth.TechRebirth;
import com.grobe.techrebirth.gui.BaseMachineScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class FluidInfuserScreen extends BaseMachineScreen<FluidInfuserMenu> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "textures/gui/fluid_infuser_gui.png");

    private static final int FLUID_TANK_X = 26;
    private static final int FLUID_TANK_Y = 18;
    private static final int FLUID_TANK_WIDTH = 10;
    private static final int FLUID_TANK_HEIGHT = 50;

    public FluidInfuserScreen(FluidInfuserMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, TEXTURE, 75, 40, 20, 8, false, true);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        super.renderBg(guiGraphics, pPartialTick, pMouseX, pMouseY);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        renderFluidTank(guiGraphics, x + FLUID_TANK_X, y + FLUID_TANK_Y);
    }

    private void renderFluidTank(GuiGraphics guiGraphics, int x, int y) {
        FluidStack fluidStack = menu.getFluidStack();
        if (fluidStack.isEmpty()) {
            // Background for empty tank
            guiGraphics.fill(x, y, x + FLUID_TANK_WIDTH, y + FLUID_TANK_HEIGHT, 0xFF101010);
            return;
        }

        int capacity = menu.getFluidCapacity();
        int amount = fluidStack.getAmount();
        int height = (int) (FLUID_TANK_HEIGHT * ((float) amount / capacity));

        if (height > 0) {
            IClientFluidTypeExtensions props = IClientFluidTypeExtensions.of(fluidStack.getFluid());
            ResourceLocation still = props.getStillTexture(fluidStack);
            if (still != null) {
                TextureAtlasSprite sprite = net.minecraft.client.Minecraft.getInstance()
                        .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                        .apply(still);

                int color = props.getTintColor(fluidStack);
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderColor(
                        ((color >> 16) & 0xFF) / 255f,
                        ((color >> 8) & 0xFF) / 255f,
                        (color & 0xFF) / 255f,
                        ((color >> 24) & 0xFF) / 255f
                );
                RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);

                // Render fluid in segments if necessary, or just tiled
                int fillY = y + FLUID_TANK_HEIGHT - height;
                guiGraphics.blit(x, fillY, 0, FLUID_TANK_WIDTH, height, sprite);
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            }
        }

        // Border
        guiGraphics.renderOutline(x - 1, y - 1, FLUID_TANK_WIDTH + 2, FLUID_TANK_HEIGHT + 2, 0xFF202020);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        super.render(guiGraphics, mouseX, mouseY, delta);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        if (isHovering(FLUID_TANK_X, FLUID_TANK_Y, FLUID_TANK_WIDTH, FLUID_TANK_HEIGHT, mouseX, mouseY)) {
            FluidStack fluidStack = menu.getFluidStack();
            List<Component> tooltip = new ArrayList<>();
            if (fluidStack.isEmpty()) {
                tooltip.add(Component.translatable("gui.techrebirth.empty"));
            } else {
                tooltip.add(fluidStack.getHoverName());
                tooltip.add(Component.literal(fluidStack.getAmount() + " / " + menu.getFluidCapacity() + " mB")
                        .withStyle(net.minecraft.ChatFormatting.GRAY));
            }
            guiGraphics.renderTooltip(this.font, tooltip, java.util.Optional.empty(), mouseX, mouseY);
        }
    }
}
