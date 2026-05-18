package org.BsXinQin.kinswathe.roles.kobe;

import dev.doctor4t.wathe.cca.GameWorldComponent;
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
import net.minecraft.util.math.Vec3d;
import org.BsXinQin.kinswathe.KinsWatheConfig;
import org.BsXinQin.kinswathe.KinsWatheRoles;
import org.BsXinQin.kinswathe.component.AbilityPlayerComponent;
import org.BsXinQin.kinswathe.component.PlayerEffectComponent;
import org.jetbrains.annotations.NotNull;

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

            // 冲刺效果：短时间内给玩家速度 V 级和抗性提升（避免被击退）
            int durationTicks = 20; // 1 秒冲刺
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, durationTicks, 4, false, false, true));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, durationTicks, 2, false, false, true));

            // 播放冲刺音效和粒子
            player.playSoundToPlayer(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 1.0f, 1.2f);
            if (player.getWorld() instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(ParticleTypes.SWEEP_ATTACK, player.getX(), player.getY() + 0.5, player.getZ(), 1, 0.5, 0.5, 0.5, 0);
            }

            // 设置冷却
            ability.setAbilityCooldown(KinsWatheConfig.HANDLER.instance().KobeCooldown);

            // 延迟击退检测：在冲刺期间多次检测（通过计划任务）
            player.getServer().execute(() -> {
                for (int i = 0; i < durationTicks; i += 5) {
                    player.getServer().getScheduler().schedule(() -> {
                        if (player.isRemoved() || !player.isAlive()) return;
                        // 检测周围 3 格内的玩家
                        Box box = player.getBoundingBox().expand(2.0);
                        player.getWorld().getEntitiesByClass(ServerPlayerEntity.class, box, target ->
                            target != player && GameFunctions.isPlayerAliveAndSurvival(target)
                        ).forEach(target -> {
                            // 计算击退方向（垂直于玩家面向方向，向左或向右）
                            Vec3d playerDir = player.getRotationVector().normalize();
                            Vec3d toTarget = target.getPos().subtract(player.getPos()).normalize();
                            Vec3d knockbackDir = playerDir.crossProduct(new Vec3d(0, 1, 0)).normalize();
                            if (toTarget.dotProduct(knockbackDir) < 0) knockbackDir = knockbackDir.negate();
                            knockbackDir = knockbackDir.add(0, 0.2, 0).normalize();
                            target.setVelocity(knockbackDir.multiply(1.2));
                            target.velocityModified = true;

                            // 眩晕
                            int stunDuration = KinsWatheConfig.HANDLER.instance().KobeStunDuration * 20;
                            PlayerEffectComponent.KEY.get(target).setStunTicks(stunDuration);
                            target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, stunDuration, 2, false, true, true));

                            // 给科比玩家发送提示
                            player.sendMessage(Text.translatable("tip.kinswathe.kobe.knockback", target.getName().getString()), true);
                        });
                    }, new net.minecraft.util.Identifier("kinswathe", "kobe_dash_" + i), i);
                }
            });
        }
    }
}
