package org.BsXinQin.kinswathe.client.mixin.host;

import dev.doctor4t.wathe.client.WatheClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import org.BsXinQin.kinswathe.component.PlayerEffectComponent;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = KeyBinding.class, priority = 5000)
public abstract class PlayerEffectLimitKyesMixin {

    @Unique
    private void stunEffectLimitKeys(@NotNull CallbackInfoReturnable<Boolean> cir) {
        if (MinecraftClient.getInstance().player == null) return;
        PlayerEffectComponent playerStun = PlayerEffectComponent.KEY.get(MinecraftClient.getInstance().player);
        if (WatheClient.isPlayerAliveAndInSurvival()) {
            KeyBinding key = (KeyBinding) (Object) this;
            boolean useKey = key.equals(MinecraftClient.getInstance().options.useKey);
            boolean jumpKey = key.equals(MinecraftClient.getInstance().options.jumpKey);
            boolean attackKey = key.equals(MinecraftClient.getInstance().options.attackKey);
            if (playerStun.stunTicks > 0 && (useKey || jumpKey || attackKey)) {
                cir.setReturnValue(false);
            }
        }
    }

    @Inject(method = "wasPressed", at = @At("RETURN"), cancellable = true)
    private void wasPressed(@NotNull CallbackInfoReturnable<Boolean> cir) {stunEffectLimitKeys(cir);}

    @Inject(method = "isPressed", at = @At("RETURN"), cancellable = true)
    private void isPressed(@NotNull CallbackInfoReturnable<Boolean> cir) {stunEffectLimitKeys(cir);}
}