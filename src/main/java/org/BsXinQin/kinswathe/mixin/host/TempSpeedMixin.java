package org.BsXinQin.kinswathe.mixin.host;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.player.PlayerEntity;
import org.BsXinQin.kinswathe.KinsWatheConfig;
import org.BsXinQin.kinswathe.KinsWatheRoles;
import org.BsXinQin.kinswathe.component.TempSpeedComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerEntity.class)
public class TempSpeedMixin {

    @ModifyReturnValue(method = "getMovementSpeed", at = @At("RETURN"))
    private float modifyTempSpeed(float original) {
        PlayerEntity self = (PlayerEntity) (Object) this;
        try {
            dev.doctor4t.wathe.cca.GameWorldComponent gw = dev.doctor4t.wathe.cca.GameWorldComponent.KEY.get(self.getWorld());
            if (gw.isRole(self, KinsWatheRoles.KOBE)) {
                TempSpeedComponent comp = TempSpeedComponent.KEY.get(self);
                if (comp.isActive()) {
                    // 使用科比的专用速度值（可配置）
                    return self.isSprinting() ? (float) KinsWatheConfig.HANDLER.instance().KobeCustomSprintSpeed : (float) KinsWatheConfig.HANDLER.instance().KobeCustomWalkSpeed;
                }
            } else if (gw.isRole(self, KinsWatheRoles.BELLRINGER)) {
                TempSpeedComponent comp = TempSpeedComponent.KEY.get(self);
                if (comp.isActive()) {
                    float multiplier = (float) KinsWatheConfig.HANDLER.instance().BellringerSpeedMultiplier;
                    return original * multiplier;
                }
            }
        } catch (Exception ignored) {}
        return original;
    }
}
