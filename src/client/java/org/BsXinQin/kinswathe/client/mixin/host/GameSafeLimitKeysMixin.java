package org.BsXinQin.kinswathe.client.mixin.host;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.client.WatheClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import org.BsXinQin.kinswathe.component.ConfigWorldComponent;
import org.BsXinQin.kinswathe.component.GameSafeComponent;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = KeyBinding.class, priority = 5000)
public abstract class GameSafeLimitKeysMixin {

    @Unique
    private void safeLockKeys(@NotNull CallbackInfoReturnable<Boolean> cir) {
        if (MinecraftClient.getInstance().player == null) return;
        if (!ConfigWorldComponent.KEY.get(MinecraftClient.getInstance().player.getWorld()).EnableStartSafeTime) return;
        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(MinecraftClient.getInstance().player.getWorld());
        GameSafeComponent gameSafe = GameSafeComponent.KEY.get(MinecraftClient.getInstance().player.getWorld());
        if (WatheClient.isPlayerAliveAndInSurvival()) {
            KeyBinding key = (KeyBinding) (Object) this;
            boolean attackKey = key.equals(MinecraftClient.getInstance().options.attackKey);
            if (gameWorld.isRunning() && gameSafe.isSafe() && attackKey) {
                cir.setReturnValue(false);
            }
        }
    }

    @Inject(method = "wasPressed", at = @At("RETURN"), cancellable = true)
    private void wasPressed(@NotNull CallbackInfoReturnable<Boolean> cir) {safeLockKeys(cir);}

    @Inject(method = "isPressed", at = @At("RETURN"), cancellable = true)
    private void isPressed(@NotNull CallbackInfoReturnable<Boolean> cir) {safeLockKeys(cir);}
}