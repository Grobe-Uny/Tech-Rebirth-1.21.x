package com.grobe.techrebirth.gui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public abstract class DraggableSubScreen extends Screen {
        protected int x,y;
        protected int height, width;
        protected boolean dragging = false;
        protected int dragOffsetX, dragOffsetY;
        protected final ResourceLocation background;
        protected final Screen parentScreen;
        protected boolean visible = false;
        protected boolean initialized = false;

        public DraggableSubScreen(Screen parent, ResourceLocation background, int width, int height, Component title){
            super(title);
            this.parentScreen = parent;
            this.background = background;
            this.width = width;
            this.height = height;

            //Start out centered (default position)
            if(parent != null){
                this.x = (parent.width - width) / 2;
                this.y = (parent.height - height) / 2;
            }

        }
        public void setVisible(boolean visible){
            this.visible = visible;
            if(visible){
                init(minecraft, parentScreen.width, parentScreen.height);
            }
        }
        public boolean isVisible(){ return visible;}

        @Override
        protected void init(){
            initialized = false;
         }


        public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, int partialTick){
            if(!visible) return;


            // Osiguraj da je inicijaliziran
            if (!initialized && minecraft != null) {
                super.init(minecraft, parentScreen.width, parentScreen.height);
                initialized = true;
            }

            // Background
            guiGraphics.blit(background, x, y, width, height,
                    4, 4, 4, 4, 256, 256);

            // Render draggable window
            RenderSystem.enableBlend();

            // Title bar (drag area)
            guiGraphics.fill(x, y, x + width, y + 20, 0xFF333333);
            guiGraphics.drawString(font, getTitle(), x + 10, y + 6, 0xFFFFFFFF);

            // Close button (X)
            guiGraphics.fill(x + width - 20, y, x + width, y + 20, 0xFF555555);
            guiGraphics.drawCenteredString(font, "X", x + width - 10, y + 6, 0xFFFFFFFF);

            // Render content
            renderContent(guiGraphics, mouseX, mouseY, partialTick);

            // Border
            guiGraphics.renderOutline(x, y, width, height, 0xFFAAAAAA);
        }

        protected abstract void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick);

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button){
            if(!visible) return false;

            // Check close button
            if (mouseX >= x + width - 20 && mouseX <= x + width &&
                    mouseY >= y && mouseY <= y + 20) {
                setVisible(false);
                return true;
            }

            // Check title bar for dragging
            if (mouseX >= x && mouseX <= x + width &&
                    mouseY >= y && mouseY <= y + 20) {
                dragging = true;
                dragOffsetX = (int) (mouseX - x);
                dragOffsetY = (int) (mouseY - y);
                return true;
            }

            // Check inside content
            if (mouseX >= x && mouseX <= x + width &&
                    mouseY >= y + 20 && mouseY <= y + height) {
                return super.mouseClicked(mouseX, mouseY, button);
            }

            return false;
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
                if (!visible) return false;

                 dragging = false;
                 return super.mouseReleased(mouseX, mouseY, button);
        }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (!visible) return false;

        if (dragging) {
            x = Mth.clamp((int) (mouseX - dragOffsetX), 0, parentScreen.width - width);
            y = Mth.clamp((int) (mouseY - dragOffsetY), 0, parentScreen.height - height);
            return true;
        }

        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return visible && mouseX >= x && mouseX <= x + width &&
                mouseY >= y && mouseY <= y + height;
    }

    public void bringToFront() {
        // Ova metoda može biti prazna ili možete implementirati Z-order logiku
        // Za sada je dovoljno da postoji
    }
}
