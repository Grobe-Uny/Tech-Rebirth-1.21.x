package com.grobe.techrebirth.network;

import com.grobe.techrebirth.TechRebirth;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record EnergyDataPayload(long stored, long max, long gen, long spend) implements CustomPacketPayload {
    public static final Type<EnergyDataPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "energy_data"));

    public static final StreamCodec<ByteBuf, EnergyDataPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_LONG, EnergyDataPayload::stored,
            ByteBufCodecs.VAR_LONG, EnergyDataPayload::max,
            ByteBufCodecs.VAR_LONG, EnergyDataPayload::gen,
            ByteBufCodecs.VAR_LONG, EnergyDataPayload::spend,
            EnergyDataPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}