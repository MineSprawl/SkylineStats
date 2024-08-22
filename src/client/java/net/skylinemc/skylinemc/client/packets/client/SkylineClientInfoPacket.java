package net.skylinemc.skylinemc.client.packets.client;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SkylineClientInfoPacket(String version) implements CustomPayload {
    public static final Id<SkylineClientInfoPacket> ID = new Id<>(Identifier.of("skyline", "init"));
    public static final PacketCodec<RegistryByteBuf, SkylineClientInfoPacket> CODEC = PacketCodec.tuple(PacketCodecs.STRING, SkylineClientInfoPacket::version, SkylineClientInfoPacket::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
