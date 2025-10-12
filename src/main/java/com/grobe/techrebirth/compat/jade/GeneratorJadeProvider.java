package com.grobe.techrebirth.compat.jade;

import com.grobe.techrebirth.TechRebirth;
import com.grobe.techrebirth.block.custom.entity.GeneratorBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.config.IPluginConfig;

/**
 * Jade provider for the Generator block.
 *
 * Shows in the tooltip:
 * - Energy: current / max (FE)
 * - Generation: X FE/t
 * - Fuel: <item id> x<count> (and remaining seconds if burning)
 *
 * Server gathers data in appendServerData; client formats it in appendTooltip.
 */
public enum GeneratorJadeProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    // Unique id for registration
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "generator");

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    // CLIENT: Build tooltip lines using server-provided data
    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag tag = accessor.getServerData();
        if (tag == null) return;

        if (tag.contains("energy") && tag.contains("capacity")) {
            int energy = tag.getInt("energy");
            int cap = tag.getInt("capacity");
            tooltip.add(Component.literal(String.format("Energy: %,d / %,d FE", energy, cap)));
        }
        if (tag.contains("genPerTick")) {
            int gpt = tag.getInt("genPerTick");
            tooltip.add(Component.literal("Generation: " + gpt + " FE/t"));
        }
        if (tag.contains("fuelId")) {
            String id = tag.getString("fuelId");
            int count = tag.getInt("fuelCount");
            String base = id.isEmpty() ? "<empty>" : id;
            // If burning, also show remaining seconds
            if (tag.contains("burnTicks") && tag.contains("maxBurnTicks") && tag.getInt("burnTicks") > 0) {
                int seconds = (int) Math.ceil(tag.getInt("burnTicks") / 20.0);
                tooltip.add(Component.literal("Fuel: " + base + (count > 1 ? " x" + count : "") + " (" + seconds + "s left)"));
            } else {
                tooltip.add(Component.literal("Fuel: " + base + (count > 1 ? " x" + count : "")));
            }
        }
    }

    // SERVER: Collect data from the block entity
    @Override
    public void appendServerData(CompoundTag tag, BlockAccessor accessor) {
        if (!(accessor.getBlockEntity() instanceof GeneratorBlockEntity be)) return;

        // Energy
        tag.putInt("energy", be.getEnergyStorage().getEnergyStored());
        tag.putInt("capacity", be.getEnergyStorage().getMaxEnergyStored());

        // Generation per tick
        tag.putInt("genPerTick", be.getGenPerTick());

        // Fuel item in slot and burning progress
        ItemStack fuel = be.getFuelStack();
        if (!fuel.isEmpty()) {
            Item item = fuel.getItem();
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
            tag.putString("fuelId", id == null ? "" : id.toString());
            tag.putInt("fuelCount", fuel.getCount());
        } else {
            tag.putString("fuelId", "");
            tag.putInt("fuelCount", 0);
        }

        tag.putInt("burnTicks", be.getBurnTime());
        tag.putInt("maxBurnTicks", be.getMaxBurnTime());
    }
}
