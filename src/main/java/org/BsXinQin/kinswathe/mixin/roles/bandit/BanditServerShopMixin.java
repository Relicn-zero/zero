package org.BsXinQin.kinswathe.mixin.roles.bandit;

import com.llamalad7.mixinextras.sugar.Local;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.PlayerShopComponent;
import dev.doctor4t.wathe.util.ShopEntry;
import net.minecraft.entity.player.PlayerEntity;
import org.BsXinQin.kinswathe.KinsWatheRoles;
import org.BsXinQin.kinswathe.KinsWatheShops;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PlayerShopComponent.class)
public class BanditServerShopMixin {

    @Shadow @Final private PlayerEntity player;

    @Inject(method = "tryBuy", at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
    private void replaceShopEntriesForBuy(int index, CallbackInfo ci, @Local List<ShopEntry> entries) {
        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(player.getWorld());
        if (gameWorld.isRole(player, KinsWatheRoles.BANDIT)) {
            List<ShopEntry> banditEntries = KinsWatheShops.getBanditShop(player.getWorld());
            entries.clear();
            entries.addAll(banditEntries);
        }
    }
}
