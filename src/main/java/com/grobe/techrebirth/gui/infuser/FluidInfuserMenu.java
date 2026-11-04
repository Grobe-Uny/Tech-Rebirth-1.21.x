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
        this(pContainerId, inv, (FluidInfuserBlockEntity) inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(6));
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
    protected boolean isInputSlot(int machineSlot, ItemStack itemStack) {
        return switch (machineSlot) {
            case FluidInfuserBlockEntity.INPUT_SLOT -> true;
            case FluidInfuserBlockEntity.UPGRADE_SLOT_1, FluidInfuserBlockEntity.UPGRADE_SLOT_2, FluidInfuserBlockEntity.FLUID_INPUT_SLOT -> blockEntity.getItemHandler().isItemValid(machineSlot, itemStack);
            default -> false;
        };
    }
}
