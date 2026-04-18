package com.grobe.techrebirth.event;

import com.grobe.techrebirth.TechRebirth;
import com.grobe.techrebirth.client.EnergyHUDOverlay;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(modid = TechRebirth.MODID, value = Dist.CLIENT)
public class ModClientEvents {

    private static final EnergyHUDOverlay HUD = new EnergyHUDOverlay();

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        // Pozivamo render metodu s parametrima prozora
        HUD.render(
                event.getGuiGraphics(),
                event.getPartialTick(),
                event.getGuiGraphics().guiWidth(),
                event.getGuiGraphics().guiHeight()
        );
    }
}