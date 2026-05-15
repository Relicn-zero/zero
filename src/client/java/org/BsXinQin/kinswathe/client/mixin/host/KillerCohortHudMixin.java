package org.BsXinQin.kinswathe.client.mixin.host;

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
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import org.BsXinQin.kinswathe.KinsWatheRoles;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RoleNameRenderer.class)
public class KillerCohortHudMixin {

    @Unique private static PlayerEntity TARGET = null;
    @Unique private static float COHORT_ALPHA = 0f;

    @Inject(method = "renderHud", at = @At("TAIL"))
    private static void getKillerCohortHud(@NotNull TextRenderer renderer, @NotNull ClientPlayerEntity player, @NotNull DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (MinecraftClient.getInstance().player == null) return;
        if (TARGET == null) return;
        if (TARGET != null) {
            COHORT_ALPHA = MathHelper.lerp(tickCounter.getTickDelta(true) / 4, COHORT_ALPHA, 1.0F);
        } else {
            COHORT_ALPHA = MathHelper.lerp(tickCounter.getTickDelta(true) / 4, COHORT_ALPHA, 0.0F);
        }
        if (COHORT_ALPHA <= 0.05F) return;
        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(player.getWorld());
        if (gameWorld.isRole(player, KinsWatheRoles.HACKER)) {
            if (WatheClient.isPlayerAliveAndInSurvival()) {
                if (gameWorld.getRole(TARGET) != null) {
                    if (gameWorld.canUseKillerFeatures(TARGET) || KinsWatheRoles.KILLER_NEUTRAL_ROLES.contains(gameWorld.getRole(TARGET)) || KinsWatheRoles.isKillerSidedNeutral(TARGET)) {
                        context.getMatrices().push();
                        context.getMatrices().translate(context.getScaledWindowWidth() / 2.0F, context.getScaledWindowHeight() / 2.0F + 26, 0);
                        context.getMatrices().scale(0.6F, 0.6F, 1.0F);
                        MutableText roleText = Text.translatable("game.tip.cohort");
                        int roleWidth = renderer.getWidth(roleText);
                        int color = MathHelper.packRgb(1.0F, 0.0F, 0.0F) | ((int) (COHORT_ALPHA * 255) << 24);
                        context.drawTextWithShadow(renderer, roleText, -roleWidth / 2, 0, color);
                        context.getMatrices().pop();
                    }
                }
            }
        }
    }

    @Inject(method = "renderHud", at = @At(value = "INVOKE", target = "Ldev/doctor4t/wathe/game/GameFunctions;isPlayerSpectatingOrCreative(Lnet/minecraft/entity/player/PlayerEntity;)Z"))
    private static void getTarget(TextRenderer renderer, @NotNull ClientPlayerEntity player, DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(player.getWorld());
        if (gameWorld.isRole(player, KinsWatheRoles.HACKER)) {
        HitResult hitResult = ProjectileUtil.getCollision(player, entity -> entity instanceof @NotNull PlayerEntity target && GameFunctions.isPlayerAliveAndSurvival(target), 2.0F);
        TARGET = (hitResult instanceof @NotNull EntityHitResult entityHitResult) ? (PlayerEntity) entityHitResult.getEntity() : null;
        } else {TARGET = null;}
    }
}