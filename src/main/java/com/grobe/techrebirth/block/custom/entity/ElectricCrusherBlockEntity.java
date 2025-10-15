package com.grobe.techrebirth.block.custom.entity;

import com.grobe.techrebirth.block.ModBlockEntities;
import com.grobe.techrebirth.gui.electric_crusher.ElectricCrusherMenu;
import com.grobe.techrebirth.item.ModItems;
import com.grobe.techrebirth.item.custom.UpgradeItem;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class ElectricCrusherBlockEntity extends BaseMachineBlockEntity implements MenuProvider {
    protected final ContainerData data;

    private int progress = 0;
    private int maxProgress = 72;

    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;
    private static final int UPGRADE_SLOT_1 = 2;
    private static final int UPGRADE_SLOT_2 = 3;

    public ElectricCrusherBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntities.ELECTRIC_CRUSHER.get(), pos, state);
    }

    protected ElectricCrusherBlockEntity(BlockEntityType<? extends ElectricCrusherBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state, 4, 20000, 512, 512, 0, 4);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> ElectricCrusherBlockEntity.this.progress;
                    case 1 -> ElectricCrusherBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> ElectricCrusherBlockEntity.this.progress = value;
                    case 1 -> ElectricCrusherBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    @Override
    protected String getEnergyTagName() {
        return "electric_crusher_energy";
    }

    // Validation for ItemStackHandler in BaseMachine
    @Override
    protected boolean isItemValid(int slot, ItemStack stack) {
        return switch (slot) {
            case INPUT_SLOT -> isCrushable(stack);
            case OUTPUT_SLOT -> false; // output only
            case UPGRADE_SLOT_1, UPGRADE_SLOT_2 -> isValidUpgradeForThisMachine(stack);
            default -> false;
        };
    }

    private boolean isValidUpgradeForThisMachine(ItemStack stack) {
        var item = stack.getItem();
        return item == ModItems.SPEED_UPGRADE.get() || item == ModItems.EFFICIENCY_UPGRADE.get();
    }

    private boolean isCrushable(ItemStack stack) {
        if (stack.isEmpty() || this.level == null) return false;
        var input = new net.minecraft.world.item.crafting.SingleRecipeInput(stack);
        var opt = this.level.getRecipeManager().getRecipeFor(com.grobe.techrebirth.recipe.ModRecipeTypes.CRUSHING_TYPE.get(), input, this.level);
        return opt.isPresent();
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(4);
        ItemStackHandler handler =getItemHandler();
        for (int i = 0; i < 4; i++) {
            inventory.setItem(i, handler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.techrebirth.electric_crusher");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new ElectricCrusherMenu(containerId, playerInventory, this, this.data);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {

        tag.put("inventory", getItemHandler().serializeNBT(provider));
        tag.putInt("electric_crusher.progress", progress);
        tag.putInt("electric_crusher.energy", getEnergyStorage().getEnergyStored());
        super.saveAdditional(tag, provider);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        getItemHandler().deserializeNBT(provider, tag.getCompound("inventory"));
        progress = tag.getInt("electric_crusher.progress");
        this.setEnergyStored(tag.getInt("electric_crusher.energy"));
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        // Determine if we should be working this tick
        boolean shouldWork = hasRecipe();

        // Toggle the block's LIT property to reflect working state
        boolean wasLit = state.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT)
                && state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT);
        if (state.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT) && wasLit != shouldWork) {
            level.setBlock(pos, state.setValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT, shouldWork), 3);
            state = level.getBlockState(pos); // refresh local state
        }

        if (shouldWork) {
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
        var recipeOpt = getCurrentRecipe();
        if (recipeOpt == null || recipeOpt.isEmpty()) return;
        var recipe = recipeOpt.get().value();
        ItemStackHandler handler = getItemHandler();
        handler.extractItem(INPUT_SLOT, 1, false);
        ItemStack result = recipe.getResultItem(null);
        ItemStack out = handler.getStackInSlot(OUTPUT_SLOT);
        if (out.isEmpty()) {
            handler.setStackInSlot(OUTPUT_SLOT, result.copy());
        } else {
            out.grow(result.getCount());
            handler.setStackInSlot(OUTPUT_SLOT, out);
        }
        resetProgress();
    }

    private void resetProgress() { this.progress = 0; }
    private void increaseCraftingProgress() { this.progress++; }

    private boolean hasRecipe() {
        ItemStackHandler handler = getItemHandler();
        ItemStack input = handler.getStackInSlot(INPUT_SLOT);
        if (input.isEmpty()) return false;

        var recipeOpt = getCurrentRecipe();
        if (recipeOpt.isEmpty()) return false;
        ItemStack result = recipeOpt.get().value().getResultItem(null);

        // Energy requirement based on upgrades (mirror furnace logic)
        int speedUpgrades = getUpgradeCount(ModItems.SPEED_UPGRADE.get());
        int efficiencyUpgrades = getUpgradeCount(ModItems.EFFICIENCY_UPGRADE.get());
        float energyConsumptionMultiplier = (float) Math.pow(0.75, efficiencyUpgrades);
        float energySpeedPenalty = 1 + (0.5f * speedUpgrades);
        int energyToConsume = (int) (128 * energySpeedPenalty * energyConsumptionMultiplier);
        if (getEnergyStorage().getEnergyStored() < energyToConsume) return false;

        ItemStack output = handler.getStackInSlot(OUTPUT_SLOT);
        if (output.isEmpty()) return true;
        if (!ItemStack.isSameItemSameComponents(output, result)) return false;
        return output.getCount() + result.getCount() <= output.getMaxStackSize();
    }

    private java.util.Optional<net.minecraft.world.item.crafting.RecipeHolder<com.grobe.techrebirth.recipe.CrushingRecipe>> getCurrentRecipe() {
        net.minecraft.world.item.crafting.SingleRecipeInput input = new net.minecraft.world.item.crafting.SingleRecipeInput(getItemHandler().getStackInSlot(INPUT_SLOT));
        return this.level.getRecipeManager().getRecipeFor(com.grobe.techrebirth.recipe.ModRecipeTypes.CRUSHING_TYPE.get(), input, level);
    }

    private int getUpgradeCount(Item upgradeItem) {
        int count = 0;
        ItemStackHandler handler = getItemHandler();
        for (int slot : new int[]{UPGRADE_SLOT_1, UPGRADE_SLOT_2}) {
            ItemStack stack = handler.getStackInSlot(slot);
            if (!stack.isEmpty() && stack.getItem() instanceof UpgradeItem && stack.getItem() == upgradeItem) {
                count += stack.getCount();
            }
        }
        return Math.min(count, 4); // cap to avoid extremes
    }
}
