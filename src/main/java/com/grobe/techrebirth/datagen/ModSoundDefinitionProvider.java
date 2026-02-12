package com.grobe.techrebirth.datagen;

import com.grobe.techrebirth.TechRebirth;
import com.grobe.techrebirth.sound.ModSounds;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinition;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;

public class ModSoundDefinitionProvider extends SoundDefinitionsProvider {
    public ModSoundDefinitionProvider(PackOutput output, ExistingFileHelper helper){
        super(output, TechRebirth.MODID, helper);
    }

    @Override
    public void registerSounds(){
        add(ModSounds.CRUSHER_RUNNING, SoundDefinition.definition()
                .with(
                        sound(ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "blocks/machine/crusher_running", SoundDefinition.SoundType.SOUND)
                                .
        ))
                        .subtitle("subtitles.techrebirth.crusher_running")
        );
    }
}
