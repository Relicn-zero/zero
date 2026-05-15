package org.BsXinQin.kinswathe.packet.items;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.PlayerPoisonComponent;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.BsXinQin.kinswathe.KinsWathe;
import org.BsXinQin.kinswathe.KinsWatheRoles;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Random;

public record BlowgunC2SPacket(int target) implements CustomPayload {

    public static final Identifier BLOWGUN_PLAYLOAD_ID = Identifier.of(KinsWathe.MOD_ID, "blowgun");
    public static final Id<BlowgunC2SPacket> ID = new Id<>(BLOWGUN_PLAYLOAD_ID);
    public static final PacketCodec<PacketByteBuf, BlowgunC2SPacket> CODEC = PacketCodec.tuple(PacketCodecs.INTEGER, BlowgunC2SPacket::target, BlowgunC2SPacket::new);
    public @NotNull Id<? extends @NotNull CustomPayload> getId() {return ID;}

    private static final Random random = new Random();

    public static class Receiver implements ServerPlayNetworking.PlayPayloadHandler<BlowgunC2SPacket> {
        @Override
        public void receive(@NotNull BlowgunC2SPacket payload, ServerPlayNetworking.@NotNull Context context) {
            ServerPlayerEntity player = context.player();
            if (!(player.getServerWorld().getEntityById(payload.target()) instanceof @NotNull PlayerEntity target)) return;
            if (target.distanceTo(player) > 15.0F) return;
            GameWorldComponent gameWorld = GameWorldComponent.KEY.get(player.getWorld());
            PlayerPoisonComponent targetPoison = PlayerPoisonComponent.KEY.get(target);
            if (gameWorld.isRole(target, KinsWatheRoles.ROBOT)) {
                player.sendMessage(Text.translatable("tip.kinswathe.drugmaker.poison_failed").withColor(Color.RED.getRGB()), true);
                return;
            }
            if (targetPoison.poisonTicks > 0) {
                int reduction = random.nextInt(200) + 100;
                int poisonTicks = Math.max(0, targetPoison.poisonTicks - reduction);
                targetPoison.setPoisonTicks(poisonTicks, player.getUuid());
            } else {
                int poisonTicks = PlayerPoisonComponent.clampTime.getLeft() + random.nextInt(PlayerPoisonComponent.clampTime.getRight() - PlayerPoisonComponent.clampTime.getLeft());
                targetPoison.setPoisonTicks(poisonTicks, player.getUuid());
            }
        }
    }
}