package com.grobe.techrebirth.block.custom.entity;

import com.grobe.techrebirth.block.ModBlockEntities;
import com.grobe.techrebirth.event.ModCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.items.ItemStackHandler;

public class GeneratorBlockEntity extends BlockEntity implements MenuProvider {

    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private class ModEnergy extends EnergyStorage {
        public ModEnergy(int capacity, int maxReceive, int maxExtract, int energy) {
            super(capacity, maxReceive, maxExtract, energy);
        }
        public void setEnergy(int energy) {
            this.energy = Math.min(energy, this.capacity);
            GeneratorBlockEntity.this.setChanged();
        }
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            int received = super.receiveEnergy(maxReceive, simulate);
            if (!simulate && received > 0) GeneratorBlockEntity.this.setChanged();
            return received;
        }
        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            int extracted = super.extractEnergy(maxExtract, simulate);
            if (!simulate && extracted > 0) GeneratorBlockEntity.this.setChanged();
            return extracted;
        }
    }

    private final ModEnergy energyStorage = new ModEnergy(20000, 512, 512, 0);

    // Generation configuration and client mirrors
    private int genPerTick = 40;
    private int clientMaxEnergyMirror = 20000; // used client-side when syncing via ContainerData

    private int burnTime = 0;
    private int maxBurnTime = 0;

    protected final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> GeneratorBlockEntity.this.burnTime; // remaining burn time
                case 1 -> GeneratorBlockEntity.this.maxBurnTime; // max burn time
                case 2 -> GeneratorBlockEntity.this.energyStorage.getEnergyStored(); // energy stored
                case 3 -> GeneratorBlockEntity.this.energyStorage.getMaxEnergyStored(); // max energy
                case 4 -> GeneratorBlockEntity.this.genPerTick; // generation rate
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> GeneratorBlockEntity.this.burnTime = value;
                case 1 -> GeneratorBlockEntity.this.maxBurnTime = value;
                case 2 -> GeneratorBlockEntity.this.energyStorage.setEnergy(value);
                case 3 -> GeneratorBlockEntity.this.clientMaxEnergyMirror = value; // client-side mirror
                case 4 -> GeneratorBlockEntity.this.genPerTick = value;
            }
        }

        @Override
        public int getCount() {
            return 5;
        }
    };

    public GeneratorBlockEntity(BlockPos pPos, BlockState pState) {
        super(ModBlockEntities.GENERATOR.get(), pPos, pState);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.techrebirth.generator");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new com.grobe.techrebirth.gui.generator.GeneratorMenu(containerId, playerInventory, this, this.data);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, GeneratorBlockEntity be) {
        if (level.isClientSide()) return;

        // Burn fuel and generate energy only if there is room to store it
        boolean hasRoomForEnergy = be.energyStorage.getEnergyStored() < be.energyStorage.getMaxEnergyStored();
        if (be.burnTime > 0) {
            if (hasRoomForEnergy) {
                be.burnTime--;
                be.energyStorage.receiveEnergy(be.genPerTick, false);
                setChanged(level, pos, state);
            }
            // If there is no room, do not decrement burnTime — pause burning until energy is spent
        } else {
            // Only start new fuel if there is room for energy
            if (hasRoomForEnergy) {
                ItemStack fuel = be.itemHandler.getStackInSlot(0);
                int burn = fuel.getBurnTime(RecipeType.SMELTING);
                if (burn > 0) {
                    be.itemHandler.extractItem(0, 1, false);
                    be.burnTime = burn;
                    be.maxBurnTime = burn;
                    setChanged(level, pos, state);
                }
            }
        }

        // Push energy to adjacent receivers
        if (be.energyStorage.getEnergyStored() > 0) {
            for (Direction dir : Direction.values()) {
                if (be.energyStorage.getEnergyStored() <= 0) break;
                BlockPos nPos = pos.relative(dir);
                BlockState nState = level.getBlockState(nPos);
                BlockEntity nBe = level.getBlockEntity(nPos);
                if (nBe == null) continue;
                EnergyStorage target = level.getCapability(ModCapabilities.ELECTRIC_FURNACE_ENERGY, nPos, nState, nBe, dir.getOpposite());
                if (target == null || !target.canReceive()) continue;
                int toSend = Math.min(256, be.energyStorage.getEnergyStored());
                if (toSend <= 0) continue;
                int received = target.receiveEnergy(toSend, false);
                if (received > 0) be.energyStorage.extractEnergy(received, false);
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        tag.put("inventory", itemHandler.serializeNBT(provider));
        tag.putInt("burnTime", burnTime);
        tag.putInt("maxBurnTime", maxBurnTime);
        tag.putInt("energy", energyStorage.getEnergyStored());
        super.saveAdditional(tag, provider);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        itemHandler.deserializeNBT(provider, tag.getCompound("inventory"));
        burnTime = tag.getInt("burnTime");
        maxBurnTime = tag.getInt("maxBurnTime");
        energyStorage.setEnergy(tag.getInt("energy"));
    }

    public ItemStackHandler getItemHandler() { return itemHandler; }
    public EnergyStorage getEnergyStorage() { return energyStorage; }

    // --- Added getters for Jade tooltip and other integrations ---
    // Expose current generation rate in RF per tick
    public int getGenPerTick() { return genPerTick; }
    // Expose remaining burn time (ticks)
    public int getBurnTime() { return burnTime; }
    // Expose max burn time (ticks) of the current fuel
    public int getMaxBurnTime() { return maxBurnTime; }
    // Convenience: current fuel stack in the single fuel slot
    public ItemStack getFuelStack() { return itemHandler.getStackInSlot(0); }
}