package com.grobe.techrebirth.event;

import com.grobe.techrebirth.Config;
import com.grobe.techrebirth.TechRebirth;
import com.grobe.techrebirth.item.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = TechRebirth.MODID)
public class TooltipEvents {

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        // Pickaxe tooltip
        if (event.getItemStack().is(ModItems.BLAZING_GOLD_PICKAXE.get())) {
            if (Config.AUTO_SMELT_ENABLED.get()) {
                event.getToolTip().add(Component.translatable("tooltip.techrebirth.blazing_gold_pickaxe.auto_smelt")
                        .withStyle(ChatFormatting.GOLD));

                if (Config.AUTO_SMELT_APPLY_FORTUNE.get()) {
                    event.getToolTip().add(Component.translatable("tooltip.techrebirth.blazing_gold_pickaxe.fortune_compatible")
                            .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
                }
            }
        }

        // Armor tooltips
        if (event.getItemStack().is(ModItems.BLAZING_GOLD_HELMET.get()) ||
                event.getItemStack().is(ModItems.BLAZING_GOLD_CHESTPLATE.get()) ||
                event.getItemStack().is(ModItems.BLAZING_GOLD_LEGGINGS.get()) ||
                event.getItemStack().is(ModItems.BLAZING_GOLD_BOOTS.get())) {

            event.getToolTip().add(Component.translatable("tooltip.techrebirth.blazing_gold_armor.set_bonus")
                    .withStyle(ChatFormatting.GOLD));

            event.getToolTip().add(Component.translatable("tooltip.techrebirth.blazing_gold_armor.effect")
                    .withStyle(ChatFormatting.GRAY));
        }

        if (event.getItemStack().is(ModItems.TIN_HELMET.get()) ||
                event.getItemStack().is(ModItems.TIN_CHESTPLATE.get()) ||
                event.getItemStack().is(ModItems.TIN_LEGGINGS.get()) ||
                event.getItemStack().is(ModItems.TIN_BOOTS.get())) {

            event.getToolTip().add(Component.translatable("tooltip.techrebirth.tin_armor.set_bonus")
                    .withStyle(ChatFormatting.GRAY));

            if(Screen.hasShiftDown()){
                event.getToolTip().add(Component.translatable("tooltip.techrebirth.tin_armor.effect")
                        .withStyle(ChatFormatting.GOLD));
            }

        }
    }
}