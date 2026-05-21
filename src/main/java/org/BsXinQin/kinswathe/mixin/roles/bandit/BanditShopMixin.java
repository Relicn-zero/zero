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

import java.util.List;

@Mixin(PlayerShopComponent.class)
public abstract class BanditShopMixin {

    @Shadow public int balance;
    @Shadow public abstract void sync();
    @Shadow @Final @NotNull private PlayerEntity player;

    @Inject(method = "tryBuy", at = @At("HEAD"), cancellable = true)
    void tryBuy(int index, @NotNull CallbackInfo ci) {
        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(this.player.getWorld());
        if (gameWorld.isRole(this.player, KinsWatheRoles.BANDIT)) {
            List<ShopEntry> shop = KinsWatheShops.getBanditShop(this.player.getWorld());
            if (index < 0 || index >= shop.size()) return;
            ShopEntry entry = shop.get(index);
            if (KinsWatheShops.handlePurchase(this.player, this.balance, entry.stack().getItem(), entry.price())) {
                this.balance -= entry.price();
                this.sync();
                ci.cancel();
            }
        }
    }
}
