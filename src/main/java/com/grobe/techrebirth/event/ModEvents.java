package com.grobe.techrebirth.event;

import com.grobe.techrebirth.TechRebirth;
import com.grobe.techrebirth.item.ModArmorItems;
import com.grobe.techrebirth.item.ModItems;
import com.grobe.techrebirth.item.custom.util.EnergyInformatorItem;
import com.grobe.techrebirth.network.EnergyDataPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@EventBusSubscriber(modid = TechRebirth.MODID)
public class ModEvents {

    private static final Map<UUID, Map<BlockPos, Long>> HISTORY = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            // 1. Logika za traženje itema (Ruka ili Curios)
            ItemStack stack = player.getMainHandItem();

            if (!(stack.getItem() instanceof EnergyInformatorItem)) {
                var curioInventory = CuriosApi.getCuriosInventory(player);
                if(curioInventory.isPresent()){
                    var found = curioInventory.get().findFirstCurio(ModItems.ENERGY_INFORMATOR.get());
                    if (found.isPresent()){
                        stack = found.get().stack();
                    }
                }

            }

            // 2. Ako smo našli item, kreni s izračunom
            if (stack.getItem() instanceof EnergyInformatorItem informator) {
                List<BlockPos> positions = informator.getSavedPositions(stack);
                if (positions.isEmpty()) return;

                long totalStored = 0;
                long totalMax = 0;
                long totalGen = 0;
                long totalSpend = 0;

                Map<BlockPos, Long> playerHistory = HISTORY.computeIfAbsent(player.getUUID(), k -> new HashMap<>());

                for (BlockPos pos : positions) {
                    var energy = player.level().getCapability(Capabilities.EnergyStorage.BLOCK, pos, null);

                    if (energy != null) {
                        long current = energy.getEnergyStored();
                        long max = energy.getMaxEnergyStored();
                        long last = playerHistory.getOrDefault(pos, current);
                        long delta = current - last;

                        totalStored += current;
                        totalMax += max;

                        if (delta > 0) {
                            totalGen += delta;
                        } else if (delta < 0) {
                            totalSpend += Math.abs(delta);
                        }

                        playerHistory.put(pos, current);
                    }
                }

                // Slanje podataka klijentu
                PacketDistributor.sendToPlayer(player, new EnergyDataPayload(totalStored, totalMax, totalGen, totalSpend));
            } else {
                if(HISTORY.containsKey(player.getUUID())){
                    HISTORY.remove(player.getUUID());
                    PacketDistributor.sendToPlayer(player, new EnergyDataPayload(0,0,0,0));
                }
            }
        }
    }

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

        return !helmet.isEmpty() && helmet.getItem() == ModArmorItems.BLAZING_GOLD_HELMET.get()
                && !chestplate.isEmpty() && chestplate.getItem() == ModArmorItems.BLAZING_GOLD_CHESTPLATE.get()
                && !leggings.isEmpty() && leggings.getItem() == ModArmorItems.BLAZING_GOLD_LEGGINGS.get()
                && !boots.isEmpty() && boots.getItem() == ModArmorItems.BLAZING_GOLD_BOOTS.get();

    }

}
