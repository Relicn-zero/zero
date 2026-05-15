package org.BsXinQin.kinswathe.client.mixin.host;

import dev.doctor4t.wathe.Wathe;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.client.WatheClient;
import dev.doctor4t.wathe.game.GameConstants;
import dev.doctor4t.wathe.index.WatheSounds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.MathHelper;
import org.BsXinQin.kinswathe.client.KinsWatheInitializeClient;
import org.BsXinQin.kinswathe.component.ConfigWorldComponent;
import org.BsXinQin.kinswathe.roles.technician.TechnicianComponent;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class BetterBlackOutMixin {

    @Unique private static long INSIDE_TIME = 0;
    @Unique private static boolean OUTSIDE = true;
    @Unique private static long OUTSIDE_TIME = 0;
    @Unique private static boolean WAS_INSIDE = false;
    @Unique private static long INSTINCT_CHANGE_TIME = 0;
    @Unique private static boolean LAST_INSTINCT_STATE = false;
    @Unique private static float INSTINCT_START_ALPHA = 0;
    @Unique private static float CURRENT_ALPHA = 0;

    @Inject(method = "render", at = @At("HEAD"))
    private void getBetterBlackoutHud(@NotNull DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (MinecraftClient.getInstance().player == null) return;
        if (!ConfigWorldComponent.KEY.get(MinecraftClient.getInstance().player.getWorld()).EnableBetterBlackout) return;
        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(MinecraftClient.getInstance().player.getWorld());
        TechnicianComponent playerBlackout = TechnicianComponent.KEY.get(MinecraftClient.getInstance().player);
        long currentTime = System.currentTimeMillis();
        long blackoutTime = KinsWatheInitializeClient.BLACKOUT_TIME;
        if (gameWorld.isRunning() && currentTime < blackoutTime && playerBlackout.blackoutTicks > 0) {
            boolean isOutside = Wathe.isSkyVisibleAdjacent(MinecraftClient.getInstance().player);
            boolean isInstinctEnabled = WatheClient.isInstinctEnabled();
            float currentAlpha = CURRENT_ALPHA;
            if (isInstinctEnabled != LAST_INSTINCT_STATE) {
                INSTINCT_CHANGE_TIME = currentTime;
                INSTINCT_START_ALPHA = currentAlpha;
                LAST_INSTINCT_STATE = isInstinctEnabled;
            }
            if (OUTSIDE && !isOutside) {
                INSIDE_TIME = currentTime;
            }
            if (!OUTSIDE && isOutside) {
                OUTSIDE_TIME = currentTime;
                WAS_INSIDE = true;
            }
            OUTSIDE = isOutside;
            if (WatheClient.isPlayerAliveAndInSurvival() && !MinecraftClient.getInstance().player.hasStatusEffect(StatusEffects.NIGHT_VISION)) {
                int alpha = calculateAlpha(isOutside, isInstinctEnabled, getBlackoutAlpha(blackoutTime, currentTime), currentTime);
                CURRENT_ALPHA = alpha;
                if (alpha > 0) {
                    context.fill(0, 0, context.getScaledWindowWidth(), context.getScaledWindowHeight(), (alpha << 24));
                }
            }
        }
    }

    @Mixin(SoundSystem.class)
    public static class SoundSystemMixin {
        @Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", at = @At("HEAD"))
        private void onPlaySound(@NotNull SoundInstance sound, CallbackInfo ci) {
            if (MinecraftClient.getInstance().player == null) return;
            if (!ConfigWorldComponent.KEY.get(MinecraftClient.getInstance().player.getWorld()).EnableBetterBlackout) return;
            GameWorldComponent gameWorld = GameWorldComponent.KEY.get(MinecraftClient.getInstance().player.getWorld());
            TechnicianComponent playerBlackout = TechnicianComponent.KEY.get(MinecraftClient.getInstance().player);
            if (gameWorld.isRunning() && sound.getId().equals(WatheSounds.AMBIENT_BLACKOUT.getId())) {
                playerBlackout.setBlackoutTicks(GameConstants.BLACKOUT_MAX_DURATION);
                KinsWatheInitializeClient.BLACKOUT_TIME = System.currentTimeMillis() + (GameConstants.BLACKOUT_MAX_DURATION * 50L);
            }
        }
    }

    @Unique
    private int calculateAlpha(boolean isOutside, boolean isInstinctEnabled, int targetAlpha, long currentTime) {
        long timeSinceInstinctChange = currentTime - INSTINCT_CHANGE_TIME;
        float instinctProgress = MathHelper.clamp((float) timeSinceInstinctChange / 500, 0f, 1f);
        float baseTarget;
        if (isOutside) {
            if (WAS_INSIDE) {
                long timeOutside = currentTime - OUTSIDE_TIME;
                if (timeOutside < 500) {
                    float outsideProgress = (float) timeOutside / 500;
                    baseTarget = targetAlpha * (1 - outsideProgress);
                } else {
                    WAS_INSIDE = false;
                    baseTarget = 0;
                }
            } else {
                baseTarget = 0;
            }
        } else {
            long timeInside = currentTime - INSIDE_TIME;
            if (timeInside < 500) {
                float insideProgress = (float) timeInside / 500;
                baseTarget = targetAlpha * insideProgress;
            } else {
                baseTarget = targetAlpha;
            }
        }
        float finalTarget;
        if (isInstinctEnabled) {
            finalTarget = 0;
        } else {
            finalTarget = baseTarget;
        }
        if (timeSinceInstinctChange < 500) {
            return (int) (INSTINCT_START_ALPHA + (finalTarget - INSTINCT_START_ALPHA) * instinctProgress);
        } else {
            return (int) finalTarget;
        }
    }

    @Unique
    private static int getBlackoutAlpha(long blackoutTime, long currentTime) {
        long startTime = blackoutTime - (GameConstants.BLACKOUT_MAX_DURATION * 50L);
        long fadeStartTime = startTime + (GameConstants.BLACKOUT_MIN_DURATION * 50L);
        if (currentTime < fadeStartTime) {
            return (int) (255 * 0.8f);
        } else {
            long fadeDuration = (GameConstants.BLACKOUT_MAX_DURATION - GameConstants.BLACKOUT_MIN_DURATION) * 50L;
            long fadeElapsed = currentTime - fadeStartTime;
            float progress = MathHelper.clamp((float) fadeElapsed / fadeDuration, 0f, 1f);
            return (int) (255 * 0.8f * (1 - progress));
        }
    }
}