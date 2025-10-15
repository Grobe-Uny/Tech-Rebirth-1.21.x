package com.grobe.techrebirth.block.custom.entity;

import com.grobe.techrebirth.block.ModBlockEntities;
import com.grobe.techrebirth.event.ModCapabilities;
import com.grobe.techrebirth.recipe.GeneratorFuelRecipe;
import com.grobe.techrebirth.recipe.ModRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.Optional;

public class GeneratorBlockEntity extends BaseMachineBlockEntity implements MenuProvider {



    // Generation configuration and client mirrors
    private int genPerTick = 40;
    private int clientMaxEnergyMirror = 20000; // used client-side when syncing via ContainerData

    private int burnTime = 0;
    private int maxBurnTime = 0;

    protected final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> GeneratorBlockEntity.this.burnTime; // remaining burn time
                case 1 -> GeneratorBlockEntity.this.maxBurnTime; // max burn time
                case 2 -> GeneratorBlockEntity.this.getEnergyStorage().getEnergyStored(); // energy stored
                case 3 -> GeneratorBlockEntity.this.getEnergyStorage().getMaxEnergyStored(); // max energy
                case 4 -> GeneratorBlockEntity.this.genPerTick; // generation rate
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> GeneratorBlockEntity.this.burnTime = value;
                case 1 -> GeneratorBlockEntity.this.maxBurnTime = value;
                case 2 -> GeneratorBlockEntity.this.setEnergyStored(value);
                case 3 -> GeneratorBlockEntity.this.clientMaxEnergyMirror = value; // client-side mirror
                case 4 -> GeneratorBlockEntity.this.genPerTick = value;
            }
        }

        @Override
        public int getCount() {
            return 5;
        }
    };

    public GeneratorBlockEntity(BlockPos pPos, BlockState pState) {
        super(ModBlockEntities.GENERATOR.get(), pPos, pState, 1, 20000, 512, 512, 0, 4);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.techrebirth.generator");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new com.grobe.techrebirth.gui.generator.GeneratorMenu(containerId, playerInventory, this, this.data);
    }

    @Override
    protected boolean isItemValid(int slot, ItemStack stack) {
        if (slot != 0) return false; // single fuel slot
        if (stack.isEmpty() || this.level == null) return false;
        // Accept if custom generator fuel recipe exists or if vanilla burn time > 0
        SingleRecipeInput input = new SingleRecipeInput(stack);
        Optional<RecipeHolder<GeneratorFuelRecipe>> opt = this.level.getRecipeManager().getRecipeFor(ModRecipeTypes.GENERATOR_FUEL_TYPE.get(), input, this.level);
        if (opt.isPresent()) return true;
        int burn = stack.getBurnTime(RecipeType.SMELTING);
        return burn > 0;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, GeneratorBlockEntity be) {
        if (level.isClientSide()) return;

        // Burn fuel and generate energy only if there is room to store it
        boolean hasRoomForEnergy = be.getEnergyStorage().getEnergyStored() < be.getEnergyStorage().getMaxEnergyStored();
        if (be.burnTime > 0) {
            if (hasRoomForEnergy) {
                be.burnTime--;
                be.getEnergyStorage().receiveEnergy(be.genPerTick, false);
                if(be.burnTime == 0){
                    be.genPerTick = 40;
                    be.maxBurnTime = 0;
                    setChanged(level, pos, state);
                }
                setChanged(level, pos, state);
            }
            // If there is no room, do not decrement burnTime — pause burning until energy is spent
        } else {
            // Only start new fuel if there is room for energy
            if (hasRoomForEnergy) {
                ItemStack fuel = be.getItemHandler().getStackInSlot(0);
                if (!fuel.isEmpty()){
                    //1) Check if there is a recipe for the fuel
                    SingleRecipeInput input = new SingleRecipeInput(fuel);
                    Optional<RecipeHolder<GeneratorFuelRecipe>> opt =level.getRecipeManager().getRecipeFor(ModRecipeTypes.GENERATOR_FUEL_TYPE.get(),input, level);
                    if(opt.isPresent()){
                        GeneratorFuelRecipe recipe = opt.get().value();
                        be.getItemHandler().extractItem(0, 1, false);
                        be.burnTime = recipe.burnTime();
                        be.maxBurnTime = recipe.burnTime();
                        be.genPerTick = recipe.powerPerTick();
                        setChanged(level, pos, state);
                    }else {
                        //2) Fallback to vanilla smelting
                        int burn = fuel.getBurnTime(RecipeType.SMELTING);
                        if (burn > 0) {
                            be.getItemHandler().extractItem(0, 1, false);
                            be.burnTime = burn;
                            be.maxBurnTime = burn;
                            setChanged(level, pos, state);
                    }
                }

                }
            }
        }

        // Push energy to adjacent receivers
        if (be.getEnergyStorage().getEnergyStored() > 0) {
            for (Direction dir : Direction.values()) {
                if (be.getEnergyStorage().getEnergyStored() <= 0) break;
                BlockPos nPos = pos.relative(dir);
                BlockState nState = level.getBlockState(nPos);
                BlockEntity nBe = level.getBlockEntity(nPos);
                if (nBe == null) continue;
                EnergyStorage target = level.getCapability(ModCapabilities.ELECTRIC_FURNACE_ENERGY, nPos, nState, nBe, dir.getOpposite());
                if (target == null || !target.canReceive()) continue;
                int toSend = Math.min(256, be.getEnergyStorage().getEnergyStored());
                if (toSend <= 0) continue;
                int received = target.receiveEnergy(toSend, false);
                if (received > 0) be.getEnergyStorage().extractEnergy(received, false);
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        tag.put("inventory", getItemHandler().serializeNBT(provider));
        tag.putInt("burnTime", burnTime);
        tag.putInt("maxBurnTime", maxBurnTime);
        tag.putInt("energy", getEnergyStorage().getEnergyStored());
        tag.putInt("genPerTick", genPerTick);
        super.saveAdditional(tag, provider);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        getItemHandler().deserializeNBT(provider, tag.getCompound("inventory"));
        burnTime = tag.getInt("burnTime");
        maxBurnTime = tag.getInt("maxBurnTime");
        setEnergyStored(tag.getInt("energy"));
        genPerTick = tag.contains("genPerTick") ? tag.getInt("genPerTick") : 40;
        super.loadAdditional(tag, provider);
    }

    // --- Added getters for Jade tooltip and other integrations ---
    // Expose current generation rate in RF per tick
    public int getGenPerTick() { return genPerTick; }
    // Expose remaining burn time (ticks)
    public int getBurnTime() { return burnTime; }
    // Expose max burn time (ticks) of the current fuel
    public int getMaxBurnTime() { return maxBurnTime; }
    // Convenience: current fuel stack in the single fuel slot
    public ItemStack getFuelStack() { return getItemHandler().getStackInSlot(0); }
}