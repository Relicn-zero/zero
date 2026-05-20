package org.BsXinQin.kinswathe.mixin;

// ... (必要的 import 语句)
import org.BsXinQin.kinswathe.component.KobeComponent;
import org.BsXinQin.kinswathe.component.BellringerComponent;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class KobeBellringerSpeedMixin {

    @ModifyVariable(method = "getMovementSpeed", at = @At("RETURN"), ordinal = 0)
    private float applySpeedBuff(float originalSpeed) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        float multiplier = 1.0f;

        // 检查科比组件是否激活
        KobeComponent kobeComp = KobeComponent.KEY.get(player);
        if (kobeComp != null && kobeComp.speedTicks > 0) {
            multiplier = 1.5f; // 科比的速度倍率
        } else {
            // 如果科比组件未激活，再检查敲钟人组件
            BellringerComponent bellringerComp = BellringerComponent.KEY.get(player);
            if (bellringerComp != null && bellringerComp.speedTicks > 0) {
                multiplier = 1.3f; // 敲钟人的速度倍率
            }
        }

        return originalSpeed * multiplier;
    }
}
