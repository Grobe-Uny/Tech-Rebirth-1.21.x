package com.grobe.techrebirth.block.custom.entity.crusher;

import com.grobe.techrebirth.block.ModBlockEntities;
import com.grobe.techrebirth.block.custom.entity.BaseMachineBlockEntity;
import com.grobe.techrebirth.gui.electric_crusher.ElectricCrusherMenu;
import com.grobe.techrebirth.item.ModItems;
import com.grobe.techrebirth.item.custom.UpgradeItem;
import com.grobe.techrebirth.recipe.CrushingRecipe;
import com.grobe.techrebirth.recipe.ModRecipeTypes;
import com.grobe.techrebirth.util.MachineTier;
import com.grobe.techrebirth.util.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
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
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ElectricCrusherBlockEntity extends BaseMachineBlockEntity implements MenuProvider {


    private static final int INPUT_SLOT = 0;
    private static final int PRIMARY_OUTPUT_1 = 1;
    private static final int PRIMARY_OUTPUT_2 = 2;
    private static final int CHANCE_OUTPUT_1 = 3;
    private static final int UPGRADE_SLOT_1 = 4;
    private static final int UPGRADE_SLOT_2 = 5;

    public ElectricCrusherBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntities.ELECTRIC_CRUSHER.get(), pos, state);
    }

    protected ElectricCrusherBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state, 6, MachineTier.BASIC, 4);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> ElectricCrusherBlockEntity.this.progress;
                    case 1 -> ElectricCrusherBlockEntity.this.maxProgress;
                    case 2 -> ElectricCrusherBlockEntity.this.getEnergyStorage().getEnergyStored();
                    case 3 -> ElectricCrusherBlockEntity.this.getEnergyStorage().getMaxEnergyStored();
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> ElectricCrusherBlockEntity.this.progress = value;
                    case 1 -> ElectricCrusherBlockEntity.this.maxProgress = value;
                    case 2 -> ElectricCrusherBlockEntity.this.setEnergyStored(value);
                    case 3 -> {}
                }
            }

            @Override
            public int getCount() {
                return 4;
            }
        };
    }

    @Override
    protected String getEnergyTagName() {
        return "electric_crusher_energy";
    }

    @Override
    protected String getInventoryTagName(){return "electric_crusher_inventory";}

    // Validation for ItemStackHandler in BaseMachine
    @Override
    protected boolean isItemValid(int slot, ItemStack stack) {
        return switch (slot) {
            case INPUT_SLOT -> isCrushable(stack);
            case PRIMARY_OUTPUT_1,PRIMARY_OUTPUT_2, CHANCE_OUTPUT_1 -> false; // output only
            case UPGRADE_SLOT_1, UPGRADE_SLOT_2 -> isValidUpgradeForThisMachine(stack);
            default -> false;
        };
    }

    private boolean isValidUpgradeForThisMachine(ItemStack stack) {
        var item = stack.getItem();
        return item == ModItems.SPEED_UPGRADE.get() || item == ModItems.EFFICIENCY_UPGRADE.get();
    }

    private boolean isCrushable(ItemStack stack) {
//        if (stack.isEmpty() || this.level == null) return false;
//            // Debug ispis
//        System.out.println("🔍 Checking: " + stack.getItem().getDescriptionId());
//        System.out.println("🔍 RecipeType: " + ModRecipeTypes.CRUSHING_TYPE);
//        System.out.println("🔍 RecipeType.get(): " + ModRecipeTypes.CRUSHING_TYPE.get());
//
//
//
//        var input = new SingleRecipeInput(stack);
//        var opt = this.level.getRecipeManager().getRecipeFor(ModRecipeTypes.CRUSHING_TYPE.get(), input, this.level);
//        return opt.isPresent();

        if (stack.isEmpty() || this.level == null) return false;

        // ✅ PRVO provjeri tagove (brže i sigurnije)
        if (isTaggedAsCrushable(stack)) {
            return true;
        }

        // ✅ ONDA provjeri specifične recepte (fallback)
        var input = new SingleRecipeInput(stack);
        var opt = this.level.getRecipeManager().getRecipeFor(ModRecipeTypes.CRUSHING_TYPE.get(), input, this.level);
        return opt.isPresent();
    }

    private boolean isTaggedAsCrushable(ItemStack stack) {
        try {
            HolderLookup.RegistryLookup<Item> items = this.level.registryAccess().lookupOrThrow(Registries.ITEM);

            // ✅ SAFE TAG CHECK - koristi try-catch za svaki tag
            return  isInTagSafely(stack, items, ModTags.Items.RAW_MATERIALS_D.common()) ||
                    isInTagSafely(stack, items, ModTags.Items.RAW_TIN_D.common()) ||
                    isInTagSafely(stack, items, ModTags.Items.RAW_LEAD_D.common()) ||
                    isInTagSafely(stack, items, ModTags.Items.RAW_NICKEL_D.common()) ||
                    isInTagSafely(stack, items, Tags.Items.ORES);

        } catch (Exception e) {
            // ✅ FALLBACK: ako tag sistem faila, vrati false i koristi recipe sistem
            System.out.println("⚠️ Tag check failed, using recipe fallback: " + e.getMessage());
            return false;
        }
    }

    // ✅ SIGURNA metoda za tag provjeru
    private boolean isInTagSafely(ItemStack stack, HolderLookup.RegistryLookup<Item> items, TagKey<Item> tag) {
        try {
            return stack.is(items.getOrThrow(tag));
        } catch (Exception e) {
            // Ako tag ne postoji ili je problem, jednostavno preskoči
            return false;
        }
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(6);
        ItemStackHandler handler =getItemHandler();
        for (int i = 0; i < 6; i++) {
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
        super.saveAdditional(tag, provider);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
    }
    protected int getEnergyCostPerTick() {
        int speedUpgrades = getUpgradeCount(ModItems.SPEED_UPGRADE.get());
        int efficiencyUpgrades = getUpgradeCount(ModItems.EFFICIENCY_UPGRADE.get());

        float energyConsumptionMultiplier = 1 - (0.20f * efficiencyUpgrades);
        float energySpeedPenalty = 1 + (0.15f * speedUpgrades);

        float tierEnergyMultiplier = getTier().energyMultiplier;

        return (int) (160 * energySpeedPenalty * energyConsumptionMultiplier * tierEnergyMultiplier);
    }


    public void tick(Level level, BlockPos pos, BlockState state) {
        boolean hasEnergy = getEnergyStorage().getEnergyStored() >= getEnergyCostPerTick();
        boolean hasRecipe = hasRecipe();

        boolean shouldWork = hasRecipe && hasEnergy;
        boolean isWorking =  progress > 0;

        // Toggle LIT state
        boolean wasLit = state.hasProperty(BlockStateProperties.LIT) && state.getValue(BlockStateProperties.LIT);
        if (state.hasProperty(BlockStateProperties.LIT) && wasLit != shouldWork) {
            level.setBlock(pos, state.setValue(BlockStateProperties.LIT, shouldWork), 3);
            state = level.getBlockState(pos);
        }

        if (shouldWork) {
            var recipeOpt = getCurrentRecipe();
            if (recipeOpt.isPresent()) {
                CrushingRecipe recipe = recipeOpt.get().value();

                int speedUpgrades = getUpgradeCount(ModItems.SPEED_UPGRADE.asItem());
                // ✅ RAČUNAJ maxProgress SAMO AKO JE progress == 0 (novi crafting)
                if (progress == 0) {
                    float speedMultiplier = 1 + (0.5f * speedUpgrades);
                    int baseTime = recipe.getTime();
                    this.maxProgress = (int) (baseTime / speedMultiplier);
                    if (this.maxProgress < 1) this.maxProgress = 1;

                    // Debug ispis
                    System.out.println("🔄 NEW CRAFTING: maxProgress = " + maxProgress +
                            ", baseTime = " + baseTime + ", speedMultiplier = " + speedMultiplier);
                }

                // Energy cost
                int efficiencyUpgrades = getUpgradeCount(ModItems.EFFICIENCY_UPGRADE.get());
                float energyConsumptionMultiplier = (float) Math.pow(0.75, efficiencyUpgrades);
                float energySpeedPenalty = 1 + (0.5f * speedUpgrades);
                int energyToConsume = (int) (128 * energySpeedPenalty * energyConsumptionMultiplier);

                // Debug progress
                System.out.println("🔧 Progress: " + progress + "/" + maxProgress +
                        " (" + (maxProgress > 0 ? (progress * 100 / maxProgress) : 0) + "%)");

                if (getEnergyStorage().getEnergyStored() >= energyToConsume) {
                    getEnergyStorage().extractEnergy(energyToConsume, false);
                    increaseCraftingProgress();
                    setChanged(level, pos, state);

                    if (progress >= maxProgress) {
                        craftItem();
                        resetProgress();
                    }
                }
            } else {
                resetProgress();
            }
        } else {
            // Ako nema recepta, resetiraj progress
            if (!hasRecipe && progress > 0) {
                resetProgress();
            }
        }
    }

    private void craftItem() {
        var recipeOpt = getCurrentRecipe();
        if (recipeOpt.isEmpty()) return;

        CrushingRecipe recipe = recipeOpt.get().value();
        ItemStackHandler handler = getItemHandler();

        // Potroši input
        handler.extractItem(INPUT_SLOT, 1, false);

        // Glavni result
        ItemStack primaryResult = recipe.getResultItem(null);
        insertIntoPrimaryOutput(primaryResult);

        // Chance output (ako postoji i prođe RNG)
        if (hasChanceOutput(recipe) && level.random.nextFloat() < getChanceOutputRate(recipe)) {
            ItemStack chanceResult = getChanceOutput(recipe);
            insertIntoChanceOutput(chanceResult);
        }

        resetProgress();
    }

    private void insertIntoPrimaryOutput(ItemStack result) {
        ItemStackHandler handler = getItemHandler();

        // Prvo pokušaj PRIMARY_OUTPUT_1
        ItemStack output1 = handler.getStackInSlot(PRIMARY_OUTPUT_1);
        if (output1.isEmpty()) {
            handler.setStackInSlot(PRIMARY_OUTPUT_1, result.copy());
            return;
        }

        if (ItemStack.isSameItemSameComponents(output1, result) &&
                output1.getCount() + result.getCount() <= output1.getMaxStackSize()) {
            output1.grow(result.getCount());
            handler.setStackInSlot(PRIMARY_OUTPUT_1, output1);
            return;
        }

        // Onda PRIMARY_OUTPUT_2
        ItemStack output2 = handler.getStackInSlot(PRIMARY_OUTPUT_2);
        if (output2.isEmpty()) {
            handler.setStackInSlot(PRIMARY_OUTPUT_2, result.copy());
            return;
        }

        if (ItemStack.isSameItemSameComponents(output2, result) &&
                output2.getCount() + result.getCount() <= output2.getMaxStackSize()) {
            output2.grow(result.getCount());
            handler.setStackInSlot(PRIMARY_OUTPUT_2, output2);
            return;
        }
    }

    // ✅ Nova metoda - stavi chance output
    private void insertIntoChanceOutput(ItemStack result) {
        ItemStackHandler handler = getItemHandler();
        ItemStack chanceSlot = handler.getStackInSlot(CHANCE_OUTPUT_1);

        if (chanceSlot.isEmpty()) {
            handler.setStackInSlot(CHANCE_OUTPUT_1, result.copy());
        } else if (ItemStack.isSameItemSameComponents(chanceSlot, result) &&
                chanceSlot.getCount() + result.getCount() <= chanceSlot.getMaxStackSize()) {
            chanceSlot.grow(result.getCount());
            handler.setStackInSlot(CHANCE_OUTPUT_1, chanceSlot);
        }
    }

    // ✅ Nova metoda - dobij chance rate (npr. 0.1 = 10% šanse)
    private float getChanceOutputRate(CrushingRecipe recipe) {
        return recipe.getChanceRate(); // Prilagodi prema tvojoj implementaciji
    }

    private void resetProgress() { this.progress = 0; System.out.println("Progress Reset");}
    private void increaseCraftingProgress() { this.progress++;
    if(progress % 10 == 0 ){
        System.out.println("📈 Progress: " + progress + "/" + maxProgress);
        }
    }

    private boolean hasRecipe() {
        ItemStackHandler handler = getItemHandler();
        ItemStack input = handler.getStackInSlot(INPUT_SLOT);
        if (input.isEmpty()) return false;

        var recipeOpt = getCurrentRecipe();
        if (recipeOpt.isEmpty()) return false;

        CrushingRecipe recipe = recipeOpt.get().value();
        ItemStack primaryResult = recipe.getResultItem(null);

        // Energy check
//        int energyToConsume = getEnergyCostPerTick();
//        if (getEnergyStorage().getEnergyStored() < energyToConsume) return false;

        // ✅ PROVJERA: Glavni result mora moći u PRIMARY_OUTPUT_1 ili PRIMARY_OUTPUT_2
        boolean canInsertPrimary = canInsertIntoPrimaryOutput(primaryResult);

        // ✅ PROVJERA: Ako recept ima chance output, mora moći u CHANCE_OUTPUT
        boolean canInsertChance = true; // Default true ako nema chance output
        if (hasChanceOutput(recipe)) {
            ItemStack chanceResult = getChanceOutput(recipe);
            canInsertChance = canInsertIntoChanceOutput(chanceResult);
        }

        return canInsertPrimary && canInsertChance;
    }
    private boolean canInsertIntoPrimaryOutput(ItemStack primaryResult) {
        ItemStackHandler handler = getItemHandler();

        // Provjeri PRIMARY_OUTPUT_1
        ItemStack output1 = handler.getStackInSlot(PRIMARY_OUTPUT_1);
        if (output1.isEmpty() ||
                (ItemStack.isSameItemSameComponents(output1, primaryResult) &&
                        output1.getCount() + primaryResult.getCount() <= output1.getMaxStackSize())) {
            return true;
        }

        // Provjeri PRIMARY_OUTPUT_2
        ItemStack output2 = handler.getStackInSlot(PRIMARY_OUTPUT_2);
        if (output2.isEmpty() ||
                (ItemStack.isSameItemSameComponents(output2, primaryResult) &&
                        output2.getCount() + primaryResult.getCount() <= output2.getMaxStackSize())) {
            return true;
        }

        return false;
    }

    private boolean canInsertIntoChanceOutput(ItemStack chanceResult) {
        ItemStackHandler handler = getItemHandler();
        ItemStack chanceSlot = handler.getStackInSlot(CHANCE_OUTPUT_1);

        return chanceSlot.isEmpty() ||
                (ItemStack.isSameItemSameComponents(chanceSlot, chanceResult) &&
                        chanceSlot.getCount() + chanceResult.getCount() <= chanceSlot.getMaxStackSize());
    }

    private boolean hasChanceOutput(CrushingRecipe recipe) {
        // Ovo ovisi kako si implementirao CrushingRecipe
        // Možda imaš recipe.getChanceOutput() ili slično
        return recipe.hasChanceOutput(); // Prilagodi prema tvojoj implementaciji
    }

    private ItemStack getChanceOutput(CrushingRecipe recipe) {
        // Prilagodi prema tvojoj recipe klasi
        return recipe.getChanceOutput();
    }

    private Optional<RecipeHolder<CrushingRecipe>> getCurrentRecipe() {
        SingleRecipeInput input = new SingleRecipeInput(getItemHandler().getStackInSlot(INPUT_SLOT));
        return this.level.getRecipeManager().getRecipeFor(ModRecipeTypes.CRUSHING_TYPE.get(), input, level);
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
