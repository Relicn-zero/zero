package org.BsXinQin.kinswathe.roles.kobe;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.PlayerMoodComponent;
import dev.doctor4t.wathe.cca.PlayerShopComponent;
import dev.doctor4t.wathe.game.GameFunctions;
import net.minecraft.entity.attribute.EntityAttributes;
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

            // 硬编码速度效果：直接修改移动速度属性
            double originalSpeed = player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getBaseValue();
            double boostedSpeed = originalSpeed * 2.5; // 2.5倍速度，可调整
            player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(boostedSpeed);
            // 5秒后恢复
            player.getServer().execute(() -> {
                try { Thread.sleep(5000); } catch (InterruptedException ignored) {}
                player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(originalSpeed);
            });

            // 检测周围玩家（半径5格）给予奖励
            double range = 5.0;
            Box area = player.getBoundingBox().expand(range);
            List<ServerPlayerEntity> nearby = player.getWorld().getEntitiesByClass(
                ServerPlayerEntity.class,
                area,
                target -> target != player && GameFunctions.isPlayerAliveAndSurvival(target)
            );

            if (!nearby.isEmpty()) {
                int rewardPerPlayer = KinsWatheConfig.HANDLER.instance().KobeRewardPerPlayer;
                int totalReward = rewardPerPlayer * nearby.size();
                playerShop.addToBalance(totalReward);
                playerShop.sync();
                player.sendMessage(Text.translatable("tip.kinswathe.kobe.reward", totalReward), true);

                // 增加情绪
                PlayerMoodComponent mood = PlayerMoodComponent.KEY.get(player);
                if (mood != null) {
                    mood.setMood(mood.getMood() + 10 * nearby.size());
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
