
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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import com.grobe.techrebirth.util.ModTags;

public class ElectricFurnaceBlockEntity extends BlockEntity implements MenuProvider {

    protected final ContainerData data;
    private final ItemStackHandler itemHandler = new ItemStackHandler(4) {
        @Override
        protected void onContentsChanged(int slot) {
            // Mark the block entity as dirty so data is saved
            ElectricFurnaceBlockEntity.this.setChanged();
        }
    };

    public boolean isItemValid(int slot, ItemStack stack){
        return switch (slot){
            case INPUT_SLOT -> isSmeltable(stack);
            case OUTPUT_SLOT -> false; // only output
            case UPGRADE_SLOT_1, UPGRADE_SLOT_2 -> isValidUpgradeForThisMachine(stack);
            default -> false;
        };
    }

    private boolean isSmeltable(ItemStack stack) {
        if (stack.isEmpty() || this.level == null) return false;
        var opt = this.level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(stack), this.level);
        return opt.isPresent();
    }

    private boolean isValidUpgradeForThisMachine(ItemStack stack) {
        Item item = stack.getItem();
        return item == ModItems.SPEED_UPGRADE.get() ||item == ModItems.EFFICIENCY_UPGRADE.get();
    }

    public int drainPendingXpRandomRounded() {
        if(this.level == null) return 0;
        int whole = (int) Math.floor(this.pendingXp);
        float fractional = this.pendingXp - whole;
        if(fractional > 0 && this.level.random.nextFloat() < fractional) whole++;
        this.pendingXp -= whole;
        return whole;
    }

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

    public final ModEnergyStorage energyHandler = new ModEnergyStorage(20000, 512, 512, 0);
    private int progress = 0;
    private int maxProgress = 72;
    private float pendingXp;
    private ItemStack lastInput = ItemStack.EMPTY;
    private static final int DEFAULT_VANILLA_COOK = 200; // fallback if recipe lacks time
    private static final float MACHINE_SPEED_FACTOR = 0.18f; // 200 * 0.18 = 36 ticks (~1.8 s)
    private static final float HEAVY_TIME_MULT = 1.30f; // heavy items take ~1.3x time (~+~2s over light)
    private static final float HEAVY_RF_MULT = 1.15f;   // heavy items draw modestly more RF/t

    private static int getRecipeCookTime(SmeltingRecipe r) {
        int t = r.getCookingTime();
        return t > 0 ? t : DEFAULT_VANILLA_COOK;
    }

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
                    case 0 -> ElectricFurnaceBlockEntity.this.progress;                    // cook progress
                    case 1 -> ElectricFurnaceBlockEntity.this.maxProgress;                 // max progress
                    case 2 -> ElectricFurnaceBlockEntity.this.getEnergyStorage().getEnergyStored(); // energy stored
                    case 3 -> ElectricFurnaceBlockEntity.this.getEnergyStorage().getMaxEnergyStored(); // max energy
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> ElectricFurnaceBlockEntity.this.progress = value;
                    case 1 -> ElectricFurnaceBlockEntity.this.maxProgress = value;
                    case 2 -> ElectricFurnaceBlockEntity.this.energyHandler.setEnergy(value); // client mirror
                    case 3 -> { /* no-op: max energy is static; client-side mirror only */ }
                }
            }

            @Override
            public int getCount() {
                return 4;
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

    public ContainerData getContainerData() {
        return this.data;
    }


    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        tag.put("inventory", getItemHandler().serializeNBT(provider));
        tag.putInt("electric_furnace.progress", progress);
        tag.putInt("electric_furnace.energy", getEnergyStorage().getEnergyStored());
        tag.putFloat("pendingXp", pendingXp);
        super.saveAdditional(tag, provider);
    }


    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        getItemHandler().deserializeNBT(provider, tag.getCompound("inventory"));
        progress = tag.getInt("electric_furnace.progress");
        this.energyHandler.setEnergy(tag.getInt("electric_furnace.energy"));
        pendingXp = tag.getFloat("pendingXp");
        super.loadAdditional(tag, provider);
    }

