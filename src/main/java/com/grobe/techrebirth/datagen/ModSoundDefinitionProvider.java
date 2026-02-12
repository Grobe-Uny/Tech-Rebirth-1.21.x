package com.grobe.techrebirth.datagen;

import com.grobe.techrebirth.TechRebirth;
import com.grobe.techrebirth.sound.ModSounds;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinition;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;

import java.util.function.Supplier;

public class ModSoundDefinitionProvider extends SoundDefinitionsProvider {
    public ModSoundDefinitionProvider(PackOutput output, ExistingFileHelper helper){
        super(output, TechRebirth.MODID, helper);
    }

    @Override
    public void registerSounds(){
        registerMachineSounds(ModSounds.CRUSHER_RUNNING, "blocks/machine/crusher_running", "subtitles.techrebirth.crusher_running");
        registerMachineSounds(ModSounds.ALLOY_SMELTING, "blocks/machine/alloy_smelting", "subtitles.techrebirth.alloy_smelting");
        registerMachineSounds(ModSounds.FURNACE_RUNNING, "blocks/machine/furnace_running", "subtitles.techrebirth.furnace_running");
    }


    public void registerMachineSounds(Supplier<SoundEvent> newSound, String path, String subtitle){
        add(newSound, SoundDefinition.definition()
                .with(sound(ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, path), SoundDefinition.SoundType.SOUND)
                        .pitch(1).attenuationDistance(16).stream(true).weight(2))
                .subtitle(subtitle));
    }

}
