package com.grobe.techrebirth.compat.jade;

import com.grobe.techrebirth.TechRebirth;
import com.grobe.techrebirth.block.custom.entity.bank.EnergyBankBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.config.IPluginConfig;

/**
 * Jade provider for Energy Bank.
 *
 * Displays aggregated network energy: current / max (FE) so placing banks together shows increased capacity.
 */
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
        if (tag == null) return;
        if (tag.contains("energy") && tag.contains("capacity")) {
            int energy = tag.getInt("energy");
            int cap = tag.getInt("capacity");
            tooltip.add(Component.literal(String.format("Energy: %,d / %,d FE", energy, cap)));
        }
    }

    @Override
    public void appendServerData(CompoundTag tag, BlockAccessor accessor) {
        if (!(accessor.getBlockEntity() instanceof EnergyBankBlockEntity be)) return;
        // The EnergyBank exposes a network-wide EnergyStorage via getExposedEnergyStorage()
        tag.putInt("energy", be.getExposedEnergyStorage().getEnergyStored());
        tag.putInt("capacity", be.getExposedEnergyStorage().getMaxEnergyStored());
    }
}