//    public void tick(Level level, BlockPos pos, BlockState state) {
//        if (hasRecipe()) {
//            int speedUpgrades = getUpgradeCount(ModItems.SPEED_UPGRADE.get());
//            int efficiencyUpgrades = getUpgradeCount(ModItems.EFFICIENCY_UPGRADE.get());
//
//            float speedMultiplier = 1 + (0.5f * speedUpgrades);
//            this.maxProgress = (int) (72 / speedMultiplier);
//            if (this.maxProgress < 1) this.maxProgress = 1;
//
//            float energyConsumptionMultiplier = (float) Math.pow(0.75, efficiencyUpgrades);
//            float energySpeedPenalty = 1 + (0.5f * speedUpgrades);
//            int energyToConsume = (int) (128 * energySpeedPenalty * energyConsumptionMultiplier);
//
//            getEnergyStorage().extractEnergy(energyToConsume, false);
//            increaseCraftingProgress();
//
//            setChanged(level, pos, state);
//
//            if (progress >= maxProgress) {
//                craftItem();
//            }
//        } else {
//            resetProgress();
//        }
//    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (hasRecipe()) {
            Optional<RecipeHolder<SmeltingRecipe>> recipeOpt = getCurrentRecipe();
            if (recipeOpt.isEmpty()) { resetProgress(); return; }
            SmeltingRecipe recipe = recipeOpt.get().value();

            ItemStack in = getItemHandler().getStackInSlot(INPUT_SLOT);
            boolean inputChanged = !ItemStack.isSameItemSameComponents(in, lastInput);
            if (inputChanged && progress > 0) progress = 0; // avoid partial mismatches
            lastInput = in.copy();

            int speedUpgrades = getUpgradeCount(ModItems.SPEED_UPGRADE.get());
            int efficiencyUpgrades = getUpgradeCount(ModItems.EFFICIENCY_UPGRADE.get());

            // Category: heavy items (raws/ores) take longer and draw more RF/t
            boolean isHeavy = in.is(ModTags.Items.FURNACE_HEAVY_D.neoforge()) || in.is(ModTags.Items.FURNACE_HEAVY_D.common());

            // Per‑recipe base time mapped through machine speed and category multiplier
            int vanillaCook = Math.max(1, getRecipeCookTime(recipe));
            float categoryTimeMult = isHeavy ? HEAVY_TIME_MULT : 1.0f;
            int baseCook = Math.max(1, Math.round(vanillaCook * MACHINE_SPEED_FACTOR * categoryTimeMult));
            float speedMultiplier = 1 + (0.5f * speedUpgrades);
            this.maxProgress = Math.max(1, (int) (baseCook / speedMultiplier));

            // RF/t baseline with category multiplier, then apply upgrades
            float energyConsumptionMultiplier = (float) Math.pow(0.75, efficiencyUpgrades);
            float energySpeedPenalty = 1 + (0.5f * speedUpgrades);
            float baseRfPerTick = 128f * (isHeavy ? HEAVY_RF_MULT : 1.0f);
            int energyToConsume = Math.max(1, Math.round(baseRfPerTick * energySpeedPenalty * energyConsumptionMultiplier));

            getEnergyStorage().extractEnergy(energyToConsume, false);
            increaseCraftingProgress();
            setChanged(level, pos, state);
            if (progress >= maxProgress) craftItem();
        } else {
            resetProgress();
        }
    }

    private void craftItem() {
        Optional<RecipeHolder<SmeltingRecipe>> recipe = getCurrentRecipe();
        if (recipe.isEmpty()) return;

        ItemStack result = recipe.get().value().getResultItem(null);
        ItemStackHandler handler = getItemHandler();

        // Consume input and place output
        handler.extractItem(INPUT_SLOT, 1, false);
        handler.setStackInSlot(OUTPUT_SLOT, new ItemStack(result.getItem(),
                handler.getStackInSlot(OUTPUT_SLOT).getCount() + result.getCount()));

        float xpPerItem = recipe.get().value().getExperience();
        int produced = result.getCount();
        pendingXp += xpPerItem * produced;

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
        SingleRecipeInput input = new SingleRecipeInput(getItemHandler().getStackInSlot(INPUT_SLOT));
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