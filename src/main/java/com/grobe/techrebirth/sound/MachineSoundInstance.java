package com.grobe.techrebirth.sound;

import com.grobe.techrebirth.block.custom.entity.alloy.AlloySmelterBlockEntity;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class MachineSoundInstance extends AbstractTickableSoundInstance {
    private final AlloySmelterBlockEntity blockEntity;

    public MachineSoundInstance(AlloySmelterBlockEntity blockEntity, SoundEvent sound, boolean isLooping, float volume){
        super(sound, SoundSource.BLOCKS, SoundInstance.createUnseededRandom());
        this.blockEntity = blockEntity;
        this.looping = isLooping;
        this.delay = 0;
        this.volume = volume;
        this.x = blockEntity.getBlockPos().getX() + 0.5f;
        this.y = blockEntity.getBlockPos().getY() + 0.5f;
        this.z = blockEntity.getBlockPos().getZ() + 0.5f;
    }

    @Override
    public void tick() {
        // Zvuk se gasi ako: je stroj uništen, ili više ne radi (nema struje/recepta)
        if (this.blockEntity.isRemoved() || !this.blockEntity.isActuallyProcessing()) {
            this.stop();
        }
    }
}
