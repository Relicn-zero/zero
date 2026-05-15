package org.BsXinQin.kinswathe.mixin.host;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.PlayerShopComponent;
import dev.doctor4t.wathe.game.GameFunctions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.BsXinQin.kinswathe.KinsWatheConfig;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameFunctions.class)
public class MoneyIncreaseMixin {
    @Inject(method = "killPlayer(Lnet/minecraft/entity/player/PlayerEntity;ZLnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Identifier;)V", at = @At("HEAD"))
    private static void increaseMoney(@NotNull PlayerEntity victim, boolean spawnBody, @NotNull PlayerEntity killer, Identifier identifier, CallbackInfo ci) {
        if (!KinsWatheConfig.HANDLER.instance().EnableWatheModify) return;
        if (killer != null) {
            GameWorldComponent gameWorld = GameWorldComponent.KEY.get(victim.getWorld());
            if (!gameWorld.isInnocent(killer)) {
                PlayerShopComponent playerShop = PlayerShopComponent.KEY.get(killer);
                playerShop.addToBalance(KinsWatheConfig.HANDLER.instance().IncreaseMoneyWhenKill - 100);
            }
        }
    }
}
