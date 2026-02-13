package com.grobe.techrebirth.sound;

import com.grobe.techrebirth.block.custom.entity.BaseMachineBlockEntity;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class MachineSoundInstance extends AbstractTickableSoundInstance {
    private final BaseMachineBlockEntity blockEntity;

    public MachineSoundInstance(BaseMachineBlockEntity blockEntity, SoundEvent sound, boolean isLooping, float volume, float pitch){
        super(sound, SoundSource.BLOCKS, SoundInstance.createUnseededRandom());
        this.blockEntity = blockEntity;
        this.looping = isLooping;
        this.delay = 0;
        this.volume = volume;
        this.pitch = pitch;
        this.x = (float) blockEntity.getBlockPos().getX() + 0.5f;
        this.y = (float) blockEntity.getBlockPos().getY() + 0.5f;
        this.z = (float) blockEntity.getBlockPos().getZ() + 0.5f;
    }

    @Override
    public void tick() {
        // Zvuk se gasi ako: je stroj uništen, ili više ne radi (nema struje/recepta)
        if (this.blockEntity.isRemoved() || !this.blockEntity.isActuallyProcessing()) {
            this.stop();
        }
    }
}
