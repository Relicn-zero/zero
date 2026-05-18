package org.BsXinQin.kinswathe.roles.kobe;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.PlayerShopComponent;
import dev.doctor4t.wathe.game.GameFunctions;
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

/**
 * 科比的冲刺能力
 */
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

            // 执行冲刺
            double distance = KinsWatheConfig.HANDLER.instance().KobeDashDistance;
            Vec3d start = player.getPos();
            Vec3d direction = player.getRotationVector().normalize();
            Vec3d end = start.add(direction.multiply(distance));

            // 瞬移（模拟冲刺）
            player.teleport(end.x, end.y, end.z);
            // 播放冲刺音效
            player.playSoundToPlayer(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 1.0f, 1.2f);
            // 生成冲刺粒子效果
            if (player.getWorld() instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(P
