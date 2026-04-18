package com.grobe.techrebirth.item.custom.util;

import com.grobe.techrebirth.registration.ModComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;

import java.util.ArrayList;
import java.util.List;

public class EnergyInformatorItem extends Item {
    public EnergyInformatorItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext context){
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        ItemStack stack = context.getItemInHand();

        if (!level.isClientSide && level.getCapability(Capabilities.EnergyStorage.BLOCK, pos, null) != null) {
            // Dohvati trenutnu listu (ili praznu ako je nema)
            List<BlockPos> currentPos = new ArrayList<>(stack.getOrDefault(ModComponents.TRACKED_POSITIONS, List.of()));

            if (currentPos.contains(pos)) {
                currentPos.remove(pos); // Toggle: makni ako već postoji
                context.getPlayer().displayClientMessage(Component.literal("Blok uklonjen!"), true);
            } else {
                currentPos.add(pos); // Dodaj novi
                context.getPlayer().displayClientMessage(Component.literal("Blok dodan!"), true);
            }

            // Spremi novu listu u item
            stack.set(ModComponents.TRACKED_POSITIONS, List.copyOf(currentPos));
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    // Pomoćna metoda koju ModEvents koristi
    public List<BlockPos> getSavedPositions(ItemStack stack) {
        return stack.getOrDefault(ModComponents.TRACKED_POSITIONS, List.of());
    }
}
