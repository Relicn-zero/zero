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
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.BsXinQin.kinswathe.KinsWatheConfig;
import org.BsXinQin.kinswathe.KinsWatheRoles;
import org.BsXinQin.kinswathe.component.AbilityPlayerComponent;
import org.BsXinQin.kinswathe.component.PlayerEffectComponent;
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

            // 冲刺距离
            double range = KinsWatheConfig.HANDLER.instance().KobeDashDistance;
            Vec3d start = player.getPos();
            Vec3d direction = player.getRotationVector().normalize();
            Vec3d end = start.add(direction.multiply(range));

            // 检测冲刺路径上的玩家
            Box dashBox = new Box(start, end).expand(1.0);
            List<ServerPlayerEntity> targets = player.getWorld().getEntitiesByClass(
                ServerPlayerEntity.class,
                dashBox,
                target -> target != player && GameFunctions.isPlayerAliveAndSurvival(target)
            );

            // 击退并眩晕
            int stunDuration = KinsWatheConfig.HANDLER.instance().KobeStunDuration * 20;
            for (ServerPlayerEntity target : targets) {
                // 击退方向（垂直于玩家朝向，向左或向右）
                Vec3d knockbackDir = direction.crossProduct(new Vec3d(0, 1, 0)).normalize();
                Vec3d toTarget = target.getPos().subtract(player.getPos()).normalize();
                if (toTarget.dotProduct(knockbackDir) < 0) knockbackDir = knockbackDir.negate();
                knockbackDir = knockbackDir.add(0, 0.2, 0).normalize();
                target.setVelocity(knockbackDir.multiply(1.2));
                target.velocityModified = true;

                PlayerEffectComponent.KEY.get(target).setStunTicks(stunDuration);
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, stunDuration, 2, false, true, true));
            }

            // 加速效果（模拟冲刺）
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 10, 4, false, false, true));
            player.playSoundToPlayer(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 1.0f, 1.2f);
            if (player.getWorld() instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(ParticleTypes.SWEEP_ATTACK, player.getX(), player.getY() + 0.5, player.getZ(), 1, 0.5, 0.5, 0.5, 0);
            }

            // 设置冷却
            ability.setAbilityCooldown(KinsWatheConfig.HANDLER.instance().KobeCooldown);
        }
    }
}
