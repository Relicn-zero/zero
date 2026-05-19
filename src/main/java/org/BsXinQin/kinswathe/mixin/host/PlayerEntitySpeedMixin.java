package org.BsXinQin.kinswathe.mixin.host;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.player.PlayerEntity;
import org.BsXinQin.kinswathe.component.SpeedComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntitySpeedMixin {

    @ModifyReturnValue(method = "getMovementSpeed", at = @At("RETURN"))
    private float modifyMovementSpeed(float original) {
        PlayerEntity self = (PlayerEntity) (Object) this;
        SpeedComponent speedComp = SpeedComponent.KEY.get(self);
        if (speedComp.hasCustomSpeed()) {
            return self.isSprinting() ? speedComp.getCustomSprintSpeed() : speedComp.getCustomWalkSpeed();
        }
        float multiplier = speedComp.getMultiplier();
        if (multiplier != 1.0f) {
            return original * multiplier;
        }
        return original;
    }
}
