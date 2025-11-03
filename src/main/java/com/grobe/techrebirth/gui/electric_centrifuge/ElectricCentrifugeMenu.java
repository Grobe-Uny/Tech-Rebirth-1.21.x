package com.grobe.techrebirth.gui.electric_centrifuge;

import com.grobe.techrebirth.block.ModBlocks;
import com.grobe.techrebirth.block.custom.entity.ElectricCentrifugeBlockEntity;
import com.grobe.techrebirth.gui.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;

public class ElectricCentrifugeMenu extends AbstractContainerMenu {
    public final ElectricCentrifugeBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    public ElectricCentrifugeMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        this(id, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(6));
    }

    public ElectricCentrifugeMenu(int id, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.ELECTRIC_CENTRIFUGE_MENU.get(), id);
        checkContainerSize(inv, 3);
        blockEntity = (ElectricCentrifugeBlockEntity) entity;
        this.level = inv.player.level();
        this.data = data;

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), ElectricCentrifugeBlockEntity.INPUT_SLOT, 44, 35));
        this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), ElectricCentrifugeBlockEntity.CATALYST_FILL_SLOT, 18, 52));
        this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), ElectricCentrifugeBlockEntity.OUTPUT_SLOT, 116, 35));

        addDataSlots(data);
    }

    public boolean isCrafting() {
        return data.get(0) > 0;
    }

    public int getScaledProgress() {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);
        int progressArrowSize = 24; // Width of the progress arrow

        return maxProgress != 0 && progress != 0 ? progress * progressArrowSize / maxProgress : 0;
    }

    public int getEnergy() {
        return this.data.get(2);
    }

    public int getMaxEnergy() {
        return this.data.get(3);
    }

    public int getCatalystAmount() {
        return this.data.get(4);
    }

    public ItemStack getCatalystItem() {
        return new ItemStack(net.minecraft.world.item.Item.byId(this.data.get(5)));
    }

    public int getProgress() {
        return this.data.get(0);
    }

    public int getMaxProgress() {
        return this.data.get(1);
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        Slot sourceSlot = this.slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        if (index < 36) {
            // Player inventory to machine
            if (!this.moveItemStackTo(sourceStack, 36, 39, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index < 39) {
            // Machine to player inventory
            if (!this.moveItemStackTo(sourceStack, 0, 36, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY;
        }

        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }

        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                pPlayer, ModBlocks.ELECTRIC_CENTRIFUGE.get());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
}
