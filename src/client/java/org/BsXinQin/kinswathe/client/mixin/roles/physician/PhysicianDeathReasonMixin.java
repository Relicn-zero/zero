package org.BsXinQin.kinswathe.client.mixin.roles.physician;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.client.WatheClient;
import dev.doctor4t.wathe.client.gui.RoleNameRenderer;
import dev.doctor4t.wathe.entity.PlayerBodyEntity;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.BsXinQin.kinswathe.KinsWatheRoles;
import org.BsXinQin.kinswathe.component.BodyDeathReasonComponent;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Mixin(RoleNameRenderer.class)
public class PhysicianDeathReasonMixin {

    @Unique private static PlayerBodyEntity targetBody;

    @Inject(method = "renderHud", at = @At("HEAD"))
    private static void onRenderHud(@NotNull TextRenderer renderer, @NotNull ClientPlayerEntity player, @NotNull DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (MinecraftClient.getInstance().player == null) return;
        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(MinecraftClient.getInstance().player.getWorld());
        if (gameWorld.isRole(MinecraftClient.getInstance().player, KinsWatheRoles.PHYSICIAN) && WatheClient.isPlayerAliveAndInSurvival()) {
            updateTargetBody(player);
            if (targetBody != null) {
                renderDeathInfo(renderer, player, context);
            }
        }
    }

    @Unique
    private static void updateTargetBody(@NotNull ClientPlayerEntity player) {
        HitResult hitResult = ProjectileUtil.getCollision(player, entity -> entity instanceof @NotNull PlayerBodyEntity, 2.0F);
        targetBody = null;
        if (hitResult instanceof @NotNull EntityHitResult entityHitResult) {
            if (entityHitResult.getEntity() instanceof @NotNull PlayerBodyEntity bodyEntity) {
                targetBody = bodyEntity;
            }
        }
    }

    @Unique
    private static boolean isVultured(@NotNull PlayerBodyEntity playerBody) {
        if (FabricLoader.getInstance().isModLoaded("noellesroles")) {
            try {
                Class<?> bodyDeathReasonClass = Class.forName("org.agmas.noellesroles.coroner.BodyDeathReasonComponent");
                Field keyField = bodyDeathReasonClass.getField("KEY");
                Object componentKey = keyField.get(null);
                Method getComponentMethod = componentKey.getClass().getMethod("get", Object.class);
                Object deathReasonInstance = getComponentMethod.invoke(componentKey, playerBody);
                Field vulturedField = bodyDeathReasonClass.getField("vultured");
                return (boolean) vulturedField.get(deathReasonInstance);
            } catch (NoSuchFieldException | ClassNotFoundException | InvocationTargetException | IllegalAccessException | NoSuchMethodException ignored) {}
        }
        return false;
    }

    @Unique
    private static void renderDeathInfo(@NotNull TextRenderer renderer, @NotNull ClientPlayerEntity player, @NotNull DrawContext context) {
        Text deathInfo;
        BodyDeathReasonComponent bodyDeathReason = BodyDeathReasonComponent.KEY.get(targetBody);
        if (isVultured(targetBody)) {
            int randomLength = player.getRandom().nextBetween(12, 26);
            deathInfo = Text.literal("a".repeat(randomLength)).formatted(Formatting.OBFUSCATED);
        } else {
            deathInfo = Text.translatable("hud.death_info", targetBody.age / 20).append(Text.translatable("death_reason." + bodyDeathReason.deathReason.getNamespace() + "." + bodyDeathReason.deathReason.getPath()));
        }
        context.getMatrices().push();
        context.getMatrices().translate((float) context.getScaledWindowWidth() / 2.0F, (float) context.getScaledWindowHeight() / 2.0F + 6.0F, 0.0F);
        context.getMatrices().scale(0.6F, 0.6F, 1.0F);
        context.drawTextWithShadow(renderer, deathInfo, -renderer.getWidth(deathInfo) / 2, 32, Colors.RED);
        context.getMatrices().pop();
    }
}