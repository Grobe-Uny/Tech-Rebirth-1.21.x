package com.grobe.techrebirth.gui.alloy_smelter;

import com.grobe.techrebirth.block.custom.entity.alloy.AlloySmelterBlockEntity;
import com.grobe.techrebirth.gui.BaseMachineMenu;
import com.grobe.techrebirth.gui.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;

public class AlloySmelterMenu extends BaseMachineMenu {
    public final AlloySmelterBlockEntity blockEntity;
    private final Level level;
    final ContainerData data;

    public AlloySmelterMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(4));
    }
    public AlloySmelterMenu(int pContainerId, Inventory inv, BlockEntity entity, ContainerData data){
        super(ModMenuTypes.ALLOY_SMELTER_MENU.get(), pContainerId, entity);
        checkContainerSize(inv, 4);
        blockEntity = ((AlloySmelterBlockEntity) entity);
        this.level = inv.player.level();
        // Always prefer the BE's own ContainerData if available
        if (entity instanceof AlloySmelterBlockEntity as) {
            this.data = as.getContainerData();
        } else {
            this.data = data;
        }
        

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 0, 59, 27));
        this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 1, 80, 14));
        this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 2, 101, 27));

        this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 3, 80, 60){
            @Override
            public boolean mayPlace(ItemStack stack){
                return false;
            }
        });

        this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 4, 134, 60));
        this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 5, 153, 60));

        addDataSlots(this.data);
    }


    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            // 💡 Ako je kliknuo slot u mašini (slotovi 0-5)
            if (index < 6) {
                // 💡 Premjesti iz mašine u player inventory
                if (!this.moveItemStackTo(itemstack1, 6, 42, true)) {
                    return ItemStack.EMPTY;
                }
            }
            // 💡 Ako je kliknuo slot u player inventoryu
            else {
                // 💡 Pokušaj staviti u INPUT slotove (0-2) prvo
                if (!this.moveItemStackTo(itemstack1, 0, 3, false)) {
                    // 💡 Ako ne može u input, pokušaj u UPGRADE slotove (4-5)
                    if (!this.moveItemStackTo(itemstack1, 4, 6, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }

            // 💡 Ako je slot ostao prazan nakon premještanja
            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            // 💡 Ako se količina nije promijenila, vrati prazno
            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
        }

        return itemstack;
    }

    // 💡 GETTERI ZA GUI RENDERING
    public int getProgress() {
        return data.get(0); // Trenutni progress
    }

    public int getMaxProgress() {
        return data.get(1); // Maksimalni progress
    }

    public int getEnergy() {
        return data.get(2); // Trenutna energy
    }

    public int getMaxEnergy() {
        return data.get(3); // Maksimalna energy
    }

    public int getProgressScaled(int height) {
        // 💡 Izračunaj postotak progressa za progress bar
        int progress = getProgress();
        int maxProgress = getMaxProgress();
        //return maxProgress > 0 ? (float) progress / maxProgress : 0;
        return maxProgress > 0 ? (progress * height) / maxProgress : 0;
    }

    public int getEnergyScaled(int height) {
        // 💡 Izračunaj postotak energy za energy bar
        int energy = getEnergy();
        int maxEnergy = getMaxEnergy();
        return maxEnergy > 0 ?  (energy * height) / maxEnergy : 0;
    }
}
