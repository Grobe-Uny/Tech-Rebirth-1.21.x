package com.grobe.techrebirth.block.custom.entity;

import com.grobe.techrebirth.block.ModBlockEntities;
import com.grobe.techrebirth.recipe.CentrifugeRecipe;
import com.grobe.techrebirth.recipe.ModRecipeTypes;
import com.grobe.techrebirth.util.MachineTier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class ElectricCentrifugeBlockEntity extends BaseMachineBlockEntity {

    public static final int INPUT_SLOT = 0;
    public static final int CATALYST_FILL_SLOT = 1;
    public static final int OUTPUT_SLOT = 2;

    private static final int CATALYST_CAPACITY = 1000;
    private static final int SCALING_DIVISOR = 100; // For energy calculation

    private ItemStack catalystStack = ItemStack.EMPTY;
    private int catalystAmount = 0;
    private Optional<RecipeHolder<CentrifugeRecipe>> currentRecipe = Optional.empty();

    public ElectricCentrifugeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ELECTRIC_CENTRIFUGE_BE.get(), pos, state, 3, MachineTier.BASIC, 6);
    }

    //region ContainerData
    @Override
    protected ContainerData createContainerData(int size) {
        return new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> progress;
                    case 1 -> maxProgress;
                    case 2 -> energyHandler.getEnergyStored();
                    case 3 -> energyHandler.getMaxEnergyStored();
                    case 4 -> catalystAmount;
                    case 5 -> catalystStack.isEmpty() ? 0 : Item.getId(catalystStack.getItem());
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> progress = value;
                    case 1 -> maxProgress = value;
                    case 2 -> energyHandler.setEnergy(value);
                    case 4 -> catalystAmount = value;
                    case 5 -> catalystStack = value == 0 ? ItemStack.EMPTY : new ItemStack(Item.byId(value));
                }
            }

            @Override
            public int getCount() {
                return 6;
            }
        };
    }
    //endregion

    //region Ticking and Crafting
    @Override
    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;

        fillCatalyst();

        if (hasRecipe()) {
            int energyPerTick = calculateEnergyPerTick();
            if (energyHandler.getEnergyStored() >= energyPerTick) {
                energyHandler.extractEnergy(energyPerTick, false);
                progress++;
                if (progress >= maxProgress) {
                    craftItem();
                }
            }
        } else {
            resetProgress();
        }
        setChanged();
    }

    private boolean hasRecipe() {
        if (this.level == null) return false;

        Optional<RecipeHolder<CentrifugeRecipe>> recipe = getCurrentRecipe();
        if (recipe.isEmpty()) {
            return false;
        }

        CentrifugeRecipe centrifugeRecipe = recipe.get().value();
        return canInsertAmountIntoOutputSlot() &&
               canInsertItemIntoOutputSlot(centrifugeRecipe.getResultItem(this.level.registryAccess())) &&
               hasCorrectCatalyst(centrifugeRecipe) &&
               hasEnoughCatalyst(centrifugeRecipe);
    }

    private void craftItem() {
        if (this.level == null) return;

        Optional<RecipeHolder<CentrifugeRecipe>> recipe = getCurrentRecipe();
        if (recipe.isEmpty()) return;

        CentrifugeRecipe centrifugeRecipe = recipe.get().value();
        this.itemHandler.extractItem(INPUT_SLOT, 1, false);
        this.catalystAmount -= centrifugeRecipe.catalystAmount();

        this.itemHandler.setStackInSlot(OUTPUT_SLOT, new ItemStack(centrifugeRecipe.getResultItem(this.level.registryAccess()).getItem(),
                this.itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() + centrifugeRecipe.getResultItem(this.level.registryAccess()).getCount()));

        resetProgress();
    }

    private Optional<RecipeHolder<CentrifugeRecipe>> getCurrentRecipe() {
        if (this.level == null) return Optional.empty();

        if(currentRecipe.isPresent() && currentRecipe.get().value().matches(new SingleRecipeInput(this.itemHandler.getStackInSlot(INPUT_SLOT)), level)) {
            return currentRecipe;
        } else {
            currentRecipe = this.level.getRecipeManager().getRecipeFor(ModRecipeTypes.CENTRIFUGE_TYPE.get(), new SingleRecipeInput(this.itemHandler.getStackInSlot(INPUT_SLOT)), this.level);
            if(currentRecipe.isPresent()) {
                this.maxProgress = currentRecipe.get().value().processingTime();
                this.progress = 0;
            }
            return currentRecipe;
        }
    }
    //endregion

    //region Catalyst Logic
    private void fillCatalyst() {
        ItemStack catalystFillStack = this.itemHandler.getStackInSlot(CATALYST_FILL_SLOT);
        if (catalystFillStack.isEmpty()) {
            return;
        }

        int catalystValue = 10; // This should be defined in a config or data-driven way later

        if ((this.catalystStack.isEmpty() || ItemStack.isSameItem(this.catalystStack, catalystFillStack)) && this.catalystAmount < CATALYST_CAPACITY) {
            if (this.catalystStack.isEmpty()) {
                this.catalystStack = new ItemStack(catalystFillStack.getItem(), 1);
            }

            int amountToAdd = Math.min(catalystValue, CATALYST_CAPACITY - this.catalystAmount);
            if (amountToAdd > 0) {
                this.catalystAmount += amountToAdd;
                catalystFillStack.shrink(1);
            }
        }
    }

    private boolean hasCorrectCatalyst(CentrifugeRecipe recipe) {
        return !this.catalystStack.isEmpty() && recipe.catalyst().test(this.catalystStack);
    }

    private boolean hasEnoughCatalyst(CentrifugeRecipe recipe) {
        return this.catalystAmount >= recipe.catalystAmount();
    }
    //endregion

    //region Helpers
    private void resetProgress() {
        this.progress = 0;
    }

    private int calculateEnergyPerTick() {
        Optional<RecipeHolder<CentrifugeRecipe>> recipe = getCurrentRecipe();
        if (recipe.isEmpty()) return 0;

        int time = recipe.get().value().processingTime();
        int catalyst = recipe.get().value().catalystAmount();

        return (getTier().energyCapacity / 1000 * time * catalyst) / SCALING_DIVISOR;
    }

    private boolean canInsertItemIntoOutputSlot(ItemStack stack) {
        return this.itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty() || this.itemHandler.getStackInSlot(OUTPUT_SLOT).getItem() == stack.getItem();
    }

    private boolean canInsertAmountIntoOutputSlot() {
        return this.itemHandler.getStackInSlot(OUTPUT_SLOT).getMaxStackSize() > this.itemHandler.getStackInSlot(OUTPUT_SLOT).getCount();
    }
    //endregion

    //region Boilerplate
    @Override
    protected String getEnergyTagName() { return "centrifuge_energy"; }

    @Override
    protected String getInventoryTagName() { return "centrifuge_inventory"; }

    @Override
    protected boolean isItemValid(int slot, ItemStack stack) {
        return switch (slot) {
            case INPUT_SLOT, CATALYST_FILL_SLOT -> true;
            case OUTPUT_SLOT -> false;
            default -> super.isItemValid(slot, stack);
        };
    }

    @Override
    public Component getDisplayName() { return Component.literal("Electric Centrifuge"); }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory) {
        return new com.grobe.techrebirth.gui.electric_centrifuge.ElectricCentrifugeMenu(id, playerInventory, this, this.data);
    }
    //endregion

    //region NBT
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putInt("centrifuge.progress", this.progress);
        tag.putInt("centrifuge.catalyst_amount", this.catalystAmount);
        if (!this.catalystStack.isEmpty()) {
            tag.put("centrifuge.catalyst_type", this.catalystStack.save(provider));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        this.progress = tag.getInt("centrifuge.progress");
        this.catalystAmount = tag.getInt("centrifuge.catalyst_amount");
        if (tag.contains("centrifuge.catalyst_type")) {
            this.catalystStack = ItemStack.parse(provider, tag.getCompound("centrifuge.catalyst_type")).orElse(ItemStack.EMPTY);
        }
    }
    //endregion
}
