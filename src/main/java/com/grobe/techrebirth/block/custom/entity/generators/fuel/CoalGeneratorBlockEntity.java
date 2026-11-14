package com.grobe.techrebirth.block.custom.entity.generators.fuel;

import com.grobe.techrebirth.block.ModBlockEntities;
import com.grobe.techrebirth.energy.EnergyComponent;
import com.grobe.techrebirth.energy.EnergyConfig;
import com.grobe.techrebirth.energy.IEnergyBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class CoalGeneratorBlockEntity extends BlockEntity implements IEnergyBlock, MenuProvider {

    public CoalGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COAL_GENERATOR.get(), pos, state);
    }

    private static final EnergyConfig ENERGY_CONFIG = new EnergyConfig(100000, 0, 512, 0);

    private final EnergyComponent energy = new EnergyComponent(ENERGY_CONFIG, this::setChanged);

    @Override
    public EnergyComponent getEnergyComponent() {
        return energy;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.techrebirth.coal_generator");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return null;
    }
}
