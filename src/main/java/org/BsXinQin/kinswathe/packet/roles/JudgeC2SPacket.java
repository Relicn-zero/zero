package org.BsXinQin.kinswathe.packet.roles;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.BsXinQin.kinswathe.KinsWathe;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record JudgeC2SPacket(UUID target) implements CustomPayload {

    public static final Identifier JUDGE_PLAYLOAD_ID = Identifier.of(KinsWathe.MOD_ID, "judge");
    public static final Id<JudgeC2SPacket> ID = new Id<>(JUDGE_PLAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, JudgeC2SPacket> CODEC;
    public JudgeC2SPacket(@NotNull UUID target) {this.target = target;}
    public UUID target() {return this.target;}
    static {CODEC = PacketCodec.of(JudgeC2SPacket::write, JudgeC2SPacket::read);}
    public void write(@NotNull PacketByteBuf buf) {buf.writeUuid(this.target);}
    public static JudgeC2SPacket read(@NotNull PacketByteBuf buf) {return new JudgeC2SPacket(buf.readUuid());}
    public @NotNull Id<? extends @NotNull CustomPayload> getId() {return ID;}
}