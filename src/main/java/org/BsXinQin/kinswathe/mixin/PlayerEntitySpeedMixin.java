package org.BsXinQin.kinswathe.mixin;

import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import org.BsXinQin.kinswathe.component.SpeedComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PlayerEntity.class)
public class PlayerEntitySpeedMixin {

    @ModifyVariable(
        method = "tickMovement",
        at = @At("HEAD"),
        argsOnly = true
    )
    private void applySpeedModifier() {
        // 在 tickMovement 开始时修改移动速度属性
        PlayerEntity player = (PlayerEntity) (Object) this;
        SpeedComponent speedComp = SpeedComponent.KEY.get(player);
        if (speedComp != null) {
            float multiplier = speedComp.getFinalMultiplier();
            player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED)
                    .setBaseValue(0.1f * multiplier); // 原版基础速度 0.1
        }
    }
}
