package com.grobe.techrebirth.block.custom.entity.infuser;

import com.grobe.techrebirth.block.ModBlockEntities;
import com.grobe.techrebirth.block.custom.entity.BaseMachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class FluidInfuserBlockEntity extends BaseMachineBlockEntity {


    public FluidInfuserBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntities.FLUID_INFUSER.get(), pos, state);
    }

    @Override
    protected String getEnergyTagName() {
        return "fluid_infuser_energy";
    }

    @Override
    protected String getInventoryTagName() {
        return "fluid_infuser_inventory";
    }

    @Override
    protected boolean isItemValid(int slot, ItemStack stack) {
        return false;
    }

    @Override
    protected boolean hasRecipe() {
        return false;
    }

    @Override
    protected boolean canContinueProcessing() {
        return false;
    }

    @Override
    protected int getEnergyCostPerTick() {
        return 0;
    }

    @Override
    protected void finishProcessing() {

    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.techrebirth.fluid_infuser");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return null;
    }
}
