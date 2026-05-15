package org.BsXinQin.kinswathe;

import de.maxhenkel.voicechat.api.Group;
import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatConnection;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent;
import de.maxhenkel.voicechat.api.events.VoicechatServerStartedEvent;
import dev.doctor4t.wathe.compat.TrainVoicePlugin;
import net.minecraft.server.network.ServerPlayerEntity;
import org.BsXinQin.kinswathe.roles.kidnapper.KidnapperComponent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class KinsWatheVoiceChatPlugin implements VoicechatPlugin {

    public static final UUID KILLER_GROUP_ID = UUID.randomUUID();
    public static Group KILLER_GROUP;

    @Override public String getPluginId() {return KinsWathe.MOD_ID;}
    @Override public void initialize(@NotNull VoicechatApi api) {VoicechatPlugin.super.initialize(api);}

    @Override
    public void registerEvents(@NotNull EventRegistration registration) {
        registration.registerEvent(MicrophonePacketEvent.class, this::onMicrophonePacket);
        VoicechatPlugin.super.registerEvents(registration);
        registration.registerEvent(VoicechatServerStartedEvent.class, event -> {
            TrainVoicePlugin.SERVER_API = event.getVoicechat();
        });
    }

    public static void addKillerGroup(@NotNull UUID player) {
        if (TrainVoicePlugin.isVoiceChatMissing()) return;
        VoicechatConnection connection = TrainVoicePlugin.SERVER_API.getConnectionOf(player);
        if (connection != null) {
            if (KILLER_GROUP == null)
                KILLER_GROUP = TrainVoicePlugin.SERVER_API.groupBuilder().setHidden(true).setId(KILLER_GROUP_ID).setName("Killer Group").setPersistent(true).setType(Group.Type.OPEN).build();
            if (KILLER_GROUP != null) connection.setGroup(KILLER_GROUP);
        }
    }

    public void onMicrophonePacket(@NotNull MicrophonePacketEvent ci) {
        if (ci.getSenderConnection() == null) return;
        Object playerObject = ci.getSenderConnection().getPlayer().getPlayer();
        if (playerObject instanceof ServerPlayerEntity serverPlayer) {
            KidnapperComponent targetControlled = KidnapperComponent.KEY.get(serverPlayer);
            if (targetControlled.controlTicks > 0) {
                ci.cancel();
            }
        }
    }
}