package com.grobe.techrebirth.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import javax.annotation.Nullable;
import java.util.Optional;

// Data klasa za spremanje machine podataka
public record MachineItemData(int storedEnergy, int progress, int maxProgress, @Nullable CompoundTag extraData) {
    public static final Codec<MachineItemData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("storedEnergy").forGetter(MachineItemData::storedEnergy),
                    Codec.INT.fieldOf("progress").forGetter(MachineItemData::progress),
                    Codec.INT.fieldOf("maxProgress").forGetter(MachineItemData::maxProgress),
                    CompoundTag.CODEC.optionalFieldOf("extraData").xmap(
                            opt -> opt.orElse(null),
                            data -> data == null ? Optional.empty() : Optional.of(data)
                    ).forGetter(MachineItemData::extraData)
            ).apply(instance, MachineItemData::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, MachineItemData> STREAM_CODEC = StreamCodec.of(
            (buf, data) -> {
                buf.writeInt(data.storedEnergy);
                buf.writeInt(data.progress);
                buf.writeInt(data.maxProgress);
                buf.writeBoolean(data.extraData != null);
                if (data.extraData != null) {
                    buf.writeNbt(data.extraData);
                }
            },
            buf -> {
                int energy = buf.readInt();
                int progress = buf.readInt();
                int maxProgress = buf.readInt();
                boolean hasExtra = buf.readBoolean();
                CompoundTag extra = hasExtra ? buf.readNbt() : null;
                return new MachineItemData(energy, progress, maxProgress, extra);
            }
    );
}
