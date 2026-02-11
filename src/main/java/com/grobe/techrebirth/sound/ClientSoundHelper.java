package com.grobe.techrebirth.sound;

import com.grobe.techrebirth.block.custom.entity.alloy.AlloySmelterBlockEntity;
import net.minecraft.client.Minecraft;

/**
 * Helper class for client-side sound logic to avoid NoClassDefFoundError on dedicated servers.
 */
public class ClientSoundHelper {
    public static void playAlloySmelterSound(AlloySmelterBlockEntity entity) {
        Minecraft.getInstance().getSoundManager().play(
                new MachineSoundInstance(entity, ModSounds.CRUSHER_RUNNING.get(), true, 2f)
        );
    }
}
