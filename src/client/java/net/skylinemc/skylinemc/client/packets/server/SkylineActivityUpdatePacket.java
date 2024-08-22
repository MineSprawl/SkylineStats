package net.skylinemc.skylinemc.client.packets.server;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.nio.charset.StandardCharsets;

public record SkylineActivityUpdatePacket(String message) implements CustomPayload {
    public static final Id<SkylineActivityUpdatePacket> ID = new Id<>(Identifier.of("skyline", "activity"));
    public static final PacketCodec<RegistryByteBuf, SkylineActivityUpdatePacket> CODEC = PacketCodec.tuple(new PacketCodec<>() {
        @Override
        public String decode(RegistryByteBuf buf) {
            int length = buf.readableBytes();
            byte[] bytes = new byte[length];
            buf.readBytes(bytes);
            return new String(bytes, StandardCharsets.UTF_8);
        }

        @Override
        public void encode(RegistryByteBuf buf, String value) {
            buf.writeString(value);
        }
    }, SkylineActivityUpdatePacket::message, SkylineActivityUpdatePacket::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
