package org.BsXinQin.kinswathe.packet.roles;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.BsXinQin.kinswathe.KinsWathe;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record ArbiterC2SPacket(UUID targetUuid) implements CustomPayload {
    public static final Identifier ID = Identifier.of(KinsWathe.MOD_ID, "arbiter");
    public static final Id<ArbiterC2SPacket> PACKET_ID = new Id<>(ID);

    public static final PacketCodec<PacketByteBuf, ArbiterC2SPacket> CODEC = PacketCodec.of(
        (value, buf) -> buf.writeUuid(value.targetUuid()),
        buf -> new ArbiterC2SPacket(buf.readUuid())
    );

    @Override
    @NotNull
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
