package org.BsXinQin.kinswathe.roles.bellringer;

import dev.doctor4t.wathe.cca.GameTimeComponent;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.PlayerShopComponent;
import dev.doctor4t.wathe.game.GameFunctions;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import org.BsXinQin.kinswathe.KinsWatheConfig;
import org.BsXinQin.kinswathe.KinsWatheRoles;
import org.BsXinQin.kinswathe.component.AbilityPlayerComponent;
import org.jetbrains.annotations.NotNull;

public class BellringerAbility {

    public static void register(@NotNull ServerPlayerEntity player) {
        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(player.getWorld());
        AbilityPlayerComponent ability = AbilityPlayerComponent.KEY.get(player);
        GameTimeComponent time = GameTimeComponent.KEY.get(player.getWorld());
        PlayerShopComponent playerShop = PlayerShopComponent.KEY.get(player);
        if (gameWorld.isRole(player, KinsWatheRoles.BELLRINGER) && GameFunctions.isPlayerAliveAndSurvival(player) && ability.cooldown <= 0) {
            if (playerShop.balance < KinsWatheConfig.HANDLER.instance().BellringerAbilityPrice) return;
            playerShop.balance -= KinsWatheConfig.HANDLER.instance().BellringerAbilityPrice;
            playerShop.sync();
            time.setTime(Math.max(0, time.getTime() - 1200));
            player.playSoundToPlayer(SoundEvents.BLOCK_BELL_USE, SoundCategory.PLAYERS, 1.0f, 1.0f);

            // 增强：给附近玩家速度II（可配置）
            if (KinsWatheConfig.HANDLER.instance().EnableBellringerSpeedBoost) {
                double range = 15.0;
                for (ServerPlayerEntity p : player.getServer().getPlayerManager().getPlayerList()) {
                    if (p.distanceTo(player) <= range && GameFunctions.isPlayerAliveAndSurvival(p)) {
                        p.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 300, 1, false, true, true));
                        p.sendMessage(Text.translatable("tip.kinswathe.bellringer.speed_boost"), true);
                    }
                }
            }

            ability.setAbilityCooldown(KinsWatheConfig.HANDLER.instance().BellringerAbilityCooldown);
        }
    }
}
