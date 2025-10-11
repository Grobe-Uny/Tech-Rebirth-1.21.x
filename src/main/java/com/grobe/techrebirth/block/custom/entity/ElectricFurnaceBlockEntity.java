/*package com.grobe.techrebirth.block.custom.entity;

import com.grobe.techrebirth.block.ModBlockEntities;
import com.grobe.techrebirth.gui.electric_furnace.ElectricFurnaceMenu;
import com.grobe.techrebirth.item.ModItems;
import com.grobe.techrebirth.item.custom.UpgradeItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import static com.grobe.techrebirth.event.ModCapabilities.ELECTRIC_FURNACE_ENERGY;
import static com.grobe.techrebirth.event.ModCapabilities.ELECTRIC_FURNACE_ITEM_HANDLER;

public class ElectricFurnaceBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(4);
    private final EnergyStorage energyStorage = new EnergyStorage(20000, 256, 256, 0);
    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 72;

    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;
    private static final int UPGRADE_SLOT_1 = 2;
    private static final int UPGRADE_SLOT_2 = 3;

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    public EnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    public ElectricFurnaceBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.ELECTRIC_FURNACE.get(), pPos, pBlockState);
        ELECTRIC_FURNACE_ITEM_HANDLER.attach(this, (side) -> itemHandler);
        ELECTRIC_FURNACE_ENERGY.attach(this, (side) -> energyStorage);
        ELECTRIC_FURNACE_ENERGY.getCapability()
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex){
                    case 0 -> ElectricFurnaceBlockEntity.this.progress;
                    case 1 -> ElectricFurnaceBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex){
                    case 0 -> ElectricFurnaceBlockEntity.this.progress = pValue;
                    case 1 -> ElectricFurnaceBlockEntity.this.maxProgress = pValue;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    public void drops(){
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0; i < itemHandler.getSlots(); i++){
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.techrebirth.electric_furnace");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new ElectricFurnaceMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("inventory", itemHandler.serializeNBT());
        pTag.putInt("electric_furnace.progress", progress);
        pTag.putInt("electric_furnace.energy", energyStorage.getEnergyStored());
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemHandler.deserializeNBT(pTag.getCompound("inventory"));
        progress = pTag.getInt("electric_furnace.progress");
        energyStorage.setEnergy(pTag.getInt("electric_furnace.energy"));
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        if (hasRecipe()){
            int speedUpgrades = getUpgradeCount(ModItems.SPEED_UPGRADE.get());
            int efficiencyUpgrades = getUpgradeCount(ModItems.EFFICIENCY_UPGRADE.get());

            float speedMultiplier = 1 + (0.5f * speedUpgrades);
            this.maxProgress = (int) (72 / speedMultiplier);
            if (this.maxProgress < 1) this.maxProgress = 1;

            float energyConsumptionMultiplier = (float) Math.pow(0.75, efficiencyUpgrades);
            float energySpeedPenalty = 1 + (0.5f * speedUpgrades);
            int energyToConsume = (int) (128 * energySpeedPenalty * energyConsumptionMultiplier);

            extractEnergy(energyToConsume);
            increaseCraftingProgress();
            setChanged(pLevel, pPos, pState);

            if (progress >= maxProgress){
                craftItem();
            }
        } else {
            resetProgress();
        }
    }

    private void extractEnergy(int amount) {
        this.energyStorage.extractEnergy(amount, false);
    }

    private void craftItem() {
        Optional<RecipeHolder<SmeltingRecipe>> recipe = getCurrentRecipe();
        ItemStack result = recipe.get().value().getResultItem(null);

        this.itemHandler.extractItem(INPUT_SLOT, 1, false);
        this.itemHandler.setStackInSlot(OUTPUT_SLOT, new ItemStack(result.getItem(), this.itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() + result.getCount()));
        resetProgress();
    }

    private void resetProgress() {
        progress = 0;
    }

    private void increaseCraftingProgress() {
        progress++;
    }

    private boolean hasRecipe() {
        Optional<RecipeHolder<SmeltingRecipe>> recipe = getCurrentRecipe();

        if (recipe.isEmpty()){
            return false;
        }
        ItemStack result = recipe.get().value().getResultItem(null);

        int speedUpgrades = getUpgradeCount(ModItems.SPEED_UPGRADE.get());
        int efficiencyUpgrades = getUpgradeCount(ModItems.EFFICIENCY_UPGRADE.get());

        float energyConsumptionMultiplier = (float) Math.pow(0.75, efficiencyUpgrades);
        float energySpeedPenalty = 1 + (0.5f * speedUpgrades);
        int energyToConsume = (int) (128 * energySpeedPenalty * energyConsumptionMultiplier);

        return canInsertAmountIntoOutputSlot(result.getCount()) && canInsertItemIntoOutputSlot(result.getItem()) && hasEnoughEnergy(energyToConsume);
    }

    private int getUpgradeCount(Item upgradeItem) {
        int count = 0;
        ItemStack stack1 = this.itemHandler.getStackInSlot(UPGRADE_SLOT_1);
        ItemStack stack2 = this.itemHandler.getStackInSlot(UPGRADE_SLOT_2);

        if (stack1.getItem() instanceof UpgradeItem && stack1.is(upgradeItem)) {
            count++;
        }
        if (stack2.getItem() instanceof UpgradeItem && stack2.is(upgradeItem)) {
            count++;
        }
        return count;
    }

    private boolean hasEnoughEnergy(int energyToConsume) {
        return this.energyStorage.getEnergyStored() >= energyToConsume;
    }

    private Optional<RecipeHolder<SmeltingRecipe>> getCurrentRecipe() {
        SimpleContainer inventory = new SimpleContainer(1);
        inventory.setItem(0, this.itemHandler.getStackInSlot(INPUT_SLOT));
        return this.level.getRecipeManager().getRecipeFor(SmeltingRecipe.class, inventory, level);
    }

    private boolean canInsertItemIntoOutputSlot(Item item) {
        return this.itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty() || this.itemHandler.getStackInSlot(OUTPUT_SLOT).is(item);
    }

    private boolean canInsertAmountIntoOutputSlot(int count) {
        return this.itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() + count <= this.itemHandler.getStackInSlot(OUTPUT_SLOT).getMaxStackSize();
    }
}*/

