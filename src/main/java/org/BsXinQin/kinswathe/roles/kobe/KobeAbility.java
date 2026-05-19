package org.BsXinQin.kinswathe.roles.kobe;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.PlayerMoodComponent;
import dev.doctor4t.wathe.cca.PlayerShopComponent;
import dev.doctor4t.wathe.game.GameFunctions;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import org.BsXinQin.kinswathe.KinsWatheConfig;
import org.BsXinQin.kinswathe.KinsWatheRoles;
import org.BsXinQin.kinswathe.component.AbilityPlayerComponent;
import org.BsXinQin.kinswathe.component.PlayerEffectComponent;
import org.BsXinQin.kinswathe.component.SpeedComponent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class KobeAbility {

    public static void register(@NotNull PlayerEntity player) {
        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(player.getWorld());
        AbilityPlayerComponent ability = AbilityPlayerComponent.KEY.get(player);
        PlayerShopComponent playerShop = PlayerShopComponent.KEY.get(player);

        if (gameWorld.isRole(player, KinsWatheRoles.KOBE) && GameFunctions.isPlayerAliveAndSurvival(player) && ability.cooldown <= 0) {
            if (playerShop.balance < KinsWatheConfig.HANDLER.instance().KobeAbilityPrice) return;

            // 扣除金币
            playerShop.balance -= KinsWatheConfig.HANDLER.instance().KobeAbilityPrice;
            playerShop.sync();

            // 检测范围内玩家（半径3格，同一楼层Y轴差≤1.5）
            double range = 3.0;
            Box area = player.getBoundingBox().expand(range);
            List<ServerPlayerEntity> nearby = player.getWorld().getEntitiesByClass(
                ServerPlayerEntity.class,
                area,
                target -> target != player
                        && GameFunctions.isPlayerAliveAndSurvival(target)
                        && Math.abs(target.getY() - player.getY()) <= 1.5
            );

            int hitCount = nearby.size();

            // 速度持续时间计算（根据命中人数）
            int baseDuration = KinsWatheConfig.HANDLER.instance().KobeSpeedBaseDuration;
            int extraPerHit = KinsWatheConfig.HANDLER.instance().KobeSpeedExtraPerHit;
            int maxDuration = KinsWatheConfig.HANDLER.instance().KobeSpeedMaxDuration;
            int durationSec = Math.min(baseDuration + hitCount * extraPerHit, maxDuration);
            int durationTicks = durationSec * 20;

            // 速度倍率（可配置，例如 1.5 倍）
            float multiplier = (float) KinsWatheConfig.HANDLER.instance().KobeSpeedMultiplier;
            SpeedComponent.KEY.get(player).setMultiplier(multiplier, durationTicks);

            // 金币奖励和情绪
            if (hitCount > 0) {
                int rewardPerPlayer = KinsWatheConfig.HANDLER.instance().KobeRewardPerPlayer;
                int totalReward = rewardPerPlayer * hitCount;
                playerShop.addToBalance(totalReward);
                playerShop.sync();
                player.sendMessage(Text.translatable("tip.kinswathe.kobe.reward", totalReward), true);

                PlayerMoodComponent mood = PlayerMoodComponent.KEY.get(player);
                if (mood != null) {
                    mood.setMood(mood.getMood() + 10 * hitCount);
                }

                // 眩晕效果
                int stunDuration = KinsWatheConfig.HANDLER.instance().KobeStunDuration * 20;
                for (ServerPlayerEntity target : nearby) {
                    PlayerEffectComponent.KEY.get(target).setStunTicks(stunDuration);
                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, stunDuration, 2, false, true, true));
                    target.sendMessage(Text.translatable("tip.kinswathe.kobe.stunned"), true);
                }
            }

            // 特效和音效
            if (player.getWorld() instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(ParticleTypes.SWEEP_ATTACK, player.getX(), player.getY() + 0.5, player.getZ(), 1, 0.5, 0.5, 0.5, 0);
            }
            player.playSoundToPlayer(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 1.0f, 1.2f);

            // 冷却
            ability.setAbilityCooldown(KinsWatheConfig.HANDLER.instance().KobeCooldown);
        }
    }
}
