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
    //final ContainerData data;

    public AlloySmelterMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(4));
    }
    public AlloySmelterMenu(int pContainerId, Inventory inv, BlockEntity entity, ContainerData data){
        super(ModMenuTypes.ALLOY_SMELTER_MENU.get(), pContainerId, entity, data);
        checkContainerSize(inv, 4);
        blockEntity = ((AlloySmelterBlockEntity) entity);
        this.level = inv.player.level();
        /*// Always prefer the BE's own ContainerData if available
        if (entity instanceof AlloySmelterBlockEntity as) {
            this.data = as.getContainerData();
        } else {
            this.data = data;
        }*/
        

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 0, 59, 30));
        this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 1, 80, 17));
        this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 2, 101, 30));

        this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 3, 80, 60){
            @Override
            public boolean mayPlace(ItemStack stack){
                return false;
            }
        });

        this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 4, 134, 60));
        this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 5, 153, 60));

        //addDataSlots(this.data);
    }

    @Override
    protected boolean isInputSlot(int machineSlot, ItemStack itemStack) {
        return switch (machineSlot) {
            case 0, 1, 2 -> true;
            case 4, 5 -> blockEntity.getItemHandler().isItemValid(machineSlot, itemStack);
            default -> false;
        };
    }

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
        int progress = getProgress();
        int maxProgress = getMaxProgress();
        return maxProgress > 0 ? (progress * height) / maxProgress : 0;
    }

    public int getEnergyScaled(int height) {
        int energy = getEnergy();
        int maxEnergy = getMaxEnergy();
        return maxEnergy > 0 ?  (energy * height) / maxEnergy : 0;
    }
}