package com.grobe.techrebirth.block.custom.entity;

import com.grobe.techrebirth.block.ModBlockEntities;
import com.grobe.techrebirth.gui.electric_furnace.ElectricFurnaceMenu;
import com.grobe.techrebirth.item.ModItems;
import com.grobe.techrebirth.item.custom.UpgradeItem;
import com.grobe.techrebirth.event.ModCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ElectricFurnaceBlockEntity extends BlockEntity implements MenuProvider {

    protected final ContainerData data;
    private final ItemStackHandler itemHandler = new ItemStackHandler(4) {
        @Override
        protected void onContentsChanged(int slot) {
            // Mark the block entity as dirty so data is saved
            ElectricFurnaceBlockEntity.this.setChanged();
        }
    };
    private class ModEnergyStorage extends EnergyStorage {
        public ModEnergyStorage(int capacity, int maxReceive, int maxExtract, int energy) {
            super(capacity, maxReceive, maxExtract, energy);
        }
        public void setEnergy(int energy) {
            this.energy = Math.min(energy, this.capacity);
            ElectricFurnaceBlockEntity.this.setChanged();
        }
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            int received = super.receiveEnergy(maxReceive, simulate);
            if (!simulate && received > 0) {
                ElectricFurnaceBlockEntity.this.setChanged();
            }
            return received;
        }
        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            int extracted = super.extractEnergy(maxExtract, simulate);
            if (!simulate && extracted > 0) {
                ElectricFurnaceBlockEntity.this.setChanged();
            }
            return extracted;
        }
    }

    public final ModEnergyStorage energyHandler = new ModEnergyStorage(20000, 256, 256, 0);
    private int progress = 0;
    private int maxProgress = 72;

    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;
    private static final int UPGRADE_SLOT_1 = 2;
    private static final int UPGRADE_SLOT_2 = 3;

    public ElectricFurnaceBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntities.ELECTRIC_FURNACE.get(), pos, state);
    }

    protected ElectricFurnaceBlockEntity(BlockEntityType<? extends ElectricFurnaceBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> ElectricFurnaceBlockEntity.this.progress;
                    case 1 -> ElectricFurnaceBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> ElectricFurnaceBlockEntity.this.progress = value;
                    case 1 -> ElectricFurnaceBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    // Dohvat inventara preko BlockCapability
    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    // Dohvat energije preko BlockCapability
    public EnergyStorage getEnergyStorage() {
        return energyHandler;
    }

    // Helper for subclasses (e.g., creative variant) to set energy directly and mark dirty
    protected void setEnergyStored(int energy) {
        this.energyHandler.setEnergy(energy);
    }

    // Convenience: fill to maximum capacity each tick if desired (creative machines)
    protected void fillEnergyToMax() {
        this.energyHandler.setEnergy(this.energyHandler.getMaxEnergyStored());
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(4);
        ItemStackHandler handler = getItemHandler();
        for (int i = 0; i < 4; i++) {
            inventory.setItem(i, handler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.techrebirth.electric_furnace");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new ElectricFurnaceMenu(containerId, playerInventory, this, this.data);
    }


    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        tag.put("inventory", getItemHandler().serializeNBT(provider));
        tag.putInt("electric_furnace.progress", progress);
        tag.putInt("electric_furnace.energy", getEnergyStorage().getEnergyStored());
        super.saveAdditional(tag, provider);
    }


    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        getItemHandler().deserializeNBT(provider, tag.getCompound("inventory"));
        progress = tag.getInt("electric_furnace.progress");
        this.energyHandler.setEnergy(tag.getInt("electric_furnace.energy"));
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (hasRecipe()) {
            int speedUpgrades = getUpgradeCount(ModItems.SPEED_UPGRADE.get());
            int efficiencyUpgrades = getUpgradeCount(ModItems.EFFICIENCY_UPGRADE.get());

            float speedMultiplier = 1 + (0.5f * speedUpgrades);
            this.maxProgress = (int) (72 / speedMultiplier);
            if (this.maxProgress < 1) this.maxProgress = 1;

            float energyConsumptionMultiplier = (float) Math.pow(0.75, efficiencyUpgrades);
            float energySpeedPenalty = 1 + (0.5f * speedUpgrades);
            int energyToConsume = (int) (128 * energySpeedPenalty * energyConsumptionMultiplier);

            getEnergyStorage().extractEnergy(energyToConsume, false);
            increaseCraftingProgress();

            setChanged(level, pos, state);

            if (progress >= maxProgress) {
                craftItem();
            }
        } else {
            resetProgress();
        }
    }

    private void craftItem() {
        Optional<RecipeHolder<SmeltingRecipe>> recipe = getCurrentRecipe();
        if (recipe.isEmpty()) return;

        ItemStack result = recipe.get().value().getResultItem(null);
        ItemStackHandler handler = getItemHandler();

        handler.extractItem(INPUT_SLOT, 1, false);
        handler.setStackInSlot(OUTPUT_SLOT, new ItemStack(result.getItem(),
                handler.getStackInSlot(OUTPUT_SLOT).getCount() + result.getCount()));

        resetProgress();
    }

    private void resetProgress() { progress = 0; }
    private void increaseCraftingProgress() { progress++; }

    private boolean hasRecipe() {
        Optional<RecipeHolder<SmeltingRecipe>> recipe = getCurrentRecipe();
        if (recipe.isEmpty()) return false;

        ItemStack result = recipe.get().value().getResultItem(null);
        ItemStackHandler handler = getItemHandler();

        int speedUpgrades = getUpgradeCount(ModItems.SPEED_UPGRADE.get());
        int efficiencyUpgrades = getUpgradeCount(ModItems.EFFICIENCY_UPGRADE.get());

        float energyConsumptionMultiplier = (float) Math.pow(0.75, efficiencyUpgrades);
        float energySpeedPenalty = 1 + (0.5f * speedUpgrades);
        int energyToConsume = (int) (128 * energySpeedPenalty * energyConsumptionMultiplier);

        return canInsertAmountIntoOutputSlot(result.getCount()) &&
                canInsertItemIntoOutputSlot(result.getItem()) &&
                getEnergyStorage().getEnergyStored() >= energyToConsume;
    }

    private int getUpgradeCount(Item upgradeItem) {
        int count = 0;
        ItemStackHandler handler = getItemHandler();
        ItemStack stack1 = handler.getStackInSlot(UPGRADE_SLOT_1);
        ItemStack stack2 = handler.getStackInSlot(UPGRADE_SLOT_2);

        if (stack1.getItem() instanceof UpgradeItem && stack1.is(upgradeItem)) count++;
        if (stack2.getItem() instanceof UpgradeItem && stack2.is(upgradeItem)) count++;

        return count;
    }

    private Optional<RecipeHolder<SmeltingRecipe>> getCurrentRecipe() {
        net.minecraft.world.item.crafting.SingleRecipeInput input =
                new net.minecraft.world.item.crafting.SingleRecipeInput(getItemHandler().getStackInSlot(INPUT_SLOT));
        return this.level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, input, level);
    }

    private boolean canInsertItemIntoOutputSlot(Item item) {
        ItemStackHandler handler = getItemHandler();
        return handler.getStackInSlot(OUTPUT_SLOT).isEmpty() ||
                handler.getStackInSlot(OUTPUT_SLOT).is(item);
    }

    private boolean canInsertAmountIntoOutputSlot(int count) {
        ItemStackHandler handler = getItemHandler();
        return handler.getStackInSlot(OUTPUT_SLOT).getCount() + count <=
                handler.getStackInSlot(OUTPUT_SLOT).getMaxStackSize();
    }
}
