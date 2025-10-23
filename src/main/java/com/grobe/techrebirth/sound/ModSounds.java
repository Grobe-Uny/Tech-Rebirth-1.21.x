package com.grobe.techrebirth.sound;

import com.grobe.techrebirth.TechRebirth;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, TechRebirth.MODID);

    // Registriraj sve zvukove
    public static final Supplier<SoundEvent> CRUSHER_RUNNING = registerSoundEvent("crusher_running");


    private static Supplier<SoundEvent> registerSoundEvent(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(
                ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, name)
        ));
    }
}