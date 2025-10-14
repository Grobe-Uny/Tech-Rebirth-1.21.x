package com.grobe.techrebirth.block.custom.entity.alloy;

import com.grobe.techrebirth.block.custom.entity.BaseMachineBlockEntity;
import com.grobe.techrebirth.item.ModItems;
import com.grobe.techrebirth.recipe.AlloySmeltingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class AlloySmelterBlockEntity extends BaseMachineBlockEntity {
    private static final int INPUT_SLOT_1 = 0;
    private static final int INPUT_SLOT_2 = 1;
    private static final int INPUT_SLOT_3 = 2;
    private static final int OUTPUT_SLOT = 3;
    private static final int UPGRADE_SLOT_1 = 4;
    private static final int UPGRADE_SLOT_2 = 5;

    // Cache za performance
    private Optional<AlloySmeltingRecipe> cachedRecipe = Optional.empty();
    private boolean recipeCacheValid = false;

    protected AlloySmelterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int inventorySize, int energyCapacity, int maxReceive, int maxExtract, int initialEnergy) {
        super(type, pos, state, inventorySize, energyCapacity, maxReceive, maxExtract, initialEnergy);
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
    @Override
    protected boolean canProcess(){
        if(!hasRecipe()) return false;

        ItemStack result = getCurrentRecipe().get().getResultItem(null);
        int energyCost = getEnergyCostPerTick();

        return canInsertOutput(result) &&
                getEnergyStorage().getEnergyStored() >= energyCost;
    }
    @Override
    protected void finishProcessing(){
        craftItem();
    }
    @Override
    protected int getEnergyCostPerTick(){
        int speedUpgrades = getUpgradeCount(ModItems.SPEED_UPGRADE.get());
        int efficiencyUpgrades = getUpgradeCount(ModItems.EFFICIENCY_UPGRADE.get());

        float energyConsumptionMultiplier = (float)Math.pow(0.75, efficiencyUpgrades);
        float energySpeedPenalty = 1 + (0.25f * speedUpgrades);
        return (int) (160 * energySpeedPenalty * energyConsumptionMultiplier);
    }

    @Override
    protected void onProcessStart(){
        //place for particles and sounds
    }

    @Override
    protected void updateBlockState(Level level, BlockPos pos, BlockState state, boolean isActive){
        if(state.hasProperty(BlockStateProperties.LIT)){
            boolean wasLit = state.getValue(BlockStateProperties.LIT);
            if(wasLit != isActive){
                level.setBlock(pos, state.setValue(BlockStateProperties.LIT, isActive), 3);
            }
        }
    }
    @Override
    protected void onInventoryChanged(int slot) {
        // Invalidate cache kada se input promijeni
        if (slot == INPUT_SLOT_1 || slot == INPUT_SLOT_2 || slot == INPUT_SLOT_3) {
            recipeCacheValid = false;
        }
    }


    private boolean hasRecipe() {
        return getCurrentRecipe().isPresent();
    }
    private Optional<AlloySmeltingRecipe> getCurrentRecipe() {
        if (!recipeCacheValid) {
            SimpleContainer inventory = new SimpleContainer(3);
            inventory.setItem(0, getItemHandler().getStackInSlot(INPUT_SLOT_1));
            inventory.setItem(1, getItemHandler().getStackInSlot(INPUT_SLOT_2));
            inventory.setItem(2, getItemHandler().getStackInSlot(INPUT_SLOT_3));

            cachedRecipe = level.getRecipeManager().getRecipeFor(
                    com.grobe.techrebirth.recipe.ModRecipeTypes.ALLOY_SMELTING_TYPE.get(),
                    inventory, level
            );
            recipeCacheValid = true;
        }
        return cachedRecipe;
    }
    private void craftItem(){
        Optional<AlloySmeltingRecipe> recipeOpt = getCurrentRecipe();
        if(recipeOpt.isEmpty()) return;

        AlloySmeltingRecipe recipe = recipeOpt.get();
        ItemStack result = recipe.getResultItem(null);

        //consume inputs
        consumeInputs(recipe);

        //produce output
        ItemStack output = getItemHandler().getStackInSlot(OUTPUT_SLOT);
        if(output.isEmpty()){
            getItemHandler().setStackInSlot(OUTPUT_SLOT, result.copy());
        }else{
            output.grow(result.getCount());
        }

        //reset cache
        recipeCacheValid = false;
    }
    private void consumeInputs(AlloySmeltingRecipe recipe){
        // Ovo ćeš refine-ati kada implementiraš recipe pattern matching
        // Za sada, jednostavno consume-aj po jedan iz svakog input slota
        getItemHandler().extractItem(INPUT_SLOT_1, 1, false);
        getItemHandler().extractItem(INPUT_SLOT_2, 1, false);
        getItemHandler().extractItem(INPUT_SLOT_3, 1, false);
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
        return new AlloySmelterMenu(containerId, playerInventory, this, getContainerData());
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
