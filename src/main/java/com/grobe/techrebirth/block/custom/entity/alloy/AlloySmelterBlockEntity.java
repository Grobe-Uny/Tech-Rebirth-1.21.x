package com.grobe.techrebirth.block.custom.entity.alloy;

import com.grobe.techrebirth.block.ModBlockEntities;
import com.grobe.techrebirth.block.custom.entity.BaseMachineBlockEntity;
import com.grobe.techrebirth.gui.alloy_smelter.AlloySmelterMenu;
import com.grobe.techrebirth.item.ModItems;
import com.grobe.techrebirth.recipe.AlloySmeltingRecipe;
import com.grobe.techrebirth.recipe.ModRecipeTypes;
import com.grobe.techrebirth.recipe.MultiItemRecipeInput;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

public class AlloySmelterBlockEntity extends BaseMachineBlockEntity {
    private static final int INPUT_SLOT_1 = 0;
    private static final int INPUT_SLOT_2 = 1;
    private static final int INPUT_SLOT_3 = 2;
    private static final int OUTPUT_SLOT = 3;
    private static final int UPGRADE_SLOT_1 = 4;
    private static final int UPGRADE_SLOT_2 = 5;

    // Cache za performance
    private Optional<RecipeHolder<AlloySmeltingRecipe>> cachedRecipe = Optional.empty();
    private boolean recipeCacheValid = false;

    public AlloySmelterBlockEntity(BlockPos pos, BlockState state){
        this (ModBlockEntities.ALLOY_SMELTER.get(), pos, state);
    }
    protected AlloySmelterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state, 6, 50000, 1024, 1024, 0);
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

    protected boolean canProcess(){
        if(!hasRecipe()) return false;

        ItemStack result = getCurrentRecipe().get().value().getResultItem(null);

        int energyCost = getEnergyCostPerTick();

        return canInsertOutput(result) &&
                getEnergyStorage().getEnergyStored() >= energyCost;
    }

    protected void finishProcessing(){
        craftItem();
    }

    protected int getEnergyCostPerTick(){
        int speedUpgrades = getUpgradeCount(ModItems.SPEED_UPGRADE.get());
        int efficiencyUpgrades = getUpgradeCount(ModItems.EFFICIENCY_UPGRADE.get());

        float energyConsumptionMultiplier = (float)Math.pow(0.75, efficiencyUpgrades);
        float energySpeedPenalty = 1 + (0.25f * speedUpgrades);
        return (int) (160 * energySpeedPenalty * energyConsumptionMultiplier);
    }

    protected void onProcessStart(){
        //place for particles and sounds
    }


    protected void updateBlockState(Level level, BlockPos pos, BlockState state, boolean isActive){
        if(state.hasProperty(BlockStateProperties.LIT)){
            boolean wasLit = state.getValue(BlockStateProperties.LIT);
            if(wasLit != isActive){
                level.setBlock(pos, state.setValue(BlockStateProperties.LIT, isActive), 3);
            }
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
                    com.grobe.techrebirth.recipe.ModRecipeTypes.ALLOY_SMELTING_TYPE.get(),
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

        // CONSUME INPUTS BASED ON ACTUAL RECIPE - OVO JE KLJUČNO!
        consumeInputsBasedOnRecipe(recipe);

        // Produce output
        ItemStack output = getItemHandler().getStackInSlot(OUTPUT_SLOT);
        if (output.isEmpty()) {
            getItemHandler().setStackInSlot(OUTPUT_SLOT, result.copy());
        } else {
            output.grow(result.getCount());
        }

        recipeCacheValid = false;
    }
    private void consumeInputsBasedOnRecipe(AlloySmeltingRecipe recipe) {
        // Mapiraj koje ingredient-e trebamo potrošiti
        Map<Ingredient, Integer> ingredientsToConsume = new HashMap<>();
        for (Ingredient ingredient : recipe.getIngredients()) {
            ingredientsToConsume.put(ingredient, ingredientsToConsume.getOrDefault(ingredient, 0) + 1);
        }

        // Potroši iteme iz slotova
        for (int slot : new int[]{INPUT_SLOT_1, INPUT_SLOT_2, INPUT_SLOT_3}) {
            ItemStack stack = getItemHandler().getStackInSlot(slot);
            if (stack.isEmpty()) continue;

            // Pronađi ingredient koji odgovara ovom itemu i potroši ga
            for (Iterator<Map.Entry<Ingredient, Integer>> it = ingredientsToConsume.entrySet().iterator(); it.hasNext();) {
                Map.Entry<Ingredient, Integer> entry = it.next();
                Ingredient ingredient = entry.getKey();
                int countNeeded = entry.getValue();

                if (ingredient.test(stack)) {
                    int consumed = Math.min(countNeeded, stack.getCount());
                    stack.shrink(consumed);
                    getItemHandler().setStackInSlot(slot, stack);

                    countNeeded -= consumed;
                    if (countNeeded <= 0) {
                        it.remove(); // Svi ovi ingredienti su potrošeni
                    } else {
                        entry.setValue(countNeeded);
                    }
                    break;
                }
            }

            // Ako smo potrošili sve ingredient-e, prekini
            if (ingredientsToConsume.isEmpty()) {
                break;
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
        return new AlloySmelterMenu(containerId, playerInventory, this, this.data);
    }


    public void drops (){
        SimpleContainer inventory = new SimpleContainer(6);
        for (int i = 0; i < 6; i++) {
            inventory.setItem(i, getItemHandler().getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putFloat("alloy_smelter.progress", getProgress());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        // Progress se automatski loada iz base klase
    }

}
