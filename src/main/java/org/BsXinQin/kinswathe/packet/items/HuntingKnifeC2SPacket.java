package org.BsXinQin.kinswathe.packet.items;

import dev.doctor4t.wathe.game.GameConstants;
import dev.doctor4t.wathe.game.GameFunctions;
import dev.doctor4t.wathe.index.WatheSounds;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.BsXinQin.kinswathe.KinsWathe;
import org.BsXinQin.kinswathe.KinsWatheItems;
import org.BsXinQin.kinswathe.roles.hunter.HunterComponent;
import org.jetbrains.annotations.NotNull;

public record HuntingKnifeC2SPacket(int target) implements CustomPayload {

    public static final Identifier HUNTING_KNIFE_PLAYLOAD_ID = Identifier.of(KinsWathe.MOD_ID, "hunting_knife");
    public static final Id<HuntingKnifeC2SPacket> ID = new Id<>(HUNTING_KNIFE_PLAYLOAD_ID);
    public static final PacketCodec<PacketByteBuf, HuntingKnifeC2SPacket> CODEC = PacketCodec.tuple(PacketCodecs.INTEGER, HuntingKnifeC2SPacket::target, HuntingKnifeC2SPacket::new);
    public @NotNull Id<? extends @NotNull CustomPayload> getId() {return ID;}

    public static class Receiver implements ServerPlayNetworking.PlayPayloadHandler<HuntingKnifeC2SPacket> {
        @Override
        public void receive(@NotNull HuntingKnifeC2SPacket payload, ServerPlayNetworking.@NotNull Context context) {
            ServerPlayerEntity player = context.player();
            if (!(player.getServerWorld().getEntityById(payload.target()) instanceof @NotNull PlayerEntity target)) return;
            if (target.distanceTo(player) > 3.0F) return;
            HunterComponent playerHunter = HunterComponent.KEY.get(player);
            playerHunter.reset();
            KinsWatheItems.setItemAfterUsing(player, KinsWatheItems.HUNTING_KNIFE, null);
            GameFunctions.killPlayer(target, true, player, GameConstants.DeathReasons.KNIFE);
            target.playSound(WatheSounds.ITEM_KNIFE_STAB, 1.0F, 1.0F);
            player.swingHand(Hand.MAIN_HAND);
        }
    }
}