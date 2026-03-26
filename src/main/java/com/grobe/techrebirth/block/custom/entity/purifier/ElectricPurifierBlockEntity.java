package com.grobe.techrebirth.block.custom.entity.purifier;

import com.grobe.techrebirth.block.ModBlockEntities;
import com.grobe.techrebirth.block.custom.entity.BaseMachineBlockEntity;
import com.grobe.techrebirth.gui.electric_purifier.ElectricPurifierMenu;
import com.grobe.techrebirth.item.ModItems;
import com.grobe.techrebirth.item.custom.UpgradeItem;
import com.grobe.techrebirth.recipe.PurifierRecipe;
import com.grobe.techrebirth.recipe.ModRecipeTypes;
import com.grobe.techrebirth.sound.ModSounds;
import com.grobe.techrebirth.util.MachineTier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ElectricPurifierBlockEntity extends BaseMachineBlockEntity {

    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;
    private static final int UPGRADE_SLOT_1 = 2;
    private static final int UPGRADE_SLOT_2 = 3;

    private static final int BASE_ENERGY_COST = 40;
    private static final float SPEED_GAIN_PER_UPGRADE = 0.5f;
    private static final float POWER_COST_PER_SPEED_UPGRADE = 0.5f;
    private static final float EFFICIENCY_FACTOR = 0.1f;

    public ElectricPurifierBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntities.ELECTRIC_PURIFIER.get(), pos, state);
    }

    public ElectricPurifierBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state, 4, MachineTier.BASIC, 5); // 4 slots, 5 data values
    }

    @Override
    protected String getEnergyTagName() {
        return "electric_purifier_energy";
    }

    @Override
    protected String getInventoryTagName() {
        return "electric_purifier_inventory";
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.techrebirth.electric_purifier");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new ElectricPurifierMenu(containerId, playerInventory, this, this.data);
    }

    @Override
    protected boolean isItemValid(int slot, ItemStack stack) {
        return switch (slot) {
            case INPUT_SLOT -> true; // Allow any item, recipe will check validity
            case OUTPUT_SLOT -> false;
            case UPGRADE_SLOT_1, UPGRADE_SLOT_2 -> isValidUpgradeForThisMachine(stack);
            default -> false;
        };
    }
//
//    private boolean isValidUpgradeForThisMachine(ItemStack stack) {
//        Item item = stack.getItem();
//        return item == ModItems.SPEED_UPGRADE.get() || item == ModItems.EFFICIENCY_UPGRADE.get();
//    }

    @Override
    protected boolean hasRecipe() {
        return getCurrentRecipe().isPresent();
    }

    @Override
    protected void finishProcessing() {
        Optional<RecipeHolder<PurifierRecipe>> recipe = getCurrentRecipe();
        if (recipe.isEmpty()) return;

        ItemStack result = recipe.get().value().getResultItem(null);
        this.itemHandler.extractItem(INPUT_SLOT, 1, false);
        this.itemHandler.setStackInSlot(OUTPUT_SLOT, new ItemStack(result.getItem(),
                this.itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() + result.getCount()));

        resetProgress();
    }

    @Override
    protected void initProgressData() {
        getCurrentRecipe().ifPresent(recipe -> {
            int speedUpgrades = getUpgradeCount(ModItems.SPEED_UPGRADE.get());
            float totalSpeedMultiplier = 1.0f + (SPEED_GAIN_PER_UPGRADE * speedUpgrades);
            this.maxProgress = Math.max(1, (int) (recipe.value().getProcessingTime() / totalSpeedMultiplier));
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
        return getCurrentRecipe().map(recipe -> {
            ItemStack result = recipe.value().getResultItem(null);
            return canInsertAmountIntoOutputSlot() && canInsertItemIntoOutputSlot(result.getItem());
        }).orElse(false);
    }

    private Optional<RecipeHolder<PurifierRecipe>> getCurrentRecipe() {
        if (this.level == null) return Optional.empty();
        SingleRecipeInput input = new SingleRecipeInput(this.itemHandler.getStackInSlot(INPUT_SLOT));
        return this.level.getRecipeManager().getRecipeFor(ModRecipeTypes.PURIFIER_TYPE.get(), input, this.level);
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
        return this.itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty() || this.itemHandler.getStackInSlot(OUTPUT_SLOT).is(item);
    }

    private boolean canInsertAmountIntoOutputSlot() {
        return this.itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() < this.itemHandler.getStackInSlot(OUTPUT_SLOT).getMaxStackSize();
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }
}
