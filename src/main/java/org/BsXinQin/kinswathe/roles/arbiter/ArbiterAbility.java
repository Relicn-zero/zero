package org.BsXinQin.kinswathe.roles.arbiter;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.PlayerShopComponent;
import dev.doctor4t.wathe.game.GameFunctions;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.BsXinQin.kinswathe.KinsWatheConfig;
import org.BsXinQin.kinswathe.KinsWatheRoles;
import org.BsXinQin.kinswathe.component.AbilityPlayerComponent;
import org.jetbrains.annotations.NotNull;

public class ArbiterAbility {

    public static void register(@NotNull PlayerEntity player) {
        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(player.getWorld());
        AbilityPlayerComponent ability = AbilityPlayerComponent.KEY.get(player);
        PlayerShopComponent playerShop = PlayerShopComponent.KEY.get(player);

        if (gameWorld.isRole(player, KinsWatheRoles.ARBITER) && GameFunctions.isPlayerAliveAndSurvival(player) && ability.cooldown <= 0) {
            if (playerShop.balance < KinsWatheConfig.HANDLER.instance().ArbiterAbilityPrice) return;

            HitResult hitResult = ProjectileUtil.getCollision(player, entity -> entity instanceof ServerPlayerEntity target && GameFunctions.isPlayerAliveAndSurvival(target), 2.0f);
            ServerPlayerEntity target = (hitResult instanceof EntityHitResult entityHitResult) ? (ServerPlayerEntity) entityHitResult.getEntity() : null;
            if (target == null || player.getUuid().equals(target.getUuid())) return;

            playerShop.balance -= KinsWatheConfig.HANDLER.instance().ArbiterAbilityPrice;
            playerShop.sync();

            target.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING,
                    KinsWatheConfig.HANDLER.instance().ArbiterGlowDuration * 20, 0, false, true));

            target.getServer().execute(() -> {
                if (target.isAlive() && !target.isSpectator()) {
                    target.damage(target.getWorld().getDamageSources().magic(), Float.MAX_VALUE);
                    target.sendMessage(Text.translatable("tip.kinswathe.arbiter.verdict_executed").withColor(0xAA0000), false);
                }
            });

            ability.setAbilityCooldown(KinsWatheConfig.HANDLER.instance().ArbiterCooldown);
            player.playSoundToPlayer(SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT, SoundCategory.PLAYERS, 1.0f, 1.0f);
        }
    }
}
