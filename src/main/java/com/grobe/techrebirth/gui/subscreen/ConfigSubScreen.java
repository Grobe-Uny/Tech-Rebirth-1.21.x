package com.grobe.techrebirth.gui.subscreen;

import com.grobe.techrebirth.TechRebirth;
import com.grobe.techrebirth.block.custom.entity.BaseMachineBlockEntity;
import com.grobe.techrebirth.gui.widgets.DraggableSubScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ConfigSubScreen extends DraggableSubScreen {

    public ConfigSubScreen(Screen parent, BaseMachineBlockEntity machine) {
        super(parent,
                ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "textures/gui/config_window.png"),
                180, 160,
                Component.translatable("gui.techrebirth.config"));
    }

    @Override
    protected void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Implement later
        guiGraphics.drawString(font, "Side Configuration", x + 10, y + 40, 0xFFFFFF);
        guiGraphics.drawString(font, "Coming Soon...", x + 10, y + 60, 0xAAAAAA);
    }
}