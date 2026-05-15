package org.BsXinQin.kinswathe.client.mixin.roles.hacker;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.client.WatheClient;
import dev.doctor4t.wathe.client.gui.RoleNameRenderer;
import dev.doctor4t.wathe.game.GameFunctions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.text.Text;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.BsXinQin.kinswathe.KinsWatheRoles;
import org.BsXinQin.kinswathe.component.ConfigWorldComponent;
import org.BsXinQin.kinswathe.component.GameSafeComponent;
import org.BsXinQin.kinswathe.roles.hacker.HackerComponent;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(RoleNameRenderer.class)
public class HackerTargetHudMixin {

    @Unique private static PlayerEntity HACKER_TARGET = null;

    @Inject(method = "renderHud", at = @At("TAIL"))
    private static void getTargetHud(@NotNull TextRenderer renderer, @NotNull ClientPlayerEntity player, @NotNull DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (MinecraftClient.getInstance().player == null) return;
        if (HACKER_TARGET == null) return;
        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(player.getWorld());
        HackerComponent targetHack = HackerComponent.KEY.get(HACKER_TARGET);
        GameSafeComponent gameSafe = GameSafeComponent.KEY.get(player.getWorld());
        if (gameWorld.isRole(player, KinsWatheRoles.HACKER) && WatheClient.isPlayerAliveAndInSurvival()) {
            context.getMatrices().push();
            context.getMatrices().translate((float) context.getScaledWindowWidth() / 2.0F, (float) context.getScaledWindowHeight() / 2.0F + 6.0F, 0.0F);
            context.getMatrices().scale(0.6F, 0.6F, 1.0F);
            Text targetInfo;
            if (gameWorld.getRole(HACKER_TARGET) != null) {
                if (!(gameWorld.canUseKillerFeatures(HACKER_TARGET) || KinsWatheRoles.KILLER_NEUTRAL_ROLES.contains(gameWorld.getRole(HACKER_TARGET)) || KinsWatheRoles.isKillerSidedNeutral(HACKER_TARGET))) {
                    if (gameSafe.isSafe()) {
                        targetInfo = Text.translatable("hud.kinswathe.hacker.target_safe").withColor(KinsWatheRoles.HACKER.color());
                        context.drawTextWithShadow(renderer, targetInfo, -renderer.getWidth(targetInfo) / 2, 32, KinsWatheRoles.HACKER.color());
                    } else {
                        if (targetHack.hackingTime < ConfigWorldComponent.KEY.get(player.getWorld()).HackerHackingTime * 20) {
                            targetInfo = Text.translatable("hud.kinswathe.hacker.target").styled(style -> style.withColor(KinsWatheRoles.HACKER.color())).append(Text.literal(" [ " + (int) (((float) targetHack.hackingTime / (ConfigWorldComponent.KEY.get(player.getWorld()).HackerHackingTime * 20)) * 100) + "% ]").styled(style -> style.withColor(Color.GREEN.getRGB())));
                        } else {
                            targetInfo = Text.translatable("hud.kinswathe.hacker.target_hacked").styled(style -> style.withColor(Color.GREEN.getRGB()));
                        }
                    }
                    context.drawTextWithShadow(renderer, targetInfo, -renderer.getWidth(targetInfo) / 2, 32, KinsWatheRoles.HACKER.color());
                }
            }
            context.getMatrices().pop();
        }
    }

    @Inject(method = "renderHud", at = @At(value = "INVOKE", target = "Ldev/doctor4t/wathe/game/GameFunctions;isPlayerSpectatingOrCreative(Lnet/minecraft/entity/player/PlayerEntity;)Z"))
    private static void getTarget(TextRenderer renderer, @NotNull ClientPlayerEntity player, DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(player.getWorld());
        if (!gameWorld.isRole(player, KinsWatheRoles.HACKER)) {HACKER_TARGET = null;return;}
        HitResult hitResult = ProjectileUtil.getCollision(player, entity -> entity instanceof @NotNull PlayerEntity target && GameFunctions.isPlayerAliveAndSurvival(target), 2.0F);
        HACKER_TARGET = (hitResult instanceof @NotNull EntityHitResult entityHitResult) ? (PlayerEntity) entityHitResult.getEntity() : null;
    }
}