package com.grobe.techrebirth.block.custom.entity.generators.solar;

import com.grobe.techrebirth.block.ModBlockEntities;
import com.grobe.techrebirth.block.custom.entity.BaseGeneratorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class SolarGeneratorTier2BlockEntity extends BaseGeneratorBlockEntity {
    public SolarGeneratorTier2BlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SOLAR_GENERATOR_TIER_2.get(), pos, state, 0, 2);
    }

    @Override
    protected int getCapacity() {
        return 75000;
    }

    @Override
    protected int getMaxExtract() {
        return 4096;
    }

    @Override
    protected ContainerData createContainerData(int size) {
        return new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> energyHandler.getEnergyStored();
                    case 1 -> energyHandler.getMaxEnergyStored();
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                if (index == 0) energyHandler.setEnergy(value);
            }

            @Override
            public int getCount() {
                return size;
            }
        };
    }
            @Override
    protected boolean canGenerate() {
        return level != null && level.isDay() && !level.isRaining() && level.canSeeSky(worldPosition.above());
    }

    @Override
    protected int generateEnergy() {
        return 40;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.techrebirth.solar_generator_tier_2");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return null;
    }
}
