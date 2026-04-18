package com.grobe.techrebirth.client;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public class EnergyHUDOverlay {

    public void render(GuiGraphics graphics, DeltaTracker partialTick, int width, int height) {
        Minecraft mc = Minecraft.getInstance();

        // Prikazuj samo ako igrač postoji i ako monitor ima podatke
        if (mc.player == null || ClientPacketHandler.max <= 0) return;

        int x = 10;
        int y = 10;

        // 1. Pozadina bara (Tamno siva)
        graphics.fill(x, y, x + 100, y + 8, 0xFF333333);

        // 2. Punjenje bara (Plava)
        float fillRatio = (float) ClientPacketHandler.stored / ClientPacketHandler.max;
        int fillWidth = (int) (fillRatio * 100);
        graphics.fill(x, y, x + fillWidth, y + 8, 0xFF4287f5);

        // 3. Tekstualni podaci
        String genText = "Generating: +" + formatEnergy(ClientPacketHandler.gen) + " FE/t";
        String spendText = "Spending: -" + formatEnergy(ClientPacketHandler.spend) + " FE/t";
        String storedText = formatEnergy(ClientPacketHandler.stored) + " / " + formatEnergy(ClientPacketHandler.max) + " FE";

        graphics.drawString(mc.font, storedText, x, y + 12, 0xFFFFFF);
        graphics.drawString(mc.font, genText, x, y + 22, 0x55FF55); // Zelena
        graphics.drawString(mc.font, spendText, x, y + 32, 0xFF5555); // Crvena
    }

    private String formatEnergy(long amount) {
        if (amount >= 1000000) return String.format("%.1fM", amount / 1000000.0);
        if (amount >= 1000) return String.format("%.1fk", amount / 1000.0);
        return String.valueOf(amount);
    }
}