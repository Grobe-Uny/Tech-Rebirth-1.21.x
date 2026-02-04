package com.grobe.techrebirth.gui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
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
        // Dodano: spremanje širine i visine parent screena
        private int parentWidth = 0;
        private int parentHeight = 0;


        public DraggableSubScreen(Screen parent, ResourceLocation background, int width, int height, Component title){
            super(title);
            this.parentScreen = parent;
            this.background = background;
            this.width = width;
            this.height = height;

            if(parent != null){
                this.minecraft = parent.getMinecraft();
            }

        }
        public void setVisible(boolean visible){
            if (this.visible == visible) return;

            this.visible = visible;

            if (visible) {
                // VAŽNO: Osiguraj da je Minecraft instanca postavljena
                if (this.minecraft == null && parentScreen != null) {
                    this.minecraft = parentScreen.getMinecraft();
                }

                // Inicijaliziraj ako je potrebno
                if (this.minecraft != null) {
                    // Postavi širinu i visinu iz parent screena
                    this.parentWidth = parentScreen.width;
                    this.parentHeight = parentScreen.height;

                    // Ponovno pozovi init() da se ažuriraju pozicije widgeta
                    this.init();
                }
            } else {
                // Resetiraj stanje povlačenja kada se sakrije
                dragging = false;
            }
        }
        public boolean isVisible(){ return visible;}

        @Override
        protected void init(){

            // Samo pozovi super.init() - on će postaviti potrebne stvari
            super.init();

            // Sada možemo postaviti poziciju jer imamo širinu i visinu
            if (parentScreen != null) {
                this.parentWidth = parentScreen.width;
                this.parentHeight = parentScreen.height;

                // Centriraj samo ako nije već postavljeno
                if (x == 0 && y == 0) {
                    this.x = (parentWidth - width) / 2;
                    this.y = (parentHeight - height) / 2;
                }

                // Close button (X u gornjem desnom kutu)
                this.addRenderableWidget(
                        Button.builder(Component.literal("X"),
                                        button -> {
                                            this.minecraft.setScreen(parentScreen);
                                        })
                                .pos(x + width - 25, y + 5)
                                .size(20, 20)
                                .build()
                );

            }
         }


//        public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, int partialTick){
//            if(!visible) return;
//
//
//            // Osiguraj da je inicijaliziran
//            if (!initialized && minecraft != null) {
//                super.init(minecraft, parentScreen.width, parentScreen.height);
//                initialized = true;
//            }
//
//            // Render draggable window
//            RenderSystem.enableBlend();
//            RenderSystem.defaultBlendFunc();
//
//            // Background
//            guiGraphics.blit(background, x, y, width, height,
//                    4, 4, 4, 4, 256, 256);
//
//
//            // Title bar (drag area)
//            guiGraphics.fill(x, y, x + width, y + 20, 0xFF333333);
//            guiGraphics.drawString(font, getTitle(), x + 10, y + 6, 0xFFFFFFFF);
//
//            // Close button (X)
//            guiGraphics.fill(x + width - 20, y, x + width, y + 20, 0xFF555555);
//            guiGraphics.drawCenteredString(font, "X", x + width - 10, y + 6, 0xFFFFFFFF);
//
//            // Render content
//            renderContent(guiGraphics, mouseX, mouseY, partialTick);
//
//            // Border
//            guiGraphics.renderOutline(x, y, width, height, 0xFFAAAAAA);
//
//            super.render(guiGraphics, mouseX, mouseY, partialTick);
//        }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 1. Polu-transparentna crna pozadina preko cijelog ekrana
        guiGraphics.fill(0, 0, this.width, this.height, 0x88000000);

        // 2. Render prozora
        renderWindow(guiGraphics, mouseX, mouseY, partialTick);

        // 3. OBAVEZNO pozovi super za widgete
        super.render(guiGraphics, mouseX, mouseY, partialTick);

    }

    protected void renderWindow(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Bijela pozadina prozora
        guiGraphics.fill(x, y, x + width, y + height, 0xFF333333);

        // Bijeli okvir
        guiGraphics.renderOutline(x, y, width, height, 0xFFFFFFFF);

        // Siva traka za naslov (gornjih 30px)
        guiGraphics.fill(x, y, x + width, y + 30, 0xFF555555);

        // Naslov u traci
        guiGraphics.drawCenteredString(font, this.title,
                x + width/2, y + 10, 0xFFFFFF);
    }
    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Ne renderiraj default pozadinu
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

    // Dodano: Getter za parent dimenzije
    public int getParentWidth() {
        return parentWidth;
    }

    public int getParentHeight() {
        return parentHeight;
    }
}
