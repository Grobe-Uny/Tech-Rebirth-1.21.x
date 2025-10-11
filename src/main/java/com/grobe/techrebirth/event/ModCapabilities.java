package com.grobe.techrebirth.event;

import com.grobe.techrebirth.TechRebirth;
import com.grobe.techrebirth.block.ModBlockEntities;
import com.grobe.techrebirth.block.custom.entity.ElectricFurnaceBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.items.ItemStackHandler;

@EventBusSubscriber(modid = TechRebirth.MODID, bus = EventBusSubscriber.Bus.MOD)
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
    }


}
