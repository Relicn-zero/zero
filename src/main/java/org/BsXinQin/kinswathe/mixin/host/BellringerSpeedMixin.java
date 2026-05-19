package org.BsXinQin.kinswathe.mixin.host;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.player.PlayerEntity;
import org.BsXinQin.kinswathe.KinsWatheConfig;
import org.BsXinQin.kinswathe.KinsWatheRoles;
import org.BsXinQin.kinswathe.component.TempSpeedComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerEntity.class)
public class BellringerSpeedMixin {

    @ModifyReturnValue(method = "getMovementSpeed", at = @At("RETURN"))
    private float modifyBellringerSpeed(float original) {
        PlayerEntity self = (PlayerEntity) (Object) this;
        try {
            if (dev.doctor4t.wathe.cca.GameWorldComponent.KEY.get(self.getWorld()).isRole(self, KinsWatheRoles.BELLRINGER)) {
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
