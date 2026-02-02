package com.grobe.techrebirth.gui.subscreen;

import com.grobe.techrebirth.TechRebirth;
import com.grobe.techrebirth.block.custom.entity.BaseMachineBlockEntity;
import com.grobe.techrebirth.gui.widgets.DraggableSubScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class UpgradeSubScreen extends DraggableSubScreen {

    public UpgradeSubScreen(Screen parent, BaseMachineBlockEntity machine) {
        super(parent,
                ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "textures/gui/upgrade_window.png"),
                200, 150,
                Component.translatable("gui.techrebirth.upgrades"));
    }

    @Override
    protected void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Implement later
        guiGraphics.drawString(font, "Main Upgrades", x + 10, y + 40, 0xFFFFFF);
        guiGraphics.drawString(font, "Coming Soon...", x + 10, y + 60, 0xAAAAAA);
    }
}