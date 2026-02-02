package com.grobe.techrebirth.gui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;

public class ConfigButton extends ExtendedButton {
    private final ResourceLocation texture;
    private final int texX, texY;

    public ConfigButton(int x, int y, int width, int height, ResourceLocation texture, int texX, int texY, Component message, OnPress onPress){
        super(x,y, width, height, message, onPress);
        this.texture = texture;
        this.texX = texX;
        this.texY = texY;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick){
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        //render background
        guiGraphics.blit(texture, getX(), getY(), texX, texY, width, height);

        //Hover effect
        if(isHovered()){
            guiGraphics.fill(getX(), getY(), getX() + width, getY() + height, 0x080FFFFFF);// semi transparent overlay
        }
//
//        //Render Tooltip
//        if(isHovered() && getMessage() != null){
//            guiGraphics.renderTooltip(font, getMessage(), mouseX, mouseY);
//        }
    }

}
