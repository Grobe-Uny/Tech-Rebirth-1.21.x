package com.grobe.techrebirth.block.custom.entity.infuser;

import com.grobe.techrebirth.block.ModBlockEntities;
import com.grobe.techrebirth.block.custom.entity.BaseMachineBlockEntity;
import com.grobe.techrebirth.gui.infuser.FluidInfuserMenu;
import com.grobe.techrebirth.item.ModItems;
import com.grobe.techrebirth.item.custom.UpgradeItem;
import com.grobe.techrebirth.recipe.FluidInfuserRecipe;
import com.grobe.techrebirth.recipe.ModRecipeTypes;
import com.grobe.techrebirth.util.MachineTier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class FluidInfuserBlockEntity extends BaseMachineBlockEntity {

    public static final int INPUT_SLOT = 0;
    public static final int OUTPUT_SLOT = 1;
    public static final int UPGRADE_SLOT_1 = 2;
    public static final int UPGRADE_SLOT_2 = 3;
    public static final int FLUID_INPUT_SLOT = 4;

    private static final int BASE_ENERGY_COST = 60;
    private static final float SPEED_GAIN_PER_UPGRADE = 0.47f;
    private static final float POWER_COST_PER_SPEED_UPGRADE = 0.5f;
    private static final float EFFICIENCY_FACTOR = 0.1f;

    public static final int FLUID_CAPACITY = 10000;
    private final FluidTank fluidTank = new FluidTank(FLUID_CAPACITY) {
        @Override
        protected void onContentsChanged() {
            setChanged();
            if (level != null && !level.isClientSide) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    };

    public FluidInfuserBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state, 5, MachineTier.BASIC, 5); // 5 slots, 5 data values
    }

    public FluidInfuserBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntities.FLUID_INFUSER.get(), pos, state);
    }

    @Override
    protected String getEnergyTagName() {
        return "fluid_infuser_energy";
    }

    @Override
    protected String getInventoryTagName() {
        return "fluid_infuser_inventory";
    }

    protected String getFluidTagName() {
        return "fluid_infuser_fluid";
    }

    @Override
    protected boolean isItemValid(int slot, ItemStack stack) {
        return switch (slot) {
            case INPUT_SLOT -> true;
            case OUTPUT_SLOT -> false;
            case UPGRADE_SLOT_1, UPGRADE_SLOT_2 -> isValidUpgradeForThisMachine(stack);
            case FLUID_INPUT_SLOT -> true; // Allow buckets/fluid containers
            default -> false;
        };
    }

    @Override
    protected boolean hasRecipe() {
        return getCurrentRecipe().isPresent();
    }

    @Override
    protected void finishProcessing() {
        Optional<RecipeHolder<FluidInfuserRecipe>> recipeHolder = getCurrentRecipe();
        if (recipeHolder.isEmpty()) return;

        FluidInfuserRecipe recipe = recipeHolder.get().value();
        ItemStack result = recipe.getResult();

        // Consume item
        this.itemHandler.extractItem(INPUT_SLOT, 1, false);
        // Consume fluid
        this.fluidTank.drain(recipe.getFluidInput().getAmount(), IFluidHandler.FluidAction.EXECUTE);

        // Add result
        this.itemHandler.insertItem(OUTPUT_SLOT, result.copy(), false);

        resetProgress();
    }

    @Override
    protected void initProgressData() {
        getCurrentRecipe().ifPresent(recipe -> {
            int speedUpgrades = getUpgradeCount(ModItems.SPEED_UPGRADE.get());
            float totalSpeedMultiplier = 1.0f + (SPEED_GAIN_PER_UPGRADE * speedUpgrades);
            this.maxProgress = Math.max(1, (int) (recipe.value().getTime() / totalSpeedMultiplier));
        });
    }

    @Override
    protected int getEnergyCostPerTick() {
        int speedUpgrades = getUpgradeCount(ModItems.SPEED_UPGRADE.get());
        int efficiencyUpgrades = getUpgradeCount(ModItems.EFFICIENCY_UPGRADE.get());

        float speedPenalty = 1.0f + (POWER_COST_PER_SPEED_UPGRADE * speedUpgrades);
        float efficiencyBonus = 1.0f / (1.0f + (EFFICIENCY_FACTOR * efficiencyUpgrades));
        float tierMultiplier = getTier().energyMultiplier;

        return Math.max(1, (int) (BASE_ENERGY_COST * speedPenalty * efficiencyBonus * tierMultiplier));
    }

    @Override
    protected boolean canContinueProcessing() {
        return getCurrentRecipe().map(recipeHolder -> {
            FluidInfuserRecipe recipe = recipeHolder.value();
            ItemStack result = recipe.getResult();

            boolean hasEnoughFluid = fluidTank.getFluidAmount() >= recipe.getFluidInput().getAmount() &&
                    fluidTank.getFluid().is(recipe.getFluidInput().getFluid());

            return hasEnoughFluid && canInsertAmountIntoOutputSlot() && canInsertItemIntoOutputSlot(result.getItem());
        }).orElse(false);
    }

    private Optional<RecipeHolder<FluidInfuserRecipe>> getCurrentRecipe() {
        if (this.level == null) return Optional.empty();
        SingleRecipeInput input = new SingleRecipeInput(this.itemHandler.getStackInSlot(INPUT_SLOT));
        return this.level.getRecipeManager().getRecipeFor(ModRecipeTypes.INFUSER_TYPE.get(), input, this.level);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.techrebirth.fluid_infuser");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new FluidInfuserMenu(containerId, playerInventory, this, this.data);
    }

    private int getUpgradeCount(Item upgradeItem) {
        int count = 0;
        for (int i = UPGRADE_SLOT_1; i <= UPGRADE_SLOT_2; i++) {
            ItemStack stack = this.itemHandler.getStackInSlot(i);
            if (stack.getItem() instanceof UpgradeItem && stack.is(upgradeItem)) {
                count += stack.getCount();
            }
        }
        return count;
    }

    private boolean canInsertItemIntoOutputSlot(Item item) {
        ItemStack outputStack = this.itemHandler.getStackInSlot(OUTPUT_SLOT);
        return outputStack.isEmpty() || outputStack.is(item);
    }

    private boolean canInsertAmountIntoOutputSlot() {
        ItemStack outputStack = this.itemHandler.getStackInSlot(OUTPUT_SLOT);
        return outputStack.getCount() < outputStack.getMaxStackSize();
    }

    @Override
    public void tick(net.minecraft.world.level.Level level, BlockPos pos, BlockState state) {
        if (!level.isClientSide) {
            fillTankFromItem();
        }
        super.tick(level, pos, state);
    }

    private void fillTankFromItem() {
        ItemStack stack = itemHandler.getStackInSlot(FLUID_INPUT_SLOT);
        if (stack.isEmpty()) return;

        FluidUtil.getFluidHandler(stack).ifPresent(handler -> {
            FluidStack fluidInItem = handler.getFluidInTank(0);
            if (!fluidInItem.isEmpty()) {
                int amountToFill = Math.min(fluidInItem.getAmount(), fluidTank.getCapacity() - fluidTank.getFluidAmount());
                if (amountToFill > 0) {
                    FluidStack fluidToFill = fluidInItem.copy();
                    fluidToFill.setAmount(amountToFill);
                    int filled = fluidTank.fill(fluidToFill, IFluidHandler.FluidAction.EXECUTE);
                    if (filled > 0) {
                        handler.drain(filled, IFluidHandler.FluidAction.EXECUTE);
                        itemHandler.setStackInSlot(FLUID_INPUT_SLOT, handler.getContainer());
                    }
                }
            }
        });
    }

    public FluidTank getFluidTank() {
        return fluidTank;
    }

    @Override
    public IFluidHandler getFluidHandler() {
        return fluidTank;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        CompoundTag fluidTag = new CompoundTag();
        fluidTank.writeToNBT(provider, fluidTag);
        tag.put(getFluidTagName(), fluidTag);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        if (tag.contains(getFluidTagName())) {
            fluidTank.readFromNBT(provider, tag.getCompound(getFluidTagName()));
        }
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }
}
