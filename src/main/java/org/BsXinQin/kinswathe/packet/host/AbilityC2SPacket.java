package org.BsXinQin.kinswathe.packet.host;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.BsXinQin.kinswathe.KinsWathe;

public record AbilityC2SPacket() implements CustomPayload {
    public static final Identifier ABILITY_PAYLOAD_ID = Identifier.of(KinsWathe.MOD_ID, "ability");
    public static final Id<AbilityC2SPacket> ID = new Id<>(ABILITY_PAYLOAD_ID);
    public static final PacketCodec<PacketByteBuf, AbilityC2SPacket> CODEC = PacketCodec.of(
        (value, buf) -> {},
        buf -> new AbilityC2SPacket()
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
