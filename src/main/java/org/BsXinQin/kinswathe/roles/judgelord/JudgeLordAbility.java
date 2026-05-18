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

public class JudgeLordAbility {

    public static void register(@NotNull JudgeLordC2SPacket payload, @NotNull PlayerEntity player) {
        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(player.getWorld());
        AbilityPlayerComponent ability = AbilityPlayerComponent.KEY.get(player);
        PlayerShopComponent playerShop = PlayerShopComponent.KEY.get(player);
        JudgeLordComponent lordData = JudgeLordComponent.KEY.get(player);

        if (gameWorld.isRole(player, KinsWatheRoles.JUDGELORD) && GameFunctions.isPlayerAliveAndSurvival(player) && ability.cooldown <= 0) {
            if (lordData.getRemainingUses() <= 0) {
                player.sendMessage(Text.translatable("tip.kinswathe.judgelord.no_uses_left").withColor(Color.RED.getRGB()), true);
                return;
            }
            if (playerShop.balance < KinsWatheConfig.HANDLER.instance().JudgeLordAbilityPrice) {
                player.sendMessage(Text.translatable("tip.kinswathe.ability.not_enough_money", KinsWatheConfig.HANDLER.instance().JudgeLordAbilityPrice).withColor(Color.RED.getRGB()), true);
                return;
            }

            ServerPlayerEntity target = player.getServer().getPlayerManager().getPlayer(payload.target());
            if (target == null || !GameFunctions.isPlayerAliveAndSurvival(target)) {
                player.sendMessage(Text.translatable("tip.kinswathe.judgelord.invalid_target").withColor(Color.RED.getRGB()), true);
                return;
            }
            if (target == player) {
                player.sendMessage(Text.translatable("tip.kinswathe.judgelord.cannot_judge_self").withColor(Color.RED.getRGB()), true);
                return;
            }

            // 扣除金币和使用次数
            playerShop.balance -= KinsWatheConfig.HANDLER.instance().JudgeLordAbilityPrice;
            playerShop.sync();
            lordData.decrementRemainingUses();
            lordData.sync();

            // 发光效果
            int glowDuration = KinsWatheConfig.HANDLER.instance().JudgeLordGlowDuration * 20;
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, glowDuration, 0, false, true, true));

            // 闪电
            LightningEntity lightning = new LightningEntity(net.minecraft.entity.EntityType.LIGHTNING_BOLT, target.getWorld());
            lightning.refreshPositionAfterTeleport(target.getPos());
            lightning.setCosmetic(true);
            target.getWorld().spawnEntity(lightning);

            player.playSoundToPlayer(SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT, SoundCategory.PLAYERS, 1.0f, 1.0f);

            ability.setAbilityCooldown(KinsWatheConfig.HANDLER.instance().JudgeLordCooldown);

            // 开始监控
            lordData.startMonitoring(target.getUuid(), glowDuration);
        }
    }
}
