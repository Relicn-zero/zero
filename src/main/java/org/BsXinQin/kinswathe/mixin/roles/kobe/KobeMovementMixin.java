package org.BsXinQin.kinswathe.mixin.roles.kobe;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import org.BsXinQin.kinswathe.KinsWatheConfig;
import org.BsXinQin.kinswathe.KinsWatheRoles;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class KobeMovementMixin {

    @Shadow public abstract boolean isSprinting();

    private long lastCrampCheckTime = 0;
    private boolean isCramping = false;

    /**
     * 修改移动速度：科比速度提升 20%
     */
    @ModifyVariable(method = "getMovementSpeed", at = @At("RETURN"), ordinal = 0)
    private float modifyMovementSpeed(float original) {
        PlayerEntity self = (PlayerEntity) (Object) this;
        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(self.getWorld());
        if (gameWorld.isRole(self, KinsWatheRoles.KOBE)) {
            return original * (float) KinsWatheConfig.HANDLER.instance().KobeSpeedMultiplier;
        }
        return original;
    }

    /**
     * 抽筋机制：疾跑时每 10 秒有概率触发缓慢效果
     */
    @Inject(method = "tick", at = @At("HEAD"))
    private void checkCramp(CallbackInfo ci) {
        PlayerEntity self = (PlayerEntity) (Object) this;
        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(self.getWorld());
        if (!gameWorld.isRole(self, KinsWatheRoles.KOBE)) return;

        if (self.isSprinting() && !isCramping) {
            long now = self.getWorld().getTime();
            if (lastCrampCheckTime == 0) lastCrampCheckTime = now;
            if (now - lastCrampCheckTime >= 200) { // 10 秒 = 200 tick
                lastCrampCheckTime = now;
                double prob = KinsWatheConfig.HANDLER.instance().KobeCrampProbability;
                if (self.getRandom().nextDouble() < prob) {
                    // 抽筋：施加缓慢 II 效果，持续 3 秒（60 tick）
                    isCramping = true;
                    self.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 60, 1, false, true, true));
                }
            }
        }
        if (isCramping) {
            // 检查缓慢效果是否还在（如果有更高等级的效果可能覆盖，简单处理）
            if (self.hasStatusEffect(StatusEffects.SLOWNESS)) {
                // 仍在抽筋中，不做额外动作
            } else {
                isCramping = false;
            }
        }
        if (!self.isSprinting()) {
            lastCrampCheckTime = 0; // 重置计时
        }
    }
}
