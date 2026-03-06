package com.grobe.techrebirth.event;

import com.grobe.techrebirth.Config;
import com.grobe.techrebirth.TechRebirth;
import com.grobe.techrebirth.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.List;

@EventBusSubscriber(modid = TechRebirth.MODID)
public class ToolEvents {

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!Config.AUTO_SMELT_ENABLED.get()) return;

        Player player = event.getPlayer();
        if (player == null) return;

        Level level = (Level) event.getLevel();
        if (level.isClientSide()) return;

        ItemStack heldItem = player.getMainHandItem();
        if (!heldItem.is(ModItems.BLAZING_GOLD_PICKAXE.get())) return;

        BlockPos pos = event.getPos();
        BlockState state = event.getState();

        // Minecraft izračuna drops (sa fortuneom, silk touchom, modovima, itd.)
        List<ItemStack> originalDrops = Block.getDrops(state, (ServerLevel) level, pos, null, player, heldItem);

        // Provjeri ima li išta za smeltati
        boolean anythingToSmelt = false;
        for (ItemStack drop : originalDrops) {
            if (canSmelt(drop,level)) {
                anythingToSmelt = true;
                break;
            }
        }

        if (!anythingToSmelt) return;

        // Presreći event
        event.setCanceled(true);

        // Obradi sve drops
        for (ItemStack originalDrop : originalDrops) {
            ItemStack finalDrop = processDrop(originalDrop, level);

            if (!finalDrop.isEmpty()) {
                spawnDrop( level, pos, finalDrop);
            }
        }

        // Uništi blok i ošteti alat
        level.destroyBlock(pos, false);
        heldItem.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
    }

    private static boolean canSmelt(ItemStack stack, Level level) {
        if (stack.isEmpty()) return false;

        SingleRecipeInput input = new SingleRecipeInput(stack);
        return level.getRecipeManager()
                .getRecipeFor(RecipeType.SMELTING, input, level)
                .isPresent();
    }

    private static ItemStack processDrop(ItemStack original, Level level) {
        if (original.isEmpty()) return ItemStack.EMPTY;

        SingleRecipeInput input = new SingleRecipeInput(original);
        var recipe = level.getRecipeManager()
                            .getRecipeFor(RecipeType.SMELTING, input, level);

        if (recipe.isPresent()) {
            ItemStack smelted = recipe.get().value().getResultItem(level.registryAccess()).copy();
            smelted.setCount(original.getCount());
            return smelted;
        }

        // Ne može se smeltati, vrati original
        return original.copy();
    }

    private static void spawnDrop(Level level, BlockPos pos, ItemStack stack) {
        ItemEntity item = new ItemEntity(
                level,
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5,
                stack
        );
        item.setDefaultPickUpDelay();
        level.addFreshEntity(item);
    }
}