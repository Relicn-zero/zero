package org.BsXinQin.kinswathe.mixin.roles.hacker;

import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.PlayerMoodComponent;
import dev.doctor4t.wathe.cca.PlayerShopComponent;
import net.minecraft.entity.player.PlayerEntity;
import org.BsXinQin.kinswathe.KinsWatheConfig;
import org.BsXinQin.kinswathe.KinsWatheRoles;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerMoodComponent.class)
public abstract class HackerGiveCoinsMixin {

    @Shadow public abstract float getMood();
    @Shadow @Final @NotNull private PlayerEntity player;

    @Inject(method = "setMood", at = @At("HEAD"))
    public void giveHackerCoins(float mood, CallbackInfo ci) {
        if (!KinsWatheConfig.HANDLER.instance().HackerHasShop) return;
        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(this.player.getWorld());
        Role role = gameWorld.getRole(this.player);
        if (role != null && mood > getMood()) {
            if (gameWorld.isRole(this.player, KinsWatheRoles.HACKER)) {
                PlayerShopComponent playerShop = PlayerShopComponent.KEY.get(this.player);
                playerShop.addToBalance(50);
            }
        }
    }
}