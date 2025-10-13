package com.grobe.techrebirth.gui.electric_furnace;

import com.grobe.techrebirth.block.ModBlocks;
import com.grobe.techrebirth.block.custom.entity.ElectricFurnaceBlockEntity;
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

public class ElectricFurnaceMenu extends AbstractContainerMenu {
    public final ElectricFurnaceBlockEntity blockEntity;
    private final Level level;
    final ContainerData data;

    public ElectricFurnaceMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData){
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(4));
    }
    public ElectricFurnaceMenu(int pContainerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.ELECTRIC_FURNACE_MENU.get(), pContainerId);
        checkContainerSize(inv, 4);
        blockEntity = ((ElectricFurnaceBlockEntity) entity);
        this.level = inv.player.level();
        // Always prefer the BE's own ContainerData if available
        if (entity instanceof ElectricFurnaceBlockEntity ef) {
            this.data = ef.getContainerData();
        } else {
            this.data = data;
        }

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

        addDataSlots(this.data);
    }

    public boolean isCrafting(){
        return data.get(0) > 0;
    }

    public int getScaledProgress(){
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);
        int progressArrowSize = 26;

        return maxProgress != 0 && progress != 0 ? progress * progressArrowSize / maxProgress : 0;
    }

    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;
    private static final int TE_INVENTORY_SLOT_COUNT = 4;
    @Override
    public ItemStack quickMoveStack(Player playerIn, int pIndex) {
        Slot sourceSlot = slots.get(pIndex);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();


        // Player inventory ranges
        final int VANILLA_START = VANILLA_FIRST_SLOT_INDEX;                  // 0
        final int VANILLA_END = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT; // 36 (0..35)
        final int MAIN_START = VANILLA_START;                                // 0
        final int MAIN_END = MAIN_START + PLAYER_INVENTORY_SLOT_COUNT;       // 27 (0..26)
        final int HOTBAR_START = MAIN_END;                                   // 27
        final int HOTBAR_END = HOTBAR_START + HOTBAR_SLOT_COUNT;             // 36 (27..35)

        // Tile inventory ranges
        final int TE_START = TE_INVENTORY_FIRST_SLOT_INDEX;                  // 36
        final int TE_END = TE_START + TE_INVENTORY_SLOT_COUNT;               // 40 (36..39)

        boolean fromPlayer = pIndex < VANILLA_END;

        if (fromPlayer) {
            // Route upgrades first (slots 2..3)
            if (blockEntity.getItemHandler().isItemValid(2, sourceStack)) {
                if (!this.moveItemStackTo(sourceStack, TE_START + 2, TE_START + 4, false)) {
                    return ItemStack.EMPTY;
                }
            }
            // Then route smeltable input (slot 0)
            else if (blockEntity.getItemHandler().isItemValid(0, sourceStack)) {
                if (!this.moveItemStackTo(sourceStack, TE_START + 0, TE_START + 1, false)) {
                    return ItemStack.EMPTY;
                }
            }
            // Otherwise shuffle within player inventory (hotbar <-> main)
            else if (pIndex >= MAIN_START && pIndex < MAIN_END) {
                // main -> hotbar
                if (!this.moveItemStackTo(sourceStack, HOTBAR_START, HOTBAR_END, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (pIndex >= HOTBAR_START && pIndex < HOTBAR_END) {
                // hotbar -> main
                if (!this.moveItemStackTo(sourceStack, MAIN_START, MAIN_END, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                return ItemStack.EMPTY; // should not happen
            }
        } else if (pIndex >= TE_START && pIndex < TE_END) {
            // From tile to player: move to full player inventory
            if (!this.moveItemStackTo(sourceStack, VANILLA_START, VANILLA_END, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY; // invalid index
        }

        if (sourceStack.isEmpty()) sourceSlot.set(ItemStack.EMPTY); else sourceSlot.setChanged();
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
        
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                pPlayer, blockEntity.getBlockState().getBlock());
    }

    private void addPlayerInventory(Inventory playerInventory){
        for (int i = 0; i < 3; ++i){
            for (int l = 0; l < 9; ++l){
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory){
        for (int i = 0; i < 9; ++i){
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
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