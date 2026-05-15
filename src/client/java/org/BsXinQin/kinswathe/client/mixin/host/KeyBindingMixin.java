package org.BsXinQin.kinswathe.client.mixin.host;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.client.WatheClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import org.BsXinQin.kinswathe.component.ConfigWorldComponent;
import org.BsXinQin.kinswathe.component.PlayerEffectComponent;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = KeyBinding.class, priority = 5000)
public abstract class KeyBindingMixin {

    @Unique
    private void unlockKeys(@NotNull CallbackInfoReturnable<Boolean> cir) {
        if (MinecraftClient.getInstance().player == null) return;
        if (!ConfigWorldComponent.KEY.get(MinecraftClient.getInstance().player.getWorld()).EnableJumpNotInGame) return;
        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(MinecraftClient.getInstance().player.getWorld());
        PlayerEffectComponent playerStun = PlayerEffectComponent.KEY.get(MinecraftClient.getInstance().player);
        if (WatheClient.isPlayerAliveAndInSurvival()) {
            KeyBinding key = (KeyBinding) (Object) this;
            boolean jumpKey = key.equals(MinecraftClient.getInstance().options.jumpKey);
            if (!gameWorld.isRunning() && playerStun.stunTicks <= 0 && jumpKey) {
                cir.setReturnValue(keyPressed());
            }
        }
    }

    @Inject(method = "wasPressed", at = @At("RETURN"), cancellable = true)
    private void wasPressed(@NotNull CallbackInfoReturnable<Boolean> cir) {unlockKeys(cir);}

    @Inject(method = "isPressed", at = @At("RETURN"), cancellable = true)
    private void isPressed(@NotNull CallbackInfoReturnable<Boolean> cir) {unlockKeys(cir);}

    @Accessor("pressed")
    abstract boolean keyPressed();
}