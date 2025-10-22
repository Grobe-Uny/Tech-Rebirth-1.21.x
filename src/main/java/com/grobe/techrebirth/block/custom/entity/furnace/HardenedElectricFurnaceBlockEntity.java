package com.grobe.techrebirth.block.custom.entity.furnace;

import com.grobe.techrebirth.block.ModBlockEntities;
import com.grobe.techrebirth.util.MachineTier;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

public class HardenedElectricFurnaceBlockEntity extends ElectricFurnaceBlockEntity {
    public HardenedElectricFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HARDENED_ELECTRIC_FURNACE.get(),pos, state, MachineTier.HARDENED);
    }

    @Override
    protected String getEnergyTagName() {
        return "hardened_electric_furnace_energy";
    }

    @Override
    protected String getInventoryTagName() {
        return "hardened_electric_furnace_inventory";
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.techrebirth.hardened_electric_furnace")
                .withStyle(ChatFormatting.GOLD);
    }
}
