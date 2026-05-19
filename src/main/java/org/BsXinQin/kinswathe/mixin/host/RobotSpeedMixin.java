package org.BsXinQin.kinswathe.mixin.host;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.player.PlayerEntity;
import org.BsXinQin.kinswathe.KinsWatheRoles;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerEntity.class)
public class RobotSpeedMixin {

    @ModifyReturnValue(method = "getMovementSpeed", at = @At("RETURN"))
    private float modifyRobotSpeed(float original) {
        PlayerEntity self = (PlayerEntity) (Object) this;
        // 判断是否为机器人
        try {
            if (dev.doctor4t.wathe.cca.GameWorldComponent.KEY.get(self.getWorld()).isRole(self, KinsWatheRoles.ROBOT)) {
                return 0.12f; // 固定速度值，可配置
            }
        } catch (Exception ignored) {}
        return original;
    }
}
