package com.grobe.techrebirth.block.custom.entity.alloy;

import com.grobe.techrebirth.block.ModBlockEntities;
import com.grobe.techrebirth.block.custom.alloy.AlloySmelterBlock;
import com.grobe.techrebirth.block.custom.entity.BaseMachineBlockEntity;
import com.grobe.techrebirth.gui.alloy_smelter.AlloySmelterMenu;
import com.grobe.techrebirth.item.ModItems;
import com.grobe.techrebirth.recipe.AlloySmeltingRecipe;
import com.grobe.techrebirth.recipe.ModRecipeTypes;
import com.grobe.techrebirth.recipe.MultiItemRecipeInput;
import com.grobe.techrebirth.sound.ClientSoundHelper;
import com.grobe.techrebirth.sound.ModSounds;
import com.grobe.techrebirth.util.MachineTier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class AlloySmelterBlockEntity extends BaseMachineBlockEntity {
    private static final int INPUT_SLOT_1 = 0;
    private static final int INPUT_SLOT_2 = 1;
    private static final int INPUT_SLOT_3 = 2;
    private static final int OUTPUT_SLOT = 3;
    private static final int UPGRADE_SLOT_1 = 4;
    private static final int UPGRADE_SLOT_2 = 5;


    private boolean soundPlaying = false;

    // Cache za performance
    private Optional<RecipeHolder<AlloySmeltingRecipe>> cachedRecipe = Optional.empty();
    private boolean recipeCacheValid = false;

    protected int maxProgress;
    private final Map<Integer, Integer> consumedInputs = new HashMap<>();

    public AlloySmelterBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntities.ALLOY_SMELTER.get(), pos, state);
    }


    protected AlloySmelterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state, 6, MachineTier.BASIC, 4);

        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> AlloySmelterBlockEntity.this.progress;                    // cook progress
                    case 1 -> AlloySmelterBlockEntity.this.maxProgress;                 // max progress
                    case 2 -> AlloySmelterBlockEntity.this.getEnergyStorage().getEnergyStored(); // energy stored
                    case 3 -> AlloySmelterBlockEntity.this.getEnergyStorage().getMaxEnergyStored(); // max energy
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> AlloySmelterBlockEntity.this.progress = value;
                    case 1 -> AlloySmelterBlockEntity.this.maxProgress = value;
                    case 2 -> AlloySmelterBlockEntity.this.setEnergyStored(value); // client mirror
                    case 3 -> { /* no-op: max energy is static; client-side mirror only */ }
                }
            }

            @Override
            public int getCount() {
                return 4;
            }
        };
    }
    protected AlloySmelterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, MachineTier tier) {
        super(type, pos, state, 6, tier, 4);
    }

    @Override
    public int getMaxProgress() {
        return this.maxProgress;
    }

    // ILI još bolje - overrideaj i getProgressPercent za točan prikaz:
    @Override
    public float getProgressPercent() {
        if (this.maxProgress <= 0) return 0;
        return (float) progress / this.maxProgress;
    }

    public ContainerData getContainerData() {
        return super.getContainerData();
    }

    @Override
    protected String getEnergyTagName() {
        return "alloy_smelter_energy";
    }

    @Override
    protected String getInventoryTagName() {
        return "alloy_smelter_inventory";
    }

    @Override
    protected boolean isItemValid(int slot, ItemStack stack) {
        return switch (slot) {
            case INPUT_SLOT_1, INPUT_SLOT_2, INPUT_SLOT_3 -> true;
            case OUTPUT_SLOT -> false;
            case UPGRADE_SLOT_1, UPGRADE_SLOT_2 -> isValidUpgrade(stack);
            default -> false;
        };
    }

    private boolean isValidUpgrade(ItemStack stack) {
        Item item = stack.getItem();
        return item == ModItems.EFFICIENCY_UPGRADE.get() || item == ModItems.SPEED_UPGRADE.get();
    }

    protected boolean canProcess() {
        if (!hasRecipe()) return false;

        ItemStack result = getCurrentRecipe().get().value().getResultItem(null);
        int energyCost = getEnergyCostPerTick();

        // Provjeri ima li dovoljno inputa
        if (!hasEnoughInputItems()) {
            return false;
        }

        // Provjeri može li se output staviti
        if (!canInsertOutput(result)) {
            return false;
        }

        // Provjeri ima li dovoljno energije
        return getEnergyStorage().getEnergyStored() >= energyCost;
    }

    protected void finishProcessing() {
        craftItem();
    }

    protected int getEnergyCostPerTick() {
        int speedUpgrades = getUpgradeCount(ModItems.SPEED_UPGRADE.get());
        int efficiencyUpgrades = getUpgradeCount(ModItems.EFFICIENCY_UPGRADE.get());

        float energyConsumptionMultiplier = 1 - (0.20f * efficiencyUpgrades);
        float energySpeedPenalty = 1 + (0.15f * speedUpgrades);

        float tierEnergyMultiplier = getTier().energyMultiplier;

        return (int) (160 * energySpeedPenalty * energyConsumptionMultiplier * tierEnergyMultiplier);
    }

    protected void onProcessStart() {
        //place for particles and sounds
        if(level instanceof ServerLevel serverLevel){
            serverLevel.sendParticles(ParticleTypes.SMOKE,
                    worldPosition.getX() + 0.5, worldPosition.getY() + 1.2, worldPosition.getZ() + 0.5,
                    12,
                    0.2, 0.2, 0.2,
                    0.05);
        }
    }

    private void updateSound() {
        // Check isActuallyProcessing() which reflects the LIT state
        if (this.level.isClientSide && isActuallyProcessing()) {
            if (!soundPlaying) {
                ClientSoundHelper.playAlloySmelterSound(this);
                soundPlaying = true;
            }
        } else {
            // If the machine stops working, reset the flag so it can be started again later
            soundPlaying = false;
        }
    }

    protected void onInventoryChanged(int slot) {
        // Invalidate cache kada se input promijeni
        if (slot == INPUT_SLOT_1 || slot == INPUT_SLOT_2 || slot == INPUT_SLOT_3) {
            recipeCacheValid = false;
        }
    }

    private boolean hasRecipe() {
        return getCurrentRecipe().isPresent();
    }

    private Optional<RecipeHolder<AlloySmeltingRecipe>> getCurrentRecipe() {
        if (!recipeCacheValid || cachedRecipe.isEmpty()) {
            // Uzmi sve 3 input stavke
            ItemStack input1 = getItemHandler().getStackInSlot(INPUT_SLOT_1);
            ItemStack input2 = getItemHandler().getStackInSlot(INPUT_SLOT_2);
            ItemStack input3 = getItemHandler().getStackInSlot(INPUT_SLOT_3);

            // Kreiraj MultiItemRecipeInput sa svim input stavkama
            MultiItemRecipeInput input = new MultiItemRecipeInput(input1, input2, input3);

            cachedRecipe = level.getRecipeManager().getRecipeFor(
                    ModRecipeTypes.ALLOY_SMELTING_TYPE.get(),
                    input, level
            );
            recipeCacheValid = true;
        }
        return cachedRecipe;
    }

    private void craftItem() {
        Optional<RecipeHolder<AlloySmeltingRecipe>> recipeOpt = getCurrentRecipe();
        if (recipeOpt.isEmpty()) return;

        AlloySmeltingRecipe recipe = recipeOpt.get().value();
        ItemStack result = recipe.getResultItem(null);

        // POTROŠI INPUTE - jednostavna implementacija
        consumeInputsForRecipe(recipe);

        // Produce output
        ItemStack output = getItemHandler().getStackInSlot(OUTPUT_SLOT);
        if (output.isEmpty()) {
            getItemHandler().setStackInSlot(OUTPUT_SLOT, result.copy());
        } else if (ItemStack.isSameItemSameComponents(output, result)) {
            output.grow(result.getCount());
        }

        recipeCacheValid = false;
        setChanged(); // OBAVEZNO: označi da se promijenilo
    }

    private void consumeInputsForRecipe(AlloySmeltingRecipe recipe) {
        System.out.println("🔄 consumeInputsForRecipe() called");

        ItemStack[] workingStacks = new ItemStack[3];
        for (int i = 0; i < 3; i++) {
            workingStacks[i] = getItemHandler().getStackInSlot(i).copy();
            System.out.println("📦 Slot " + i + " before: " + workingStacks[i].getItem().getDescriptionId() + " x" + workingStacks[i].getCount());
        }

        // Mapiraj ingredient-e i njihove količine
        Map<Ingredient, Integer> ingredientsNeeded = new HashMap<>();
        for (Ingredient ingredient : recipe.getIngredients()) {
            ingredientsNeeded.put(ingredient, ingredientsNeeded.getOrDefault(ingredient, 0) + 1);
            System.out.println("📋 Need to consume: " + ingredient + " (count: " + ingredientsNeeded.get(ingredient) + ")");
        }

        // Potroši iteme iz working kopija
        for (int slot = 0; slot < 3; slot++) {
            ItemStack stack = workingStacks[slot];
            if (stack.isEmpty()) continue;

            for (Iterator<Map.Entry<Ingredient, Integer>> iterator = ingredientsNeeded.entrySet().iterator(); iterator.hasNext();) {
                Map.Entry<Ingredient, Integer> entry = iterator.next();
                Ingredient ingredient = entry.getKey();
                int needed = entry.getValue();

                if (needed > 0 && ingredient.test(stack)) {
                    int toConsume = Math.min(needed, stack.getCount());
                    System.out.println("🍽️ Consuming " + toConsume + " from slot " + slot + " for " + ingredient);

                    stack.shrink(toConsume);
                    needed -= toConsume;

                    if (needed <= 0) {
                        iterator.remove();
                        System.out.println("✅ Finished consuming: " + ingredient);
                    } else {
                        entry.setValue(needed);
                        System.out.println("🔄 Still need " + needed + " more of: " + ingredient);
                    }

                    if (stack.isEmpty()) break;
                }
            }
        }

        // PRIMJENI promjene na stvarne slotove
        for (int i = 0; i < 3; i++) {
            getItemHandler().setStackInSlot(i, workingStacks[i]);
            System.out.println("📦 Slot " + i + " after: " + workingStacks[i].getItem().getDescriptionId() + " x" + workingStacks[i].getCount());
        }
}

    private boolean hasEnoughInputItems() {
        Optional<RecipeHolder<AlloySmeltingRecipe>> recipeOpt = getCurrentRecipe();
        if (recipeOpt.isEmpty()) return false;

        AlloySmeltingRecipe recipe = recipeOpt.get().value();

        // Kopiraj slotove za provjeru
        ItemStack[] checkStacks = new ItemStack[3];
        for (int i = 0; i < 3; i++) {
            checkStacks[i] = getItemHandler().getStackInSlot(i).copy();
        }

        // Provjeri može li se potrošiti dovoljno itema
        for (Ingredient ingredient : recipe.getIngredients()) {
            int needed = 1; // Default: 1 po ingredientu

            if (!hasEnoughOfIngredient(checkStacks, ingredient, needed)) {
                return false;
            }

            // "Potroši" iz check kopije
            consumeFromCheckStacks(checkStacks, ingredient, needed);
        }

        return true;
    }

    private boolean hasEnoughOfIngredient(ItemStack[] checkStacks, Ingredient ingredient, int needed) {
        int found = 0;
        for (ItemStack stack : checkStacks) {
            if (!stack.isEmpty() && ingredient.test(stack)) {
                found += stack.getCount();
                if (found >= needed) return true;
            }
        }
        return false;
    }

    private void consumeFromCheckStacks(ItemStack[] checkStacks, Ingredient ingredient, int needed) {
        for (int i = 0; i < checkStacks.length && needed > 0; i++) {
            ItemStack stack = checkStacks[i];
            if (!stack.isEmpty() && ingredient.test(stack)) {
                int toConsume = Math.min(needed, stack.getCount());
                stack.shrink(toConsume);
                needed -= toConsume;
            }
        }
    }

    private boolean canInsertOutput(ItemStack result){
        ItemStack output = getItemHandler().getStackInSlot(OUTPUT_SLOT);
        return  output.isEmpty() ||
                (ItemStack.isSameItemSameComponents(output,result) &&
                        output.getCount() + result.getCount() <= output.getMaxStackSize());
    }
    private int getUpgradeCount(Item upgradeItem) {
        int count = 0;
        for (int slot : new int[]{UPGRADE_SLOT_1, UPGRADE_SLOT_2}) {
            ItemStack stack = getItemHandler().getStackInSlot(slot);
            if (!stack.isEmpty() && stack.getItem() == upgradeItem) {
                count += stack.getCount();
            }
        }
        return Math.min(count, 4);
    }
    @Override
    public Component getDisplayName() {
        return Component.translatable("block.techrebirth.alloy_smelter");
    }
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new AlloySmelterMenu(containerId, playerInventory, this, this.getContainerData());
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) {
            updateSound();
            return;
        }

        if (!recipeCacheValid) {
            getCurrentRecipe();
            setChanged();
        }

        boolean hasRecipe = hasRecipe();
        boolean canContinue = hasRecipe && canContinueProcessing();
        int energyCost = getEnergyCostPerTick();
        boolean hasEnergy = getEnergyStorage().getEnergyStored() >= energyCost;

        // The machine is working (making progress) if it has a recipe, can process (output space), and has energy
        boolean isWorking = canContinue && hasEnergy;

        // Toggle LIT state based on whether it's actually working
        if (state.getValue(AlloySmelterBlock.LIT) != isWorking) {
            level.setBlock(pos, state.setValue(AlloySmelterBlock.LIT, isWorking), 3);
        }

        if (hasRecipe) {
            // Initialize maxProgress for new crafting
            if (this.progress == 0) {
                Optional<RecipeHolder<AlloySmeltingRecipe>> recipeOpt = getCurrentRecipe();
                if (recipeOpt.isPresent()) {
                    AlloySmeltingRecipe recipe = recipeOpt.get().value();
                    int baseTime = recipe.getCookingTime();
                    float speedMultiplier = getTier().speedMultiplier;
                    this.maxProgress = (int) (baseTime / speedMultiplier);
                    setChanged();
                }
            }

            if (isWorking) {
                if (this.progress == 0) {
                    onProcessStart();
                }

                getEnergyStorage().extractEnergy(energyCost, false);
                increaseProgress();

                if (getProgress() >= getMaxProgress()) {
                    finishProcessing();
                    resetProgress();
                }
            }
            // If hasRecipe is true but !isWorking, progress is paused (neither increased nor reset)
        } else {
            // No recipe anymore, reset progress and ensure LIT is false
            if (this.progress > 0) {
                resetProgress();
            }
        }
    }
    // NOVA METODA - provjeri može li NASTAVITI procesuirati
    private boolean canContinueProcessing() {
        Optional<RecipeHolder<AlloySmeltingRecipe>> recipeOpt = getCurrentRecipe();
        if (recipeOpt.isEmpty()) return false;

        AlloySmeltingRecipe recipe = recipeOpt.get().value();
        ItemStack result = recipe.getResultItem(null);

        // Provjeri ima li još inputa i može li se output staviti
        return hasEnoughInputItems() && canInsertOutput(result);
    }
    protected void increaseProgress() {

        progress++;

        setChanged();
    }

    protected void resetProgress() {
        progress = 0;
        setChanged();
    }

    public void drops (){
        SimpleContainer inventory = new SimpleContainer(6);
        for (int i = 0; i < 6; i++) {
            inventory.setItem(i, getItemHandler().getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public boolean isActuallyProcessing(){
        return this.getBlockState().getValue(AlloySmelterBlock.LIT);
    }


    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
    }

}
