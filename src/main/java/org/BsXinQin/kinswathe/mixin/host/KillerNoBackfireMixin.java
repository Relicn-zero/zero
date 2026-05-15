package org.BsXinQin.kinswathe.mixin.host;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.game.GameFunctions;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import org.BsXinQin.kinswathe.KinsWatheConfig;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "dev.doctor4t.wathe.util.GunShootPayload$Receiver", priority = 500)
public class KillerNoBackfireMixin {

    @Inject(method = "lambda$receive$2", at = @At("HEAD"), remap = false, cancellable = true)
    private static void noKillerBackfire(ServerPlayNetworking.Context context, @NotNull ServerPlayerEntity player, Item revolver, @NotNull CallbackInfo ci) {
        if (!KinsWatheConfig.HANDLER.instance().EnableWatheModify || !KinsWatheConfig.HANDLER.instance().PreventKillerDropRevolver) return;
        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(player.getWorld());
        if (gameWorld.canUseKillerFeatures(player) && GameFunctions.isPlayerAliveAndSurvival(player)) {
            ci.cancel();
        }
    }
}