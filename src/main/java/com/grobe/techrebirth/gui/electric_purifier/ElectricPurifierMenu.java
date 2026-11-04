package com.grobe.techrebirth.gui.electric_purifier;

import com.grobe.techrebirth.block.custom.entity.crusher.ElectricCrusherBlockEntity;
import com.grobe.techrebirth.block.custom.entity.purifier.ElectricPurifierBlockEntity;
import com.grobe.techrebirth.gui.BaseMachineMenu;
import com.grobe.techrebirth.gui.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;

public class ElectricPurifierMenu extends BaseMachineMenu {
    public final ElectricPurifierBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    public ElectricPurifierMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData){
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(5));
    }
    public ElectricPurifierMenu(int pContainerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.ELECTRIC_PURIFIER_MENU.get(), pContainerId, entity, data);
        checkContainerSize(inv, 6);
        blockEntity = ((ElectricPurifierBlockEntity) entity);
        this.level = inv.player.level();
        this.data = data;

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 0, 44, 36));
        this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 1, 116, 36){
            @Override public boolean mayPlace(ItemStack stack) { return false; }
            @Override public boolean mayPickup(Player player) { return true; }
        });
        this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 2, 107, 62));
        this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 3, 125, 62));

        addDataSlots(data);
    }

    public boolean isCrafting(){
        return data.get(0) > 0;
    }



    @Override
    protected boolean isInputSlot(int machineSlot, ItemStack itemStack) {
        return switch (machineSlot) {
            case 0-> true;
            case 2, 3 -> blockEntity.getItemHandler().isItemValid(machineSlot, itemStack);
            default -> false;
        };
    }

    public int getScaledProgress(int width){
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);

        if(maxProgress <= 0 || progress <= 0)
        {
            return 0;
        }

        int scaled = (progress * width)/maxProgress;


        return Math.min(scaled, width);
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                pPlayer, blockEntity.getBlockState().getBlock());
    }

    public int getProgress() {
        return data.get(0); // Trenutni progress
    }
    public int getMaxProgress() {
        return data.get(1); // Maksimalni progress
    }
    public int getEnergyStored() {
        return this.data.get(2);
    }

    public int getMaxEnergyStored() {
        return this.data.get(3);
    }

    public int getScaledEnergy(int height) {
        int energy = getEnergyStored();
        int max = getMaxEnergyStored();
        return max > 0 ? (energy * height) / max : 0;
    }
}
