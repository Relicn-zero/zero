package org.BsXinQin.kinswathe.mixin.roles.kidnapper;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.PlayerShopComponent;
import dev.doctor4t.wathe.game.GameFunctions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.BsXinQin.kinswathe.KinsWatheConfig;
import org.BsXinQin.kinswathe.KinsWatheRoles;
import org.BsXinQin.kinswathe.roles.kidnapper.KidnapperComponent;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameFunctions.class)
public class KidnapperMoneyIncreaseMixin {
    @Inject(method = "killPlayer(Lnet/minecraft/entity/player/PlayerEntity;ZLnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Identifier;)V", at = @At("HEAD"))
    private static void increaseKidnapperMoney(@NotNull PlayerEntity victim, boolean spawnBody, @NotNull PlayerEntity killer, Identifier identifier, CallbackInfo ci) {
        if (killer != null) {
            GameWorldComponent gameWorld = GameWorldComponent.KEY.get(victim.getWorld());
            KidnapperComponent playerControlled = KidnapperComponent.KEY.get(victim);
            if (gameWorld.isRole(killer, KinsWatheRoles.KIDNAPPER) && playerControlled.controlTicks > 0) {
                PlayerShopComponent playerShop = PlayerShopComponent.KEY.get(killer);
                playerShop.addToBalance(KinsWatheConfig.HANDLER.instance().KidnapperGetAdditionalCoins);
            }
        }
    }
}
