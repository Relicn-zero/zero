package org.BsXinQin.kinswathe.mixin.host;

import com.llamalad7.mixinextras.sugar.Local;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.entity.PlayerBodyEntity;
import dev.doctor4t.wathe.game.GameFunctions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.BsXinQin.kinswathe.component.BodyDeathReasonComponent;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameFunctions.class)
public abstract class AddDeathReasonMixin {

    @Inject(method = "killPlayer(Lnet/minecraft/entity/player/PlayerEntity;ZLnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Identifier;)V", at = @At(value = "INVOKE", target = "Ldev/doctor4t/wathe/entity/PlayerBodyEntity;setHeadYaw(F)V"))
    private static void addDeathReason(@NotNull PlayerEntity victim, boolean spawnBody, PlayerEntity killer, @NotNull Identifier identifier, CallbackInfo ci, @NotNull @Local(name = "body") PlayerBodyEntity playerBodyEntity) {
        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(victim.getWorld());
        if (gameWorld.getRole(victim) == null) return;
        (BodyDeathReasonComponent.KEY.get(playerBodyEntity)).deathReason = identifier;
    }
}