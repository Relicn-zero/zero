package org.BsXinQin.kinswathe.mixin.roles.kobe;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.BsXinQin.kinswathe.KinsWatheConfig;
import org.BsXinQin.kinswathe.KinsWatheRoles;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class KobePickupMixin {

    @Inject(method = "canPickupItem", at = @At("HEAD"), cancellable = true)
    private void allowKobePickup(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity self = (PlayerEntity) (Object) this;
        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(self.getWorld());
        if (gameWorld.isRole(self, KinsWatheRoles.KOBE) && KinsWatheConfig.HANDLER.instance().KobeCanPickupVigilanteGun) {
            // 允许拾取任何物品（覆盖原有限制）
            cir.setReturnValue(true);
        }
    }
}
