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

            // 执行冲刺位移
            double distance = KinsWatheConfig.HANDLER.instance().KobeDashDistance;
            Vec3d start = player.getPos();
            Vec3d direction = player.getRotationVector().normalize();
            Vec3d end = start.add(direction.multiply(distance));

            // 瞬移前检测沿途玩家
            Box dashBox = new Box(start, end).expand(1.0); // 扩大检测范围
            List<ServerPlayerEntity> targets = player.getWorld().getEntitiesByClass(
                ServerPlayerEntity.class,
                dashBox,
                target -> target != player && GameFunctions.isPlayerAliveAndSurvival(target)
            );

            // 执行瞬移
            player.teleport(end.x, end.y, end.z);

            // 击退并眩晕沿途玩家
            int stunDuration = KinsWatheConfig.HANDLER.instance().KobeStunDuration * 20; // 秒转 tick
            for (ServerPlayerEntity target : targets) {
                // 计算击退方向（从玩家位置指向目标，垂直方向保留）
                Vec3d knockbackDir = target.getPos().subtract(player.getPos()).normalize();
                knockbackDir = new Vec3d(knockbackDir.x, 0.2, knockbackDir.z).normalize();
                target.setVelocity(knockbackDir.multiply(1.5));
                target.velocityModified = true;
                // 眩晕效果
                PlayerEffectComponent.KEY.get(target).setStunTicks(stunDuration);
                // 额外添加缓慢效果增强眩晕感
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, stunDuration, 2, false, true, true));
            }

            // 特效和音效
            if (player.getWorld() instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(ParticleTypes.SWEEP_ATTACK, end.x, end.y + 0.5, end.z, 10, 0.5, 0.5, 0.5, 0.1);
            }
            player.playSoundToPlayer(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 1.0f, 1.2f);

            // 设置冷却
            ability.setAbilityCooldown(KinsWatheConfig.HANDLER.instance().KobeCooldown);
        }
    }
}
