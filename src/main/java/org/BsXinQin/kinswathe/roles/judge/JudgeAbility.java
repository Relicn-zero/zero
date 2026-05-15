package org.BsXinQin.kinswathe.roles.judge;

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
import org.BsXinQin.kinswathe.packet.roles.JudgeC2SPacket;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class JudgeAbility {

    public static void register(@NotNull JudgeC2SPacket payload, @NotNull PlayerEntity player) {
        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(player.getWorld());
        AbilityPlayerComponent ability = AbilityPlayerComponent.KEY.get(player);
        PlayerShopComponent playerShop = PlayerShopComponent.KEY.get(player);
        if (gameWorld.isRole(player, KinsWatheRoles.JUDGE) && GameFunctions.isPlayerAliveAndSurvival(player) && ability.cooldown <= 0) {
            if (playerShop.balance < KinsWatheConfig.HANDLER.instance().JudgeAbilityPrice) return;
            ServerPlayerEntity target = player.getServer().getPlayerManager().getPlayer(payload.target());
            if (GameFunctions.isPlayerAliveAndSurvival(target)) {
                playerShop.balance -= KinsWatheConfig.HANDLER.instance().JudgeAbilityPrice;
                playerShop.sync();
                target.sendMessage(Text.translatable("tip.kinswathe.judge.notification").withColor(KinsWatheRoles.JUDGE.color()), true);
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, KinsWatheConfig.HANDLER.instance().JudgeAbilityGlowing * 20, 0, false, true, true));
                var lightning = new LightningEntity(net.minecraft.entity.EntityType.LIGHTNING_BOLT, target.getWorld());
                lightning.refreshPositionAfterTeleport(target.getPos());
                lightning.setCosmetic(true);
                target.getWorld().spawnEntity(lightning);
                player.playSoundToPlayer(SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT, SoundCategory.PLAYERS, 1.0f, 1.0f);
                ability.setAbilityCooldown(KinsWatheConfig.HANDLER.instance().JudgeAbilityCooldown);
            } else {
                player.sendMessage(Text.translatable("tip.kinswathe.judge.ability_failed").withColor(Color.RED.getRGB()), true);
                player.playSoundToPlayer(SoundEvents.ENTITY_VILLAGER_AMBIENT, SoundCategory.PLAYERS, 1.0f, 1.0f);
                ability.setAbilityCooldown(KinsWatheConfig.HANDLER.instance().JudgeAbilityCooldown / 2);
            }
        }
    }
}