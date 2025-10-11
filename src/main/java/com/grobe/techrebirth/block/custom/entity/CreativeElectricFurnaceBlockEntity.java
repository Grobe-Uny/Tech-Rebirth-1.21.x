package com.grobe.techrebirth.block.custom.entity;

import com.grobe.techrebirth.block.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class CreativeElectricFurnaceBlockEntity extends ElectricFurnaceBlockEntity {
    public CreativeElectricFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CREATIVE_ELECTRIC_FURNACE.get(), pos, state);
    }

    @Override
    public void tick(Level level, BlockPos pos, BlockState state) {
        this.fillEnergyToMax(); // Instantly fill to capacity each tick
        super.tick(level, pos, state);
    }
}
