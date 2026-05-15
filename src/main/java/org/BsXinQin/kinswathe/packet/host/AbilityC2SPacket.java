package org.BsXinQin.kinswathe.packet.roles;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.BsXinQin.kinswathe.KinsWathe;
import org.BsXinQin.kinswathe.roles.arbiter.ArbiterAbility;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record ArbiterC2SPacket(UUID targetUuid) implements CustomPayload {
    public static final Identifier ID = Identifier.of(KinsWathe.MOD_ID, "arbiter");
    public static final Id<ArbiterC2SPacket> PACKET_ID = new Id<>(ID);
    public static final PacketCodec<PacketByteBuf, ArbiterC2SPacket> CODEC = PacketCodec.tuple(
        PacketCodecs.UUID, ArbiterC2SPacket::targetUuid,
        ArbiterC2SPacket::new
    );

    @Override
    @NotNull
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

    public static class Receiver implements ServerPlayNetworking.PlayPayloadHandler<ArbiterC2SPacket> {
        @Override
        public void receive(@NotNull ArbiterC2SPacket payload, @NotNull ServerPlayNetworking.Context context) {
            ArbiterAbility.register(context.player(), payload.targetUuid());
        }
    }
}
