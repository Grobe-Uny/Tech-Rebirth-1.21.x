package com.grobe.techrebirth.block.custom.entity.furnace;

import com.grobe.techrebirth.block.ModBlockEntities;
import com.grobe.techrebirth.block.custom.entity.BaseMachineBlockEntity;
import com.grobe.techrebirth.gui.electric_furnace.ElectricFurnaceMenu;
import com.grobe.techrebirth.item.ModItems;
import com.grobe.techrebirth.item.custom.UpgradeItem;
import com.grobe.techrebirth.sound.ModSounds;
import com.grobe.techrebirth.util.MachineTier;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.Direction;
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
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.RangedWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import com.grobe.techrebirth.util.ModTags;

public class ElectricFurnaceBlockEntity extends BaseMachineBlockEntity implements MenuProvider {

    protected final ContainerData data;

    private static final int BASE_ENERGY_COST = 20;

    // Balancing Constants (Linear/Hyperbolic for stack support)
    private static final float SPEED_GAIN_PER_UPGRADE = 0.5f; // +50% speed per item
    private static final float POWER_COST_PER_SPEED_UPGRADE = 0.5f; // +50% power per speed item
    private static final float EFFICIENCY_FACTOR = 0.1f; // Hyperbolic reduction factor


    @Override
    protected String getEnergyTagName() {
        return "electric_furnace_energy";
    }
    @Override
    protected String getInventoryTagName(){
        return "electric_furnace_inventory";
    }

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

    private float pendingXp;

    private static int getRecipeCookTime(SmeltingRecipe recipe){
        return Math.max(1, recipe.getCookingTime());
    }

    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;
    private static final int UPGRADE_SLOT_1 = 2;
    private static final int UPGRADE_SLOT_2 = 3;

    public ElectricFurnaceBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntities.ELECTRIC_FURNACE.get(), pos, state);
    }

    public ElectricFurnaceBlockEntity(BlockEntityType<? extends ElectricFurnaceBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state, 4, MachineTier.BASIC, 4);

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
                    case 2 -> ElectricFurnaceBlockEntity.this.setEnergyStored(value); // client mirror
                    case 3 -> { /* no-op: max energy is static; client-side mirror only */ }
                }
            }

            @Override
            public int getCount() {
                return 4;
            }
        };
    }
    public ElectricFurnaceBlockEntity(BlockEntityType<? extends ElectricFurnaceBlockEntity> type, BlockPos pos, BlockState state, MachineTier tier) {
        super(type, pos, state, 4, tier, 4);

        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> ElectricFurnaceBlockEntity.this.progress;
                    case 1 -> ElectricFurnaceBlockEntity.this.maxProgress;
                    case 2 -> ElectricFurnaceBlockEntity.this.getEnergyStorage().getEnergyStored();
                    case 3 -> ElectricFurnaceBlockEntity.this.getEnergyStorage().getMaxEnergyStored();
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> ElectricFurnaceBlockEntity.this.progress = value;
                    case 1 -> ElectricFurnaceBlockEntity.this.maxProgress = value;
                    case 2 -> ElectricFurnaceBlockEntity.this.setEnergyStored(value);
                    case 3 -> { /* no-op */ }
                }
            }

            @Override
            public int getCount() {
                return 4;
            }
        };
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


    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider){
        super.saveAdditional(tag, provider);
        tag.putFloat("pendingXp", pendingXp);

    }


    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        pendingXp = tag.getFloat("pendingXp");

    }
    private void calculateProgressTime(SmeltingRecipe recipe){
        int speedUpgrades = getUpgradeCount(ModItems.SPEED_UPGRADE.get());

        int vanillaTime = Math.max(1, getRecipeCookTime(recipe));

        // Linear speed increase: 100% + (50% * count)
        float totalSpeedMultiplier = 1.0f + (SPEED_GAIN_PER_UPGRADE * speedUpgrades);

        this.maxProgress = Math.max(1, (int) (vanillaTime / totalSpeedMultiplier));
    }

    protected int getEnergyCostPerTick(){
        int speedUpgrades = getUpgradeCount(ModItems.SPEED_UPGRADE.get());
        int efficiencyUpgrades = getUpgradeCount(ModItems.EFFICIENCY_UPGRADE.get());

        // Linear power cost for speed: 1 + (0.5 * count)
        float speedPenalty = 1.0f + (POWER_COST_PER_SPEED_UPGRADE * speedUpgrades);

        // Hyperbolic power reduction for efficiency: 1 / (1 + 0.1 * count)
        float efficiencyBonus = 1.0f / (1.0f + (EFFICIENCY_FACTOR * efficiencyUpgrades));

        float tierMultiplier = getTier().energyMultiplier;

        return Math.max(1, (int) (BASE_ENERGY_COST * speedPenalty * efficiencyBonus * tierMultiplier));
    }

    @Override
    protected void finishProcessing() {
        craftItem();
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

    @Override
    protected boolean hasRecipe() {
        Optional<RecipeHolder<SmeltingRecipe>> recipe = getCurrentRecipe();
        return recipe.isPresent();
    }

    @Override
    protected boolean canContinueProcessing() {
        Optional<RecipeHolder<SmeltingRecipe>> recipe = getCurrentRecipe();
        if (recipe.isEmpty()) return false;

        ItemStack result = recipe.get().value().getResultItem(null);
        return canInsertAmountIntoOutputSlot(result.getCount()) &&
                canInsertItemIntoOutputSlot(result.getItem());
    }

    @Override
    protected void initProgressData() {
        getCurrentRecipe().ifPresent(recipeHolder -> calculateProgressTime(recipeHolder.value()));
    }

    @Override
    protected SoundEvent getWorkingSound() {
        return ModSounds.FURNACE_RUNNING.get();
    }
    @Override
    protected float getWorkingSoundVolume() {
        return 4.0f;
    }

    private int getUpgradeCount(Item upgradeItem) {
        int count = 0;
        ItemStackHandler handler = getItemHandler();
        ItemStack stack1 = handler.getStackInSlot(UPGRADE_SLOT_1);
        ItemStack stack2 = handler.getStackInSlot(UPGRADE_SLOT_2);

        if (stack1.getItem() instanceof UpgradeItem && stack1.is(upgradeItem)) count += stack1.getCount();
        if (stack2.getItem() instanceof UpgradeItem && stack2.is(upgradeItem)) count += stack2.getCount();

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
    // OVERRIDE za side-based handling ako želiš custom logiku
    @Override
    public IItemHandler getSidedItemHandler(Direction side) {
        ItemStackHandler baseHandler =  getItemHandler();

        if (side == Direction.DOWN) {
            // Donja strana - samo output slot (slot 1), može se extractati
            return new RangedWrapper(baseHandler, 1, 2) {
                @Override
                public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
                    return stack; // Ne može se insertati
                }
            };
        } else {
            // Ostale strane - samo input slot (slot 0), može se insertati
            return new RangedWrapper(baseHandler, 0, 1) {
                @Override
                public ItemStack extractItem(int slot, int amount, boolean simulate) {
                    return ItemStack.EMPTY; // Ne može se extractati
                }
            };
        }
    }
}
