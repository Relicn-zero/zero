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
import org.BsXinQin.kinswathe.component.TempSpeedComponent;
import org.jetbrains.annotations.NotNull;

public class BellringerAbility {
    public static void register(@NotNull PlayerEntity player) {
        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(player.getWorld());
        AbilityPlayerComponent ability = AbilityPlayerComponent.KEY.get(player);
        GameTimeComponent time = GameTimeComponent.KEY.get(player.getWorld());
        PlayerShopComponent playerShop = PlayerShopComponent.KEY.get(player);

        if (gameWorld.isRole(player, KinsWatheRoles.BELLRINGER)
                && GameFunctions.isPlayerAliveAndSurvival(player)
                && ability.cooldown <= 0) {

            // 金币检查
            if (playerShop.balance < KinsWatheConfig.HANDLER.instance().BellringerAbilityPrice) return;

            // 扣钱
            playerShop.balance -= KinsWatheConfig.HANDLER.instance().BellringerAbilityPrice;
            playerShop.sync();

            // 核心效果：减少1分钟（1200刻）剩余时间
            time.setTime(Math.max(0, time.getTime() - 1200));

            // 音效
            player.playSoundToPlayer(SoundEvents.BLOCK_BELL_USE, SoundCategory.PLAYERS, 1.0f, 1.0f);

            // 原代码：TempSpeedComponent.KEY.get(player).activate(durationSec * 20);
// 新代码：统一使用临时速度组件

float multiplier = KinsWatheConfig.HANDLER.instance().BellringerSpeedMultiplier;
int durationTicks = KinsWatheConfig.HANDLER.instance().BellringerSpeedDuration * 20;
TempSpeedComponent.KEY.get(player).activate(durationTicks, multiplier);
            // 设置冷却
            ability.setAbilityCooldown(KinsWatheConfig.HANDLER.instance().BellringerAbilityCooldown);
        }
    }
}
