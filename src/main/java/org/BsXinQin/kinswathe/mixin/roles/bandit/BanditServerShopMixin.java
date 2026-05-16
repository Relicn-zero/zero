package org.BsXinQin.kinswathe.mixin.roles.bandit;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.PlayerShopComponent;
import dev.doctor4t.wathe.util.ShopEntry;
import net.minecraft.entity.player.PlayerEntity;
import org.BsXinQin.kinswathe.KinsWatheRoles;
import org.BsXinQin.kinswathe.KinsWatheShops;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerShopComponent.class)
public class BanditServerShopMixin {

    @Shadow @Final @NotNull private PlayerEntity player;
    @Shadow private int balance;
    // 注意：不需要 shadow sync()，直接调用 this.sync() 即可（PlayerShopComponent 有 sync 方法）

    @Inject(method = "tryBuy", at = @At("HEAD"), cancellable = true)
    void tryBuy(int index, @NotNull CallbackInfo ci) {
        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(player.getWorld());
        if (gameWorld.isRole(player, KinsWatheRoles.BANDIT)) {
            var entries = KinsWatheShops.getBanditShop(player.getWorld());
            if (index < 0 || index >= entries.size()) {
                ci.cancel();
                return;
            }
            ShopEntry entry = entries.get(index);
            if (KinsWatheShops.handlePurchase(player, this.balance, entry.stack().getItem(), entry.price())) {
                this.balance -= entry.price();
                ((PlayerShopComponent)(Object)this).sync(); // 显式调用 sync 方法
            }
            ci.cancel();
        }
    }
}
