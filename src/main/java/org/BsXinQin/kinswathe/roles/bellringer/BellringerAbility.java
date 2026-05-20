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
import org.BsXinQin.kinswathe.component.BellringerComponent;

public class BellringerAbility {

    public static void register(PlayerEntity player) {
        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(player.getWorld());
        AbilityPlayerComponent ability = AbilityPlayerComponent.KEY.get(player);
        PlayerShopComponent shop = PlayerShopComponent.KEY.get(player);
        BellringerComponent bellringerComp = BellringerComponent.KEY.get(player);

        if (!gameWorld.isRole(player, KinsWatheRoles.BELLRINGER)
                || !GameFunctions.isPlayerAliveAndSurvival(player)
                || ability.cooldown > 0) {
            return;
        }

        int price = KinsWatheConfig.HANDLER.instance().BellringerAbilityPrice;
        if (shop.balance < price) return;
        shop.balance -= price;
        shop.sync();

        GameTimeComponent time = GameTimeComponent.KEY.get(player.getWorld());
        time.setTime(Math.max(0, time.getTime() - 1200));

        if (bellringerComp != null) {
            int durationTicks = KinsWatheConfig.HANDLER.instance().BellringerSpeedDuration * 20;
            bellringerComp.setSpeedTicks(durationTicks);
        }

        player.playSoundToPlayer(SoundEvents.BLOCK_BELL_USE, SoundCategory.PLAYERS, 1.0f, 1.0f);

        // 确保配置中有 BellringerCooldown 字段
        ability.setAbilityCooldown(KinsWatheConfig.HANDLER.instance().BellringerCooldown);
    }
}
