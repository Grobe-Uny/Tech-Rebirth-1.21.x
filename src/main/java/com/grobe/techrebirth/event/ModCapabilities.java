package com.grobe.techrebirth.event;

import com.grobe.techrebirth.TechRebirth;
import com.grobe.techrebirth.block.ModBlockEntities;
import com.grobe.techrebirth.block.custom.entity.EnergyCableBlockEntity;
import com.grobe.techrebirth.block.custom.entity.ElectricFurnaceBlockEntity;
import com.grobe.techrebirth.block.custom.entity.ElectricCrusherBlockEntity;
import com.grobe.techrebirth.block.custom.entity.GeneratorBlockEntity;
import com.grobe.techrebirth.block.custom.entity.bank.EnergyBankBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.RangedWrapper;

@EventBusSubscriber(modid = TechRebirth.MODID)
public class ModCapabilities {


    public static final BlockCapability<ItemStackHandler, Direction> ELECTRIC_FURNACE_ITEM_HANDLER = BlockCapability.createSided
            (ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID,"electric_furnace_items"),
                    ItemStackHandler.class
            );
    public static final BlockCapability<EnergyStorage, Direction> ELECTRIC_FURNACE_ENERGY =
            BlockCapability.createSided(
                    ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "electric_furnace_energy"),
                    EnergyStorage.class
            );


    // Pozovi ovu metodu iz event bus handlera (RegisterCapabilitiesEvent)
    @SubscribeEvent
    public static void register(RegisterCapabilitiesEvent event) {
        BlockCapability<ItemStackHandler, Direction> ITEM_HANDLER =
                BlockCapability.createSided(
                        ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "electric_furnace_item_handler"),
                        ItemStackHandler.class
                );

        BlockCapability<EnergyStorage, Direction> ENERGY =
                BlockCapability.createSided(
                        ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "electric_furnace_energy"),
                        EnergyStorage.class
                );
        // registriraj provider za tvoj BlockEntityType
        event.registerBlockEntity(
                ITEM_HANDLER,
                ModBlockEntities.ELECTRIC_FURNACE.get(),
                (ElectricFurnaceBlockEntity be, Direction side) -> {
                    if (be == null) return null;
                    return be.getItemHandler();
                }
        );
        // Also expose item handler for the creative furnace (it uses the same BE base class)
        event.registerBlockEntity(
                ITEM_HANDLER,
                ModBlockEntities.CREATIVE_ELECTRIC_FURNACE.get(),
                (ElectricFurnaceBlockEntity be, Direction side) -> {
                    if (be == null) return null;
                    return be.getItemHandler();
                }
        );

        event.registerBlockEntity(
                ENERGY,
                ModBlockEntities.ELECTRIC_FURNACE.get(),
                (ElectricFurnaceBlockEntity be, Direction side) -> {
                    if (be == null) return null;
                    return be.getEnergyStorage();
                }
        );
        // Also expose energy for the creative furnace
        event.registerBlockEntity(
                ENERGY,
                ModBlockEntities.CREATIVE_ELECTRIC_FURNACE.get(),
                (ElectricFurnaceBlockEntity be, Direction side) -> {
                    if (be == null) return null;
                    return be.getEnergyStorage();
                }
        );

        // Electric Crusher capabilities
        event.registerBlockEntity(
                ITEM_HANDLER,
                ModBlockEntities.ELECTRIC_CRUSHER.get(),
                (ElectricCrusherBlockEntity be, Direction side) -> {
                    if (be == null) return null;
                    return be.getItemHandler();
                }
        );
        event.registerBlockEntity(
                ENERGY,
                ModBlockEntities.ELECTRIC_CRUSHER.get(),
                (ElectricCrusherBlockEntity be, Direction side) -> {
                    if (be == null) return null;
                    return be.getEnergyStorage();
                }
        );

        event.registerBlockEntity(
                ITEM_HANDLER,
                ModBlockEntities.GENERATOR.get(),
                (GeneratorBlockEntity be, Direction side) -> {
                    if (be == null) return null;
                    return be.getItemHandler();
                }
        );

        event.registerBlockEntity(
                ENERGY,
                ModBlockEntities.GENERATOR.get(),
                (GeneratorBlockEntity be, Direction side) -> {
                    if (be == null) return null;
                    return be.getEnergyStorage();
                }
        );

        event.registerBlockEntity(
                ENERGY,
                ModBlockEntities.CABLE.get(),
                (EnergyCableBlockEntity be, Direction side) -> {
                    if (be == null) return null;
                    return be.getEnergyStorage();
                }
        );

        event.registerBlockEntity(
                ENERGY,
                ModBlockEntities.ENERGY_BANK.get(),
                (EnergyBankBlockEntity be, Direction side) -> {
                    if (be == null) return null;
                    return be.getExposedEnergyStorage();
                }
        );
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.ELECTRIC_FURNACE.get(),
                (be, side) -> {
                    if (!(be instanceof ElectricFurnaceBlockEntity f)) return null;
                    var base = f.getItemHandler();
                    if (side == net.minecraft.core.Direction.DOWN) {
                        // expose output slot only, extractable
                        return new RangedWrapper(base, 1, 2) {
                            @Override public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) { return stack; }
                        };
                    } else {
                        // expose input slot only, insertable
                        return new RangedWrapper(base, 0, 1) {
                            @Override public ItemStack extractItem(int slot, int amount, boolean simulate) { return ItemStack.EMPTY; }
                        };
                    }
                });

        // Electric Furnace energy capability for other mods
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.ELECTRIC_FURNACE.get(),
                (be, side) -> {
                    if (!(be instanceof ElectricFurnaceBlockEntity f)) return null;
                    return f.getEnergyStorage();
                }
        );

        // Creative Electric Furnace
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.CREATIVE_ELECTRIC_FURNACE.get(),
                (be, side) -> {
                    if (!(be instanceof ElectricFurnaceBlockEntity f)) return null;
                    return f.getEnergyStorage();
                }
        );

        // Electric Crusher
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.ELECTRIC_CRUSHER.get(),
                (be, side) -> {
                    if (!(be instanceof ElectricCrusherBlockEntity c)) return null;
                    return c.getEnergyStorage();
                }
        );

        // Generator
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.GENERATOR.get(),
                (be, side) -> {
                    if (!(be instanceof GeneratorBlockEntity g)) return null;
                    return g.getEnergyStorage();
                }
        );

        // Cables (if they expose an internal buffer to pull/push)
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.CABLE.get(),
                (be, side) -> {
                    if (!(be instanceof EnergyCableBlockEntity cable)) return null;
                    return cable.getEnergyStorage();
                }
        );

        // Energy Bank (batteries)
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.ENERGY_BANK.get(),
                (be, side) -> {
                    if (!(be instanceof EnergyBankBlockEntity bank)) return null;
                    return bank.getExposedEnergyStorage(); // your wrapper that respects side I/O
                }
        );


    }



}
