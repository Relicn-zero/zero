package org.BsXinQin.kinswathe.mixin;

package org.BsXinQin.kinswathe.mixin;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import org.BsXinQin.kinswathe.component.SpeedComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PlayerEntity.class)
public class PlayerEntitySpeedMixin {

    @ModifyVariable(method = "getMovementSpeed", at = @At("RETURN"), ordinal = 0)
    private float modifySpeed(float originalSpeed) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        SpeedComponent speedComp = SpeedComponent.KEY.get(player);
        if (speedComp != null) {
            return originalSpeed * speedComp.getFinalMultiplier();
        }
        return originalSpeed;
    }
}
