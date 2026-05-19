package org.BsXinQin.kinswathe.mixin.roles.kobe;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.BsXinQin.kinswathe.KinsWatheConfig;
import org.BsXinQin.kinswathe.KinsWatheRoles;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerEntity.class)
public class KobeSpeedMixin {

    @ModifyReturnValue(method = "getMovementSpeed", at = @At("RETURN"))
    private float modifyKobeSpeed(float original) {
        PlayerEntity self = (PlayerEntity) (Object) this;
        // 判断是否为科比且技能激活（需要用一个组件存储激活状态）
        // 这里简化：假设技能激活时某组件 ticks > 0
        if (KinsWatheRoles.isKobe(self) && KobeComponent.hasActive(self)) {
            return self.isSprinting() ? (float) KinsWatheConfig.HANDLER.instance().KobeSprintSpeed : (float) KinsWatheConfig.HANDLER.instance().KobeWalkSpeed;
        }
        return original;
    }
}
