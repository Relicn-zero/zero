package org.BsXinQin.kinswathe.client.mixin.roles.arbiter;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.client.gui.screen.ingame.LimitedInventoryScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.PlayerScreenHandler;
import org.BsXinQin.kinswathe.KinsWatheRoles;
import org.BsXinQin.kinswathe.client.roles.arbiter.ArbiterPlayerWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mixin(LimitedInventoryScreen.class)
public class ArbiterScreenMixin {

    @Shadow
    private ClientPlayerEntity player;

    @Inject(method = "init", at = @At("HEAD"))
    private void onInit(CallbackInfo ci) {
        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(player.getWorld());
        if (gameWorld.isRole(player, KinsWatheRoles.ARBITER)) {
            List<UUID> players = new ArrayList<>(MinecraftClient.getInstance().player.networkHandler.getPlayerUuids());
            players.removeIf(uuid -> uuid.equals(player.getUuid()));

            int apart = 36;
            int x = ((HandledScreen<?>) (Object) this).width / 2 - (players.size() * apart) / 2 + 9;
            int y = ((HandledScreen<?>) (Object) this).height / 2 + 80;

            for (int i = 0; i < players.size(); i++) {
                ArbiterPlayerWidget widget = new ArbiterPlayerWidget(
                        x + apart * i, y,
                        players.get(i),
                        MinecraftClient.getInstance().player.networkHandler.getPlayerListEntry(players.get(i))
                );
                ((HandledScreen<?>) (Object) this).this.addDrawableChild(widget);
            }
        }
    }
}
