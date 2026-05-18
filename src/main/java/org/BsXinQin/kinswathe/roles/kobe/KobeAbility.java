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

            // 检测周围玩家（范围可配置，默认3格）
            double range = 3.0;
            Box area = player.getBoundingBox().expand(range);
            List<ServerPlayerEntity> nearby = player.getWorld().getEntitiesByClass(
                ServerPlayerEntity.class,
                area,
                target -> target != player && GameFunctions.isPlayerAliveAndSurvival(target)
            );

            // 计算速度持续时间：基础 + 每个附近玩家增加额外时间，不超过最大上限
            int baseDuration = KinsWatheConfig.HANDLER.instance().KobeSpeedBaseDuration;
            int perPlayerBonus = KinsWatheConfig.HANDLER.instance().KobeSpeedPerPlayerBonus;
            int maxDuration = KinsWatheConfig.HANDLER.instance().KobeSpeedMaxDuration;
            int totalSeconds = Math.min(baseDuration + nearby.size() * perPlayerBonus, maxDuration);
            int durationTicks = totalSeconds * 20;

            // 添加速度效果
            int amplifier = KinsWatheConfig.HANDLER.instance().KobeSpeedAmplifier;
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, durationTicks, amplifier, false, false, true));

            // 给予金币奖励和情绪增加
            if (!nearby.isEmpty()) {
                int reward = 25 * nearby.size();  // 每个附近玩家奖励25金币
                playerShop.addToBalance(reward);
                playerShop.sync();
                player.sendMessage(Text.translatable("tip.kinswathe.kobe.reward", reward), true);

                // 增加情绪
                PlayerMoodComponent mood = PlayerMoodComponent.KEY.get(player);
                if (mood != null) {
                    mood.setMood(mood.getMood() + 10);
                }
            }

            // 粒子效果和音效
            if (player.getWorld() instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(ParticleTypes.SWEEP_ATTACK, player.getX(), player.getY() + 0.5, player.getZ(), 1, 0.5, 0.5, 0.5, 0);
            }
            player.playSoundToPlayer(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 1.0f, 1.2f);

            // 设置冷却
            ability.setAbilityCooldown(KinsWatheConfig.HANDLER.instance().KobeCooldown);
        }
    }
}
