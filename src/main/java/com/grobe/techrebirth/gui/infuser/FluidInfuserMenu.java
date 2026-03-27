package com.grobe.techrebirth.gui.infuser;

import com.grobe.techrebirth.block.custom.entity.infuser.FluidInfuserBlockEntity;
import com.grobe.techrebirth.gui.BaseMachineMenu;
import com.grobe.techrebirth.gui.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.SlotItemHandler;

public class FluidInfuserMenu extends BaseMachineMenu {
    public final FluidInfuserBlockEntity blockEntity;

    public FluidInfuserMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, (FluidInfuserBlockEntity) inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(5));
    }

    public FluidInfuserMenu(int pContainerId, Inventory inv, FluidInfuserBlockEntity entity, ContainerData data) {
        super(ModMenuTypes.FLUID_INFUSER_MENU.get(), pContainerId, entity, data);
        this.blockEntity = entity;

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), FluidInfuserBlockEntity.INPUT_SLOT, 44, 36));
        this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), FluidInfuserBlockEntity.OUTPUT_SLOT, 116, 36) {
            @Override public boolean mayPlace(ItemStack stack) { return false; }
        });
        this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), FluidInfuserBlockEntity.UPGRADE_SLOT_1, 152, 10));
        this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), FluidInfuserBlockEntity.UPGRADE_SLOT_2, 152, 28));
        this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), FluidInfuserBlockEntity.FLUID_INPUT_SLOT, 152, 54));
    }

    public FluidStack getFluidStack() {
        return blockEntity.getFluidTank().getFluid();
    }

    public int getFluidCapacity() {
        return blockEntity.getFluidTank().getCapacity();
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int pIndex) {
        Slot sourceSlot = slots.get(pIndex);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        if (pIndex < 36) {
            if (!moveItemStackTo(sourceStack, 36, 41, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (!moveItemStackTo(sourceStack, 0, 36, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (sourceStack.isEmpty()) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }
}
