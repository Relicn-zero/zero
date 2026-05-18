package org.BsXinQin.kinswathe.roles.judgelord;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.PlayerShopComponent;
import dev.doctor4t.wathe.game.GameFunctions;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import org.BsXinQin.kinswathe.KinsWatheConfig;
import org.BsXinQin.kinswathe.KinsWatheRoles;
import org.BsXinQin.kinswathe.component.AbilityPlayerComponent;
import org.BsXinQin.kinswathe.component.JudgeLordComponent;
import org.BsXinQin.kinswathe.packet.roles.JudgeLordC2SPacket;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * 审判长能力核心逻辑
 */
public class JudgeLordAbility {

    public static void register(@NotNull JudgeLordC2SPacket payload, @NotNull PlayerEntity player) {
        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(player.getWorld());
        AbilityPlayerComponent ability = AbilityPlayerComponent.KEY.get(player);
        PlayerShopComponent playerShop = PlayerShopComponent.KEY.get(player);
        JudgeLordComponent lordData = JudgeLordComponent.KEY.get(player);

        // 检查角色、存活、冷却、剩余使用次数
        if (gameWorld.isRole(player, KinsWatheRoles.JUDGELORD) && GameFunctions.isPlayerAliveAndSurvival(player) && ability.cooldown <= 0) {
            // 剩余使用次数检查
            if (lordData.getRemainingUses() <= 0) {
                player.sendMessage(Text.translatable("tip.kinswathe.judgelord.no_uses_left").withColor(Color.RED.getRGB()), true);
                return;
            }
            if (playerShop.balance < KinsWatheConfig.HANDLER.instance().JudgeLordAbilityPrice) return;

            // 获取目标玩家
            ServerPlayerEntity target = player.getServer().getPlayerManager().getPlayer(payload.target());
            if (target == null || !GameFunctions.isPlayerAliveAndSurvival(target)) {
                player.sendMessage(Text.translatable("tip.kinswathe.judgelord.invalid_target").withColor(Color.RED.getRGB()), true);
                return;
            }
            if (target == player) {
                player.sendMessage(Text.translatable("tip.kinswathe.judgelord.cannot_judge_self").withColor(Color.RED.getRGB()), true);
                return;
            }

            // 扣除金币
            playerShop.balance -= KinsWatheConfig.HANDLER.instance().JudgeLordAbilityPrice;
            playerShop.sync();

            // 扣除使用次数
            lordData.decrementRemainingUses();
            lordData.sync();

            // 发光效果
            int glowDuration = KinsWatheConfig.HANDLER.instance().JudgeLordGlowDuration * 20;
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, glowDuration, 0, false, true, true));

            // 召唤视觉闪电
            LightningEntity lightning = new LightningEntity(net.minecraft.entity.EntityType.LIGHTNING_BOLT, target.getWorld());
            lightning.refreshPositionAfterTeleport(target.getPos());
            lightning.setCosmetic(true);
            target.getWorld().spawnEntity(lightning);

            // 播放音效
            player.playSoundToPlayer(SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT, SoundCategory.PLAYERS, 1.0f, 1.0f);

            // 设置冷却
            ability.setAbilityCooldown(KinsWatheConfig.HANDLER.instance().JudgeLordCooldown);

            // 启动监控任务：在发光期间监听目标是否杀人
            // 使用 JudgeLordComponent 来存储监控状态
            lordData.startMonitoring(target.getUuid(), glowDuration);
        }
    }
}
