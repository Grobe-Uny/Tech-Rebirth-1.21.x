package com.grobe.techrebirth.sound;

import com.grobe.techrebirth.block.custom.entity.BaseMachineBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;

/**
 * Helper class for client-side sound logic to avoid NoClassDefFoundError on dedicated servers.
 */
public class ClientSoundHelper {
    public static void playMachineSound(BaseMachineBlockEntity entity, SoundEvent sound, float volume, float pitch) {
        Minecraft.getInstance().getSoundManager().play(
                new MachineSoundInstance(entity, sound, true, volume, pitch)
        );
    }
}
