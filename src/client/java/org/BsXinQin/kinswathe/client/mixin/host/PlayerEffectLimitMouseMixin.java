package org.BsXinQin.kinswathe.client.mixin.host;

import dev.doctor4t.wathe.client.WatheClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.BsXinQin.kinswathe.component.PlayerEffectComponent;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class PlayerEffectLimitMouseMixin {

    @Inject(method = "updateMouse", at = @At("HEAD"), cancellable = true)
    private void stunEffectLimitMouse(@NotNull CallbackInfo ci) {
        if (MinecraftClient.getInstance().player == null) return;
        if (MinecraftClient.getInstance().currentScreen != null) return;
        PlayerEffectComponent playerStun = PlayerEffectComponent.KEY.get(MinecraftClient.getInstance().player);
        if (WatheClient.isPlayerAliveAndInSurvival() && playerStun.stunTicks > 0) {
            ci.cancel();
        }
    }
}