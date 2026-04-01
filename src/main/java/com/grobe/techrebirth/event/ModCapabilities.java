package com.grobe.techrebirth.event;

import com.grobe.techrebirth.TechRebirth;
import com.grobe.techrebirth.block.ModBlockEntities;
import com.grobe.techrebirth.block.custom.entity.BaseGeneratorBlockEntity;
import com.grobe.techrebirth.block.custom.entity.BaseMachineBlockEntity;
import com.grobe.techrebirth.block.custom.entity.EnergyCableBlockEntity;
import com.grobe.techrebirth.block.custom.entity.FluidTankBlockEntity;
import com.grobe.techrebirth.block.custom.entity.bank.EnergyBankBlockEntity;
import com.grobe.techrebirth.block.custom.entity.furnace.ElectricFurnaceBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.RangedWrapper;

import java.util.List;

@EventBusSubscriber(modid = TechRebirth.MODID)
public class ModCapabilities {


    // Prazna statička lista - popunit će se kasnije
    private static List<BlockEntityType<?>> ALL_MACHINES;

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        // Postavi listu nakon što su svi registri inicijalizirani
        ALL_MACHINES = List.of(
                ModBlockEntities.ELECTRIC_FURNACE.get(),
                ModBlockEntities.CREATIVE_ELECTRIC_FURNACE.get(),
                ModBlockEntities.ELECTRIC_CRUSHER.get(),
                ModBlockEntities.GENERATOR.get(),
                ModBlockEntities.ENERGY_BANK.get(),
                ModBlockEntities.ALLOY_SMELTER.get(),
                ModBlockEntities.HARDENED_ALLOY_SMELTER.get(),
                ModBlockEntities.ENERGY_CABLE.get(),
                ModBlockEntities.HARDENED_ELECTRIC_FURNACE.get(),
                ModBlockEntities.REINFORCED_ELECTRIC_FURNACE.get(),
                ModBlockEntities.ELECTRIC_CENTRIFUGE_BE.get(),
                ModBlockEntities.ELECTRIC_PURIFIER.get(),
                ModBlockEntities.FLUID_INFUSER.get(),
                ModBlockEntities.SOLAR_GENERATOR.get()
        );
    }

    @SubscribeEvent
    public static void register(RegisterCapabilitiesEvent event) {
        // Initialize list if null (safety check)
        if (ALL_MACHINES == null) {
             ALL_MACHINES = List.of(
                ModBlockEntities.ELECTRIC_FURNACE.get(),
                ModBlockEntities.CREATIVE_ELECTRIC_FURNACE.get(),
                ModBlockEntities.ELECTRIC_CRUSHER.get(),
                ModBlockEntities.GENERATOR.get(),
                ModBlockEntities.ALLOY_SMELTER.get(),
                ModBlockEntities.HARDENED_ALLOY_SMELTER.get(),
                ModBlockEntities.ENERGY_CABLE.get(),
                ModBlockEntities.HARDENED_ELECTRIC_FURNACE.get(),
                ModBlockEntities.REINFORCED_ELECTRIC_FURNACE.get(),
                ModBlockEntities.ELECTRIC_CENTRIFUGE_BE.get(),
                ModBlockEntities.ELECTRIC_PURIFIER.get(),
                ModBlockEntities.ENERGY_BANK.get(),
                ModBlockEntities.FLUID_INFUSER.get(),
                ModBlockEntities.SOLAR_GENERATOR.get()
            );
        }

        // REGISTRIRAJ SVE MAŠINE AUTOMATSKI
        for (BlockEntityType<?> machineType : ALL_MACHINES) {
            registerMachineCapabilities(event, machineType);
        }

        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                ModBlockEntities.FLUID_TANK.get(),
                (be, side) -> {
                    if (be instanceof FluidTankBlockEntity tank) {
                        return tank.getFluidHandler();
                    }
                    return null;
                }
        );
    }

    private static void registerMachineCapabilities(RegisterCapabilitiesEvent event, BlockEntityType<?> machineType) {
        // ENERGY CAPABILITY - za sve mašine
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                machineType,
                (be, side) -> {
                    if (be instanceof BaseMachineBlockEntity machine) {
                        return machine.getEnergyStorage();
                    }
                    if (be instanceof EnergyCableBlockEntity cable) {
                        return cable.getEnergyStorage();
                    }
                    if (be instanceof EnergyBankBlockEntity bank) {
                        return bank.getEnergyStorageForSide(side);
                    }
                    if(be instanceof BaseGeneratorBlockEntity generator)
                    {
                        return generator.getEnergyStorage();
                    }
                    return null;
                }
        );

        // ITEM HANDLER CAPABILITY - za sve mašine (osim onih koje nemaju iteme)
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                machineType,
                (be, side) -> {
                    if (be instanceof ElectricFurnaceBlockEntity furnace) {
                        return getElectricFurnaceItemHandler(furnace, side);
                    }
                    if (be instanceof BaseMachineBlockEntity machine) {
                        return machine.getSidedItemHandler(side);
                    }
                    return null;
                }
        );

        // FLUID HANDLER CAPABILITY - samo za mašine koje podržavaju fluide
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                machineType,
                (be, side) -> {
                    if (be instanceof BaseMachineBlockEntity machine) {
                        return machine.getFluidHandler();
                    }
                    return null;
                }
        );
    }

    private static IItemHandler getElectricFurnaceItemHandler(ElectricFurnaceBlockEntity furnace, Direction side) {
        ItemStackHandler baseHandler = furnace.getItemHandler();

        if (side == Direction.DOWN) {
            return new RangedWrapper(baseHandler, 1, 2) {
                @Override
                public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
                    return stack;
                }
            };
        } else {
            return new RangedWrapper(baseHandler, 0, 1) {
                @Override
                public ItemStack extractItem(int slot, int amount, boolean simulate) {
                    return ItemStack.EMPTY;
                }
            };
        }
    }
}
