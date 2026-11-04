package com.grobe.techrebirth.gui;

import com.grobe.techrebirth.block.custom.entity.BaseMachineBlockEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public abstract class BaseMachineMenu extends AbstractContainerMenu {
    protected final BlockEntity blockEntity;
    protected final Level level;
    protected final ContainerData data;

    protected int energyStored;
    protected int maxEnergyStored;


    protected BaseMachineMenu(@Nullable MenuType<?> menuType, int containerId, BlockEntity blockEntity, ContainerData data) {
        super(menuType, containerId);
        this.blockEntity = blockEntity;
        this.level = ((BaseMachineBlockEntity) blockEntity).getLevel();
        this.data = data;

        addDataSlots(data);
    }

    protected void addPlayerInventory(Inventory playerInventory){
        for (int i = 0; i < 3; ++i){
            for (int l = 0; l < 9; ++l){
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    protected void addPlayerHotbar(Inventory playerInventory){
        for (int i = 0; i < 9; ++i){
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
    @Override
    public boolean stillValid(Player player) {
        return stillValid(
                ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player,
                blockEntity.getBlockState().getBlock()
        );
    }

    protected int getMachineSlotStart(){
        return 36;
    }

    protected int getMachineSlotEnd(){
        return this.slots.size();
    }
    protected boolean isInputSlot(int machineSlot, ItemStack itemStack){
        return true;
    }
    protected boolean isUpgradeSlot(int machineSlot, ItemStack itemStack){
        return false;
    }



    @Override
    public ItemStack quickMoveStack(Player player, int index){
        ItemStack empty = ItemStack.EMPTY;

        if(index<0 || index >= this.slots.size()){
            return empty;
        }
        Slot slot = this.slots.get(index);

        if(!slot.hasItem()){
            return empty;
        }

        ItemStack source = slot.getItem();
        ItemStack original = source.copy();

        int machineStart = getMachineSlotStart();
        int machineEnd = getMachineSlotEnd();

        if(index < machineStart){
            boolean moved = false;


            for (int i = machineStart; i < machineEnd; i++) {
                int machineSlot = i - machineStart;

                if (isInputSlot(machineSlot, source)) {
                    if (this.moveItemStackTo(source, i, i + 1, false)) {
                        moved = true;
                    }
                }
            }

            if (!moved) {
                return empty;
            }
        }else {
            // Machine -> Player inventory

            if (!this.moveItemStackTo(source, 0, machineStart, false)) {
                return empty;
            }
        }
        if (source.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        slot.onTake(player, source);

        return original;
    }

    public int getProgress() { return data.get(0);
    }

    public int getMaxProgress() { return data.get(1);
    }

    public int getEnergy() {
        return data.get(2);
    }

    public int getMaxEnergy() {
        return data.get(3);
    }

    public int getEnergyScaled(int height) {
        int energy = getEnergy();
        int max = getMaxEnergy();
        return max > 0 ? (energy * height) / max : 0;
    }
    public int getVerticalScaledProgress(int height) {
        int progress = getProgress();
        int max = getMaxProgress();
        return max > 0 ? (progress * height) / max : 0;
    }
    public int getHorizontalScaledProgress(int width) {
        int progress = getProgress();
        int max = getMaxProgress();
        return max > 0 ? (progress * width) / max : 0;
    }
}
