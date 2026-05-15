package org.BsXinQin.kinswathe.client.mixin.roles.kidnapper;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.client.WatheClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import org.BsXinQin.kinswathe.roles.kidnapper.KidnapperComponent;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = KeyBinding.class, priority = 5000)
public abstract class KidnapperKeyControlledMixin {

    @Unique
    private void controlledLockKeys(@NotNull CallbackInfoReturnable<Boolean> cir) {
        if (MinecraftClient.getInstance().player == null) return;
        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(MinecraftClient.getInstance().player.getWorld());
        KidnapperComponent playerControlled = KidnapperComponent.KEY.get(MinecraftClient.getInstance().player);
        if (WatheClient.isPlayerAliveAndInSurvival()) {
            KeyBinding key = (KeyBinding) (Object) this;
            boolean useKey = key.equals(MinecraftClient.getInstance().options.useKey);
            boolean attackKey = key.equals(MinecraftClient.getInstance().options.attackKey);
            if (playerControlled.controlTicks > 0 && (useKey || attackKey)) {
                cir.setReturnValue(false);
            }
        }
    }

    @Inject(method = "wasPressed", at = @At("RETURN"), cancellable = true)
    private void wasPressed(@NotNull CallbackInfoReturnable<Boolean> cir) {controlledLockKeys(cir);}

    @Inject(method = "isPressed", at = @At("RETURN"), cancellable = true)
    private void isPressed(@NotNull CallbackInfoReturnable<Boolean> cir) {controlledLockKeys(cir);}
}