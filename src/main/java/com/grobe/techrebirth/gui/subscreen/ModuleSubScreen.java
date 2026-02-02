package com.grobe.techrebirth.gui.subscreen;

import com.grobe.techrebirth.TechRebirth;
import com.grobe.techrebirth.block.custom.entity.BaseMachineBlockEntity;
import com.grobe.techrebirth.gui.widgets.DraggableSubScreen;
import com.grobe.techrebirth.item.ModItems;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ModuleSubScreen extends DraggableSubScreen {

    private static final ResourceLocation BACKGROUND =
            ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "textures/gui/upgrade_window.png");

    private final BaseMachineBlockEntity machine;
    private UpgradeSlot[] upgradeSlots = new UpgradeSlot[4];

    public ModuleSubScreen(Screen parent, BaseMachineBlockEntity machine) {
        super(parent, BACKGROUND, 200, 150,
                Component.translatable("gui.techrebirth.upgrades"));
        this.machine = machine;
    }

    @Override
    protected void init() {
        super.init();

        // Create upgrade slots (2x2 grid)
        int slotSize = 24;
        int startX = x + 30;
        int startY = y + 40;

        for (int i = 0; i < upgradeSlots.length; i++) {
            int row = i / 2;
            int col = i % 2;

            int slotX = startX + col * (slotSize + 10);
            int slotY = startY + row * (slotSize + 10);

            upgradeSlots[i] = addRenderableWidget(new UpgradeSlot(
                    slotX, slotY, slotSize, i,
                    Component.translatable("gui.techrebirth.upgrade_slot." + i)
            ));
        }
    }

    @Override
    protected void renderContent (GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick){
        // render title
        guiGraphics.drawCenteredString(font,
                Component.translatable("gui.techrebirth.modules"),
                x + width /2, y + 30, 0xFFFFFFFF);

        // render slot backgrounds
        for(UpgradeSlot slot : upgradeSlots){
            slot.render(guiGraphics, mouseX, mouseY, partialTick);
        }

        //render stats
        renderStats(guiGraphics);
    }

    private void renderStats(GuiGraphics guiGraphics){
        int statsY = y + 120;

        // Speed upgrades
        int speedUpgrades = countUpgrades(ModItems.SPEED_UPGRADE.get());
        guiGraphics.drawString(font,
                Component.translatable("gui.techrebirth.speed_bonus", "+" + (speedUpgrades * 15) + "%"),
                x + 10, statsY, 0xFFFFFF, false);

        // Efficiency upgrades
        int efficiencyUpgrades = countUpgrades(ModItems.EFFICIENCY_UPGRADE.get());
        guiGraphics.drawString(font,
                Component.translatable("gui.techrebirth.efficiency_bonus", "-" + (efficiencyUpgrades * 10) + "%"),
                x + 10, statsY + 12, 0xFFFFFF, false);

        // Tier info
        guiGraphics.drawString(font,
                Component.translatable("gui.techrebirth.machine_tier", machine.getName()),
                x + width - 100, statsY, 0xFFFF00, false);
    }

    private int countUpgrades(Item upgradeItem) {
        // TODO: Count upgrades from machine
        return 0;
    }

    private class UpgradeSlot extends AbstractWidget{
        private final int slotIndex;
        private ItemStack currentItem = ItemStack.EMPTY;

        public UpgradeSlot(int x, int y, int size, int slotIndex, Component message){
            super(x,y, size, size, message);
            this.slotIndex = slotIndex;
        }

        @Override
        public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick){
            // render slot background
            guiGraphics.fill(getX(), getY(), getX() + width, getY() + height, 0xFF8B8B8B);
            guiGraphics.renderOutline(getX(), getY(), width, height, 0xFF555555);

            //render item if present
            if(!currentItem.isEmpty()){
                guiGraphics.renderFakeItem(currentItem, getX() + 4, getY() + 4);
            }

            // hover effect
            if(isHovered()){
                guiGraphics.fill(getX(), getY(), getX() + width, getY() + height, 0x80FFFFFF);

                // show tooltip
                if(!currentItem.isEmpty()){
                    guiGraphics.renderTooltip(font, currentItem.getHoverName(), mouseX, mouseY);
                }
            }
        }
        @Override
        public void onClick(double mouseX, double mouseY) {
            // Handle upgrade insertion/removal
            // TODO: Implement upgrade handling
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
            // Narration support
        }
    }
}
