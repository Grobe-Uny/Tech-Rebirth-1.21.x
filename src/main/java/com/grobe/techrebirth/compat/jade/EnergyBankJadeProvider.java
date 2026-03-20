package com.grobe.techrebirth.compat.jade;

import com.grobe.techrebirth.TechRebirth;
import com.grobe.techrebirth.block.custom.entity.bank.EnergyBankBlockEntity;
import com.grobe.techrebirth.util.EnergySideConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.config.IPluginConfig;

public enum EnergyBankJadeProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "energy_bank");

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag tag = accessor.getServerData();
        if (tag.contains("energy") && tag.contains("capacity")) {
            int energy = tag.getInt("energy");
            int cap = tag.getInt("capacity");
            tooltip.add(Component.literal(String.format("Energy: %,d / %,d FE", energy, cap)));
        }
        if (tag.contains("side_config")) {
            String sideConfig = tag.getString("side_config");
            tooltip.add(Component.literal("Side: " + sideConfig));
        }
    }

    @Override
    public void appendServerData(CompoundTag tag, BlockAccessor accessor) {
        if (!(accessor.getBlockEntity() instanceof EnergyBankBlockEntity be)) return;
        // The EnergyBank exposes a network-wide EnergyStorage. Passing null gets the full network access.
        var networkStorage = be.getEnergyStorageForSide(null);
        tag.putInt("energy", networkStorage.getEnergyStored());
        tag.putInt("capacity", networkStorage.getMaxEnergyStored());
        
        // Add side config info
        EnergySideConfig sideConfig = be.getSideConfig(accessor.getSide());
        tag.putString("side_config", sideConfig.name());
    }
}
