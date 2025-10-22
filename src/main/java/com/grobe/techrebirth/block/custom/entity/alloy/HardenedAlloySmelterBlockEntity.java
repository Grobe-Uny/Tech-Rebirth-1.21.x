package com.grobe.techrebirth.block.custom.entity.alloy;

import com.grobe.techrebirth.block.ModBlockEntities;
import com.grobe.techrebirth.util.MachineTier;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

public class HardenedAlloySmelterBlockEntity extends AlloySmelterBlockEntity{
    public HardenedAlloySmelterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HARDENED_ALLOY_SMELTER.get(),pos, state, MachineTier.HARDENED);
    }

    @Override
    protected String getEnergyTagName() {
        return "hardened_alloy_smelter_energy";
    }

    @Override
    protected String getInventoryTagName() {
        return "hardened_alloy_smelter_inventory";
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.techrebirth.hardened_alloy_smelter")
                .withStyle(ChatFormatting.GOLD);
    }
}
