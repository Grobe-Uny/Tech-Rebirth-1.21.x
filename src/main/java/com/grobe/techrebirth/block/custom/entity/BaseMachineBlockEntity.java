package com.grobe.techrebirth.block.custom.entity;

import com.grobe.techrebirth.item.ModItems;
import com.grobe.techrebirth.sound.ClientSoundHelper;
import com.grobe.techrebirth.util.MachineTier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

/**
 * A reusable base for powered machines that provides:
 * - An ItemStackHandler with onContentsChanged -> setChanged
 * - An EnergyStorage that marks the BE dirty on changes
 * Subclasses only need to implement isItemValid and their own menu/logic.
 */
public abstract class BaseMachineBlockEntity extends BlockEntity implements MenuProvider {

    protected final ItemStackHandler itemHandler;
    protected final DirtyEnergyStorage energyHandler;

    protected int progress = 0;
    protected int maxProgress;
    protected int energyCostPerTick = 0; // Added for syncing

    private boolean soundPlaying = false;

    protected abstract String getEnergyTagName();

    protected abstract String getInventoryTagName();

    protected ContainerData data;

    protected MachineTier machineTier = MachineTier.BASIC;

    protected ContainerData createContainerData(int size) {
        return new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> progress;
                    case 1 -> maxProgress;
                    case 2 -> energyHandler.getEnergyStored();
                    case 3 -> energyHandler.getMaxEnergyStored();
                    case 4 -> energyCostPerTick; // New
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> progress = value;
                    case 1 -> maxProgress = value;
                    case 2 -> energyHandler.setEnergy(value);
                    case 4 -> energyCostPerTick = value; // New
                }
            }

            @Override
            public int getCount() {
                return size;
            }
        };
    }


    protected BaseMachineBlockEntity(
            BlockEntityType<?> type,
            BlockPos pos,
            BlockState state,
            int inventorySize,
            int energyCapacity,
            int maxReceive,
            int maxExtract,
            int initialEnergy,
            int dataSize
    ) {
        super(type, pos, state);
        this.data = createContainerData(dataSize);
        this.itemHandler = new ItemStackHandler(inventorySize) {
            @Override
            protected void onContentsChanged(int slot) {
                BaseMachineBlockEntity.this.setChanged();
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return BaseMachineBlockEntity.this.isItemValid(slot, stack);
            }
        };
        this.energyHandler = new DirtyEnergyStorage(energyCapacity, maxReceive, maxExtract, initialEnergy);
    }
    protected BaseMachineBlockEntity(
            BlockEntityType<?> type,
            BlockPos pos,
            BlockState state,
            int inventorySize,
            MachineTier tier,
            int dataSize
    ) {
        this(type, pos, state, inventorySize,
                tier.energyCapacity,           // capacity iz tiera
                tier.energyInput,      // maxReceive
                tier.energyInput,      // maxExtract
                0,                             // initialEnergy
                dataSize);
        this.machineTier = tier;
    }

    public MachineTier getTier() {
        return machineTier != null ? machineTier : MachineTier.BASIC;
    }

    public int getProgress() {
        return progress;
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public float getProgressPercent() {
        return maxProgress > 0 ? (float) progress / maxProgress : 0;
    }

    protected boolean isValidUpgradeForThisMachine(ItemStack stack) {
        Item item = stack.getItem();
        return item == ModItems.SPEED_UPGRADE.get() || item == ModItems.EFFICIENCY_UPGRADE.get();
    }

    protected void increaseProgress() {
        progress++;
        setChanged();
    }

    protected void resetProgress() {
        progress = 0;
        setChanged();
    }

    public void setEnergyStored(int energy) {
        this.energyHandler.setEnergy(energy);
    }

    protected void fillEnergyToMax() {
        this.energyHandler.setEnergy(this.energyHandler.getMaxEnergyStored());
    }

    protected abstract boolean isItemValid(int slot, ItemStack stack);

    public ContainerData getContainerData() {
        return data;
    }

    protected class DirtyEnergyStorage extends EnergyStorage {
        public DirtyEnergyStorage(int capacity, int maxReceive, int maxExtract, int energy) {
            super(capacity, maxReceive, maxExtract, energy);
        }

        public void setEnergy(int energy) {
            this.energy = Math.min(energy, this.capacity);
            BaseMachineBlockEntity.this.setChanged();
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            int received = super.receiveEnergy(maxReceive, simulate);
            if (!simulate && received > 0) {
                BaseMachineBlockEntity.this.setChanged();
            }
            return received;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            int extracted = super.extractEnergy(maxExtract, simulate);
            if (!simulate && extracted > 0) {
                BaseMachineBlockEntity.this.setChanged();
            }
            return extracted;
        }
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) {
            updateSound();
            return;
        }

        if (!isRecipeCacheValid()) {
            updateRecipeCache();
        }

        boolean hasRecipe = hasRecipe();
        boolean canContinue = hasRecipe && canContinueProcessing();
        this.energyCostPerTick = getEnergyCostPerTick(); // Update the field
        boolean hasEnergy = energyHandler.getEnergyStored() >= this.energyCostPerTick;

        // The machine is working if it has a recipe, can process (output space), and has energy
        boolean isWorking = canContinue && hasEnergy;

        // Toggle LIT state based on whether it's actually working
        if (state.hasProperty(BlockStateProperties.LIT) && state.getValue(BlockStateProperties.LIT) != isWorking) {
            level.setBlock(pos, state.setValue(BlockStateProperties.LIT, isWorking), 3);
        }

        if (hasRecipe) {
            // Initialize progress-related data if starting a new craft
            if (this.progress == 0) {
                initProgressData();
            }

            if (isWorking) {
                if (this.progress == 0) {
                    onProcessStart();
                }

                energyHandler.extractEnergy(this.energyCostPerTick, false);
                increaseProgress();

                if (getProgress() >= getMaxProgress()) {
                    finishProcessing();
                    resetProgress();
                }
            }
            // If hasRecipe but !isWorking, progress is paused
        } else {
            // No valid recipe, reset progress
            if (this.progress > 0) {
                resetProgress();
            }
        }
    }

    // Methods to be implemented by subclasses for generalized tick logic
    protected boolean isRecipeCacheValid() { return true; }
    protected void updateRecipeCache() {}
    protected abstract boolean hasRecipe();
    protected abstract boolean canContinueProcessing();
    protected abstract int getEnergyCostPerTick();
    protected abstract void finishProcessing();
    protected void initProgressData() {}
    protected void onProcessStart() {}

    protected void updateSound() {
        if (this.level.isClientSide) {
            SoundEvent sound = getWorkingSound();
            if (sound != null && isActuallyProcessing()) {
                if (!soundPlaying) {
                    ClientSoundHelper.playMachineSound(this, sound, getWorkingSoundVolume(), getWorkingSoundPitch());
                    soundPlaying = true;
                }
            } else {
                soundPlaying = false;
            }
        }
    }

    protected SoundEvent getWorkingSound() {
        return null;
    }

    protected float getWorkingSoundVolume() {
        return 1.0f;
    }

    protected float getWorkingSoundPitch() {
        return 1.0f;
    }

    public boolean isActuallyProcessing() {
        BlockState state = getBlockState();
        return state.hasProperty(BlockStateProperties.LIT) && state.getValue(BlockStateProperties.LIT);
    }


    public String getName(){
        return getTier().name;
    }

    public IEnergyStorage getEnergyStorage() {
        return energyHandler;
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    // Dodaj metodu za IItemHandler (za capabilityje)
    public IItemHandler getItemHandlerCapability() {
        return itemHandler;
    }

    // Dodaj fluid support (opcionalno)
    public IFluidHandler getFluidHandler() {
        return null; // Po defaultu nema fluid support
    }

    // Metoda za side-based item handling (override-aj u podklasama po potrebi)
    public IItemHandler getSidedItemHandler(Direction side) {
        return getItemHandler(); // Default: vraća isti handler sa svih strana
    }

    // DODAJ OBAVEZNO: Eksplicitno spremanje prije uništavanja
    @Override
    public void setRemoved() {
        if (level != null && !level.isClientSide) {
            setChanged(); // Ovo triggera saveAdditional()
        }
        super.setRemoved();
    }

    // DODAJ OBAVEZNO: Ova metoda se poziva kada se chunk savea ili blok strga
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag tag = super.getUpdateTag(provider);
        saveAdditional(tag, provider); // OBAVEZNO: spremi podatke
        return tag;
    }

    // DODAJ OBAVEZNO: Ova metoda se poziva kada se blok postavi iz NBT-a
    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider provider) {
        loadAdditional(tag, provider); // OBAVEZNO: učitaj podatke
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.put(getEnergyTagName(), energyHandler.serializeNBT(provider));
        tag.put(getInventoryTagName(), getItemHandler().serializeNBT(provider));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        String energyTag = getEnergyTagName();
        String inventoryTag = getInventoryTagName();
        if (tag.contains(energyTag)) {
            energyHandler.deserializeNBT(provider, tag.get(energyTag));
        }
        if(tag.contains(inventoryTag)){
            getItemHandler().deserializeNBT(provider, tag.getCompound(inventoryTag));
        }
    }
}
