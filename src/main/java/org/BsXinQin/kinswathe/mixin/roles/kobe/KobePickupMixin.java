package org.BsXinQin.kinswathe.mixin.roles.kobe;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.BsXinQin.kinswathe.KinsWatheConfig;
import org.BsXinQin.kinswathe.KinsWatheRoles;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEntity.class)
public class KobePickupMixin {

    @Inject(method = "canMerge", at = @At("HEAD"), cancellable = true)
    private void allowKobePickupVigilanteGun(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        // 此方法控制物品是否可以与实体合并，不适合拦截拾取。
        // 更好的方法是监听 PlayerEntity 的拾取事件。
    }
}
