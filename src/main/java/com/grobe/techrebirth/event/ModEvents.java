package com.grobe.techrebirth.event;

import com.grobe.techrebirth.TechRebirth;
import com.grobe.techrebirth.item.ModItems;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

@EventBusSubscriber(modid = TechRebirth.MODID)
public class ModEvents {

    @SubscribeEvent
    public static void onEntityHurt(LivingDamageEvent.Pre event){
        if(event.getEntity() instanceof Player player){
            Entity attacker = event.getEntity().getLastHurtByMob();
            if(attacker != null){
                if(hasFullBlazingGoldArmor(player)){
                    attacker.setRemainingFireTicks(40);
                }
            }
        }
    }

    private static boolean hasFullBlazingGoldArmor(Player player){
        ItemStack boots = player.getInventory().getArmor(0);
        ItemStack leggings = player.getInventory().getArmor(1);
        ItemStack chestplate = player.getInventory().getArmor(2);
        ItemStack helmet = player.getInventory().getArmor(3);

        return !helmet.isEmpty() && helmet.getItem() == ModItems.BLAZING_GOLD_HELMET.get()
                && !chestplate.isEmpty() && chestplate.getItem() == ModItems.BLAZING_GOLD_CHESTPLATE.get()
                && !leggings.isEmpty() && leggings.getItem() == ModItems.BLAZING_GOLD_LEGGINGS.get()
                && !boots.isEmpty() && boots.getItem() == ModItems.BLAZING_GOLD_BOOTS.get();

    }

}
