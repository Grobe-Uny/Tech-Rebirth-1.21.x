package com.grobe.techrebirth.block.custom.entity;

import com.grobe.techrebirth.block.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.extensions.IItemStackExtension;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GeneratorBlockEntity extends BlockEntity {

    // The item handler for the fuel slot
    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    // The energy storage for the generator
    private final EnergyStorage energyStorage = new EnergyStorage(20000, 256, 256, 0);

    // The current burn time of the fuel
    private int burnTime = 0;
    // The maximum burn time of the fuel
    private int maxBurnTime = 0;

    public GeneratorBlockEntity(BlockPos pPos, BlockState pState) {
        super(ModBlockEntities.GENERATOR.get(), pPos, pState);
    }

    // Tick method, called every tick
    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, GeneratorBlockEntity pBlockEntity) {
        if (pLevel.isClientSide()) {
            return;
        }

        // If there is burn time left, generate energy
        if (pBlockEntity.burnTime > 0) {
            pBlockEntity.burnTime--;
            pBlockEntity.energyStorage.receiveEnergy(40, false); // Generate 40 FE/t
            setChanged(pLevel, pPos, pState);
        } else {
            // If there is no burn time left, try to burn a new item
            ItemStack fuel = pBlockEntity.itemHandler.getStackInSlot(0);
            int burnTime = fuel.getBurnTime(RecipeType.SMELTING);
            if (burnTime > 0) {
                pBlockEntity.itemHandler.extractItem(0, 1, false);
                pBlockEntity.burnTime = burnTime;
                pBlockEntity.maxBurnTime = burnTime;
                setChanged(pLevel, pPos, pState);
            }
        }

        // Distribute energy to adjacent blocks
        for (Direction direction : Direction.values()) {
            BlockEntity adjacentBlockEntity = pLevel.getBlockEntity(pPos.relative(direction));
            if (adjacentBlockEntity != null) {
                adjacentBlockEntity.getCapability(Capabilities.ENERGY, direction.getOpposite()).ifPresent(energyStorage -> {
                    if (energyStorage.canReceive()) {
                        int energyToTransfer = Math.min(pBlockEntity.energyStorage.getEnergyStored(), 256);
                        int received = energyStorage.receiveEnergy(energyToTransfer, false);
                        pBlockEntity.energyStorage.extractEnergy(received, false);
                    }
                });
            }
        }
    }

    // Save data to NBT
    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("inventory", itemHandler.serializeNBT());
        pTag.putInt("burnTime", burnTime);
        pTag.putInt("maxBurnTime", maxBurnTime);
        pTag.putInt("energy", energyStorage.getEnergyStored());
        super.saveAdditional(pTag);
    }

    // Load data from NBT
    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemHandler.deserializeNBT(pTag.getCompound("inventory"));
        burnTime = pTag.getInt("burnTime");
        maxBurnTime = pTag.getInt("maxBurnTime");
        energyStorage.setEnergy(pTag.getInt("energy"));
    }

    // Expose capabilities
    @Override
    public <T> @NotNull T getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == Capabilities.ITEM_HANDLER) {
            return (T) itemHandler;
        }
        if (cap == Capabilities.ENERGY) {
            return (T) energyStorage;
        }
        return super.getCapability(cap, side);
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    public EnergyStorage getEnergyStorage() {
        return energyStorage;
    }
}