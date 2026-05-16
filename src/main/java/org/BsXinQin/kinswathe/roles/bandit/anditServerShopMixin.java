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
public class BanditServerShopMixin {

    @Shadow @Final @NotNull private PlayerEntity player;

    /**
     * 在 tryBuy 方法执行前，修改商店条目列表，为土匪替换为折扣列表。
     * 注意：原模组中 tryBuy 方法内有一个局部变量 entries，我们可以通过 @ModifyVariable 来替换，
     * 但这里使用 @Inject 配合 LocalCapture 可能更复杂。简单的方法：在 tryBuy 开头判断角色，然后直接修改价格逻辑。
     * 不过为了保持结构一致，我们参考 DrugmakerShopMixin 的做法：使用 @ModifyVariable 修改 entries。
     */
    @ModifyVariable(method = "tryBuy", at = @At(value = "STORE"), name = "entries", ordinal = 0)
    private List<ShopEntry> modifyShopEntries(List<ShopEntry> originalEntries) {
        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(player.getWorld());
        if (gameWorld.isRole(player, KinsWatheRoles.BANDIT)) {
            return KinsWatheShops.getBanditShop(player.getWorld());
        }
        return originalEntries;
    }
}
