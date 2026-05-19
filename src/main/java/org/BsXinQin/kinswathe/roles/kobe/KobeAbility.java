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
import org.BsXinQin.kinswathe.component.TempSpeedComponent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class KobeAbility {
    public static void register(@NotNull PlayerEntity player) {
        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(player.getWorld());
        AbilityPlayerComponent ability = AbilityPlayerComponent.KEY.get(player);
        PlayerShopComponent playerShop = PlayerShopComponent.KEY.get(player);

        // 角色、存活和冷却条件
        if (gameWorld.isRole(player, KinsWatheRoles.KOBE)
                && GameFunctions.isPlayerAliveAndSurvival(player)
                && ability.cooldown <= 0) {

            // 金币检查
            if (playerShop.balance < KinsWatheConfig.HANDLER.instance().KobeAbilityPrice) return;

            // 扣钱
            playerShop.balance -= KinsWatheConfig.HANDLER.instance().KobeAbilityPrice;
            playerShop.sync();

            // 技能逻辑开始
            double range = 3.0;
            Box area = player.getBoundingBox().expand(range);
            List<ServerPlayerEntity> nearby = player.getWorld().getEntitiesByClass(
                    ServerPlayerEntity.class,
                    area,
                    target -> target != player && GameFunctions.isPlayerAliveAndSurvival(target)
                            && Math.abs(target.getY() - player.getY()) <= 1.5
            );
            int hitCount = nearby.size();

            // 速度提升持续时间（秒）
            int baseDuration = KinsWatheConfig.HANDLER.instance().KobeSpeedBaseDuration;
            int extraPerHit = KinsWatheConfig.HANDLER.instance().KobeSpeedExtraPerHit;
            int maxDuration = KinsWatheConfig.HANDLER.instance().KobeSpeedMaxDuration;
            int durationSec = Math.min(baseDuration + hitCount * extraPerHit, maxDuration);
            TempSpeedComponent.KEY.get(player).activate(durationSec * 20);

            if (hitCount > 0) {
                // 金币奖励
                int rewardPerPlayer = KinsWatheConfig.HANDLER.instance().KobeRewardPerPlayer;
                int totalReward = rewardPerPlayer * hitCount;
                playerShop.addToBalance(totalReward);
                playerShop.sync();
                player.sendMessage(Text.translatable("tip.kinswathe.kobe.reward", totalReward), true);

                // 心情值增加
                PlayerMoodComponent mood = PlayerMoodComponent.KEY.get(player);
                if (mood != null) mood.setMood(mood.getMood() + 10 * hitCount);

                // 眩晕与减速
                int stunDuration = KinsWatheConfig.HANDLER.instance().KobeStunDuration * 20;
                for (ServerPlayerEntity target : nearby) {
                    PlayerEffectComponent.KEY.get(target).setStunTicks(stunDuration);
                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, stunDuration, 2, false, true, true));
                    target.sendMessage(Text.translatable("tip.kinswathe.kobe.stunned"), true);
                }
            }

            // 特效与音效
            if (player.getWorld() instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(ParticleTypes.SWEEP_ATTACK,
                        player.getX(), player.getY() + 0.5, player.getZ(),
                        1, 0.5, 0.5, 0.5, 0);
            }
            player.playSoundToPlayer(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 1.0f, 1.2f);

            // 设置冷却
            ability.setAbilityCooldown(KinsWatheConfig.HANDLER.instance().KobeCooldown);
        }
    }
}
