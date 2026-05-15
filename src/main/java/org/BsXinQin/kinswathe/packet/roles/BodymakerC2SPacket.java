package org.BsXinQin.kinswathe.packet.roles;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.BsXinQin.kinswathe.KinsWathe;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record BodymakerC2SPacket(UUID target, String deathReason, String role) implements CustomPayload {

    public static final Identifier BODYMAKER_PLAYLOAD_ID = Identifier.of(KinsWathe.MOD_ID, "bodymaker");
    public static final Id<BodymakerC2SPacket> ID = new Id<>(BODYMAKER_PLAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, BodymakerC2SPacket> CODEC;
    public BodymakerC2SPacket(@NotNull UUID target, @NotNull String deathReason, @NotNull String role) {this.target = target;this.deathReason = deathReason;this.role = role;}
    public UUID target() {return this.target;}
    public String deathReason() {return this.deathReason;}
    public String role() {return this.role;}
    static {CODEC = PacketCodec.of(BodymakerC2SPacket::write, BodymakerC2SPacket::read);}
    public void write(@NotNull PacketByteBuf buf) {buf.writeUuid(this.target);buf.writeString(this.deathReason);buf.writeString(this.role);}
    public static BodymakerC2SPacket read(@NotNull PacketByteBuf buf) {return new BodymakerC2SPacket(buf.readUuid(), buf.readString(), buf.readString());}
    public @NotNull Id<? extends @NotNull CustomPayload> getId() {return ID;}
}