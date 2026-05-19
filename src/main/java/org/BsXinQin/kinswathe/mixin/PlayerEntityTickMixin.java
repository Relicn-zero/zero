package org.BsXinQin.kinswathe.mixin;

import net.minecraft.entity.player.PlayerEntity;
import org.BsXinQin.kinswathe.component.TempSpeedComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerEntityTickMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        TempSpeedComponent speedComp = TempSpeedComponent.KEY.get(player);
        if (speedComp != null && speedComp.isActive()) {
            speedComp.tick();
        }
    }
}
