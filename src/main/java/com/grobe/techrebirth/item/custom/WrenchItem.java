package com.grobe.techrebirth.item.custom;

import com.grobe.techrebirth.block.custom.BaseMachineBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class WrenchItem extends Item {
    public WrenchItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context){
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        Player player = context.getPlayer();
        ItemStack wrench = context.getItemInHand();

        if(state.getBlock() instanceof BaseMachineBlock machineBlock){
            // Only work if the player is sneaking to avoid conflict with opening the GUI
            if (player != null && player.isShiftKeyDown()) {
                if (!level.isClientSide()) {
                    return machineBlock.tryPickupWithWrench(state, level, pos, player, wrench);
                }
                return InteractionResult.sidedSuccess(level.isClientSide());
            }
        }

        return InteractionResult.PASS;
    }
}
