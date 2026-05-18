package org.BsXinQin.kinswathe.packet.roles;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.BsXinQin.kinswathe.KinsWathe;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record JudgeLordC2SPacket(UUID target) implements CustomPayload {
    public static final Identifier ID = Identifier.of(KinsWathe.MOD_ID, "judgelord");
    public static final Id<JudgeLordC2SPacket> PACKET_ID = new Id<>(ID);
    public static final PacketCodec<PacketByteBuf, JudgeLordC2SPacket> CODEC = PacketCodec.tuple(
        PacketCodecs.UUID, JudgeLordC2SPacket::target,
        JudgeLordC2SPacket::new
    );

    @Override
    @NotNull
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
