package com.grobe.techrebirth.gui.electric_furnace;

import com.grobe.techrebirth.block.custom.entity.furnace.ElectricFurnaceBlockEntity;
import com.grobe.techrebirth.gui.BaseMachineMenu;
import com.grobe.techrebirth.gui.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;

public class ElectricFurnaceMenu extends BaseMachineMenu {
    public final ElectricFurnaceBlockEntity blockEntity;
    private final Level level;
    //final ContainerData data;

    public ElectricFurnaceMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData){
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(4));
    }
    public ElectricFurnaceMenu(int pContainerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.ELECTRIC_FURNACE_MENU.get(), pContainerId, entity, data);
        checkContainerSize(inv, 4);
        blockEntity = ((ElectricFurnaceBlockEntity) entity);
        this.level = inv.player.level();
        // Always prefer the BE's own ContainerData if available
        /* if (entity instanceof ElectricFurnaceBlockEntity ef) {
            this.data = ef.getContainerData();
        } else {
            this.data = data;
        }*/

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 0, 56, 17){

            @Override
            public boolean mayPlace(ItemStack stack) {
                return blockEntity.getItemHandler().isItemValid(0, stack);
            }
        });
        this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 1, 56, 53){
            @Override public boolean mayPlace(ItemStack stack) { return false; }
            @Override public boolean mayPickup(Player player) { return true; }
            @Override
            public void onTake(Player player, ItemStack stack) {
                super.onTake(player, stack);
                if(!player.level().isClientSide()){
                    // Drain pending XP from the block entity and spawn orbs
                    int xp = blockEntity.drainPendingXpRandomRounded();
                    if (xp > 0 && player.level() instanceof net.minecraft.server.level.ServerLevel sl) {
                        ExperienceOrb.award(sl, player.position(), xp);}
                    }
                blockEntity.setChanged();
            }
        });
        this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 2, 116, 17){
            @Override
            public boolean mayPlace(ItemStack stack) {
                return blockEntity.getItemHandler().isItemValid(2, stack);
            }
        });
        this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 3, 116, 53){
            @Override
            public boolean mayPlace(ItemStack stack) {
                return blockEntity.getItemHandler().isItemValid(3, stack);
            }
        });

        //addDataSlots(this.data);
    }

    public boolean isCrafting(){
        return data.get(0) > 0;
    }

    public int getScaledProgress(int height){
        int progress = getProgress();
        int maxProgress = getMaxProgress();

        return maxProgress > 0 ? (progress * height) / maxProgress : 0;
    }


    @Override
    protected boolean isInputSlot(int machineSlot, ItemStack itemStack) {
        return switch (machineSlot) {
            case 0 -> true;
            case 2, 3 -> blockEntity.getItemHandler().isItemValid(machineSlot, itemStack);
            default -> false;
        };
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                pPlayer, blockEntity.getBlockState().getBlock());
    }


    public int getProgress(){ return this.data.get(0);}

    public int getMaxProgress(){ return this.data.get(1);}

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