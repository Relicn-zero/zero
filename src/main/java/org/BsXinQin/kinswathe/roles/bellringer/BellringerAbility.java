package org.BsXinQin.kinswathe.roles.bellringer;

import dev.doctor4t.wathe.cca.GameTimeComponent;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.PlayerShopComponent;
import dev.doctor4t.wathe.game.GameFunctions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import org.BsXinQin.kinswathe.KinsWatheConfig;
import org.BsXinQin.kinswathe.KinsWatheRoles;
import org.BsXinQin.kinswathe.component.AbilityPlayerComponent;
import org.BsXinQin.kinswathe.component.SpeedComponent;
import org.jetbrains.annotations.NotNull;

public class BellringerAbility {

    public static void register(@NotNull PlayerEntity player) {
        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(player.getWorld());
        AbilityPlayerComponent ability = AbilityPlayerComponent.KEY.get(player);
        GameTimeComponent time = GameTimeComponent.KEY.get(player.getWorld());
        PlayerShopComponent playerShop = PlayerShopComponent.KEY.get(player);

        if (gameWorld.isRole(player, KinsWatheRoles.BELLRINGER) && GameFunctions.isPlayerAliveAndSurvival(player) && ability.cooldown <= 0) {
            if (playerShop.balance < KinsWatheConfig.HANDLER.instance().BellringerAbilityPrice) return;

            // 扣除金币
            playerShop.balance -= KinsWatheConfig.HANDLER.instance().BellringerAbilityPrice;
            playerShop.sync();

            // 减少游戏时间 1 分钟（1200 tick）
            time.setTime(Math.max(0, time.getTime() - 1200));

            // 播放音效
            player.playSoundToPlayer(SoundEvents.BLOCK_BELL_USE, SoundCategory.PLAYERS, 1.0f, 1.0f);

            // 临时速度加成
            int durationSec = KinsWatheConfig.HANDLER.instance().BellringerSpeedDuration;
            float multiplier = (float) KinsWatheConfig.HANDLER.instance().BellringerSpeedMultiplier;
            int durationTicks = durationSec * 20;
            SpeedComponent.KEY.get(player).setTemporaryMultiplier(multiplier, durationTicks);

            // 设置技能冷却
            ability.setAbilityCooldown(KinsWatheConfig.HANDLER.instance().BellringerAbilityCooldown);
        }
    }
}
