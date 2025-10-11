package com.grobe.techrebirth.compat.jade;

import com.grobe.techrebirth.TechRebirth;
import com.grobe.techrebirth.block.custom.entity.ElectricFurnaceBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.config.IPluginConfig;

import java.util.Optional;

/**
 * Jade provider for the Electric Furnace block.
 * This class handles the tooltip display when hovering over an Electric Furnace block in-game.
 * 
 * Jade is a mod that displays information about blocks when you look at them (similar to WAILA/HWYLA).
 * This provider tells Jade what information to show for our Electric Furnace.
 * 
 * Implements:
 * - IBlockComponentProvider: Provides client-side tooltip rendering
 * - IServerDataProvider: Provides server-side data collection (sent to client for display)
 */
public enum ElectricFurnaceJadeProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE; // Using singleton pattern via enum

    // Unique identifier for this provider, used by Jade to register it
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "electric_furnace");

    /**
     * Returns the unique identifier for this provider.
     * Jade uses this to identify and manage this provider.
     */
    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    /**
     * CLIENT-SIDE method: Called to add lines to the tooltip that appears when hovering over the block.
     * This method reads the data sent from the server (via appendServerData) and formats it for display.
     * 
     * @param tooltip The tooltip to add lines to
     * @param accessor Provides access to block information (position, level, etc.)
     * @param config Plugin configuration (can be used for user settings)
     */
    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        // Get the data that was sent from the server
        CompoundTag tag = accessor.getServerData();
        if (tag == null) return;

        // Display energy information if available
        if (tag.contains("energy") && tag.contains("capacity")) {
            int energy = tag.getInt("energy");
            int capacity = tag.getInt("capacity");
            // Format with thousands separator for readability (e.g., "10,000 / 50,000 FE")
            tooltip.add(Component.literal(String.format("Energy: %,d / %,d FE", energy, capacity)));
        }

        // Display input item information if available
        if (tag.contains("inputId")) {
            String inputId = tag.getString("inputId");
            int inCount = tag.getInt("inputCount");
            if (!inputId.isEmpty()) {
                // Show item ID and count if more than 1
                tooltip.add(Component.literal("Input: " + inputId + (inCount > 1 ? " x" + inCount : "")));
            }
        }
        
        // Display output item information if available
        if (tag.contains("outputId")) {
            String outputId = tag.getString("outputId");
            int outCount = tag.getInt("outputCount");
            if (!outputId.isEmpty()) {
                // Show item ID and count if more than 1
                tooltip.add(Component.literal("Output: " + outputId + (outCount > 1 ? " x" + outCount : "")));
            }
        }
    }

    /**
     * SERVER-SIDE method: Collects data from the block entity and packages it to send to the client.
     * This runs on the server and the data is synced to the client for display in appendTooltip.
     * 
     * This separation (server collects, client displays) prevents cheating and ensures accurate data.
     * 
     * @param tag The NBT tag to write data into (will be sent to client)
     * @param accessor Provides access to block entity and world information
     */
    @Override
    public void appendServerData(CompoundTag tag, BlockAccessor accessor) {
        // Verify we're dealing with an Electric Furnace block entity
        if (!(accessor.getBlockEntity() instanceof ElectricFurnaceBlockEntity be)) return;

        // Collect energy information from the block entity
        int energy = be.getEnergyStorage().getEnergyStored();
        int capacity = be.getEnergyStorage().getMaxEnergyStored();
        tag.putInt("energy", energy);
        tag.putInt("capacity", capacity);

        // Collect input item information (slot 0 is the input slot)
        ItemStack input = be.getItemHandler().getStackInSlot(0);
        if (!input.isEmpty()) {
            // Get the item's registry ID to send to client
            Item inItem = input.getItem();
            ResourceLocation inId = BuiltInRegistries.ITEM.getKey(inItem);
            tag.putString("inputId", inId == null ? "" : inId.toString());
            tag.putInt("inputCount", input.getCount());

            // Predict what the output will be by looking up the smelting recipe
            Level level = accessor.getLevel();
            if (level != null) {
                // Create a recipe input with the input item
                net.minecraft.world.item.crafting.SingleRecipeInput single =
                        new net.minecraft.world.item.crafting.SingleRecipeInput(input.copyWithCount(1));
                
                // Look up the smelting recipe for this input
                level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, single, level).ifPresent(holder -> {
                    // Get the result of the recipe
                    ItemStack result = holder.value().getResultItem(null);
                    if (!result.isEmpty()) {
                        // Add the output item information to the tag
                        ResourceLocation outId = BuiltInRegistries.ITEM.getKey(result.getItem());
                        tag.putString("outputId", outId == null ? "" : outId.toString());
                        tag.putInt("outputCount", result.getCount());
                    }
                });
            }
        }
    }
}
