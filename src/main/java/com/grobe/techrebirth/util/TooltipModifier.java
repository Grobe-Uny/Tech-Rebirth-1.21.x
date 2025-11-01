package com.grobe.techrebirth.util;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.List;

public class TooltipModifier {


    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        Level level = event.getEntity() != null ? event.getEntity().level() : null;

        if (level == null || !event.getFlags().isAdvanced()) return;

        // ✅ DODAJ TAGOVE kada je Ctrl pritisnut
        if (Screen.hasControlDown()) {
            addTagTooltips(stack, event.getToolTip(), level);
        }
    }

    private static void addTagTooltips(ItemStack stack, List<Component> tooltip, Level level) {
        HolderLookup.RegistryLookup<Item> items = level.registryAccess().lookupOrThrow(Registries.ITEM);

        // Prikaži sve tagove itema
        items.listElements()
                .filter(holder -> holder.value() == stack.getItem())
                .forEach(holder -> {
                    holder.tags().forEach(tagKey -> {
                        tooltip.add(Component.literal("§7# " + tagKey.location()));
                    });
                });
    }
}
