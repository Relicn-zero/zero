package org.BsXinQin.kinswathe.client.mixin.roles.bodymaker;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.client.WatheClient;
import dev.doctor4t.wathe.client.gui.screen.ingame.LimitedHandledScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import org.BsXinQin.kinswathe.KinsWatheRoles;
import org.BsXinQin.kinswathe.client.roles.bodymaker.BodymakerRoleWidget;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LimitedHandledScreen.class)
public class BodymakerDoNotCloseMixin {

    @WrapOperation(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;matchesKey(II)Z", ordinal = 0))
    boolean doNotCloseInventory(@NotNull KeyBinding instance, int keyCode, int scanCode, @NotNull Operation<Boolean> original) {
        if (BodymakerRoleWidget.stopClosing) return false;
        return original.call(instance,keyCode,scanCode);
    }

    @Inject(method = "close", at = @At("HEAD"))
    void setStopClosingToFalse(CallbackInfo ci) {
        BodymakerRoleWidget.stopClosing = false;
    }

    @Unique
    public void reset() {
        if (MinecraftClient.getInstance().player == null) return;
        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(MinecraftClient.getInstance().player.getWorld());
        if (BodymakerRoleWidget.stopClosing) {
            if (WatheClient.isPlayerSpectatingOrCreative() || !gameWorld.isRole(MinecraftClient.getInstance().player, KinsWatheRoles.BODYMAKER)) {
                BodymakerRoleWidget.stopClosing = false;
            }
        }
    }
}