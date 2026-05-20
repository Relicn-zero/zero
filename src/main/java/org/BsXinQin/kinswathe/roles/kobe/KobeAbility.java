package org.BsXinQin.kinswathe.roles.kobe;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.PlayerMoodComponent;
import dev.doctor4t.wathe.cca.PlayerShopComponent;
import dev.doctor4t.wathe.game.GameFunctions;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import org.BsXinQin.kinswathe.KinsWatheConfig;
import org.BsXinQin.kinswathe.KinsWatheRoles;
import org.BsXinQin.kinswathe.component.AbilityPlayerComponent;
import org.BsXinQin.kinswathe.component.KobeComponent;

import java.util.List;

public class KobeAbility {

    public static void register(PlayerEntity player) {
        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(player.getWorld());
        AbilityPlayerComponent ability = AbilityPlayerComponent.KEY.get(player);
        PlayerShopComponent shop = PlayerShopComponent.KEY.get(player);
        KobeComponent kobeComp = KobeComponent.KEY.get(player);

        if (!gameWorld.isRole(player, KinsWatheRoles.KOBE)
                || !GameFunctions.isPlayerAliveAndSurvival(player)
                || ability.cooldown > 0) {
            return;
        }

        int price = KinsWatheConfig.HANDLER.instance().KobeAbilityPrice;
        if (shop.balance < price) return;
        shop.balance -= price;
        shop.sync();

        double range = 3.0;
        Box area = player.getBoundingBox().expand(range);
        List<ServerPlayerEntity> hitPlayers = player.getWorld().getEntitiesByClass(
                ServerPlayerEntity.class, area,
                target -> target != player && GameFunctions.isPlayerAliveAndSurvival(target)
        );

        int hitCount = hitPlayers.size();
        int reward = KinsWatheConfig.HANDLER.instance().KobeRewardPerPlayer * hitCount;
        shop.addToBalance(reward);
        shop.sync();

        // 修复心情值增加
        PlayerMoodComponent mood = PlayerMoodComponent.KEY.get(player);
        mood.setMood(mood.getMood() + 10 * hitCount);
        mood.sync();

        for (ServerPlayerEntity target : hitPlayers) {
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 2));
            target.sendMessage(Text.literal("你被科比扣晕了！"), true);
        }

        if (kobeComp != null) {
            int durationTicks = KinsWatheConfig.HANDLER.instance().KobeSpeedDuration * 20;
            kobeComp.setSpeedTicks(durationTicks);
        }

        if (player.getWorld() instanceof ServerWorld world) {
            world.spawnParticles(net.minecraft.particle.ParticleTypes.SWEEP_ATTACK,
                    player.getX(), player.getY() + 0.5, player.getZ(),
                    20, 0.5, 0.5, 0.5, 0);
        }
        player.playSoundToPlayer(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 1.0f, 1.2f);

        ability.setAbilityCooldown(KinsWatheConfig.HANDLER.instance().KobeCooldown);
    }
}
