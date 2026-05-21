package org.BsXinQin.kinswathe.mixin.roles.bandit;

import org.BsXinQin.kinswathe.KinsWatheShops;
import org.BsXinQin.kinswathe.KinsWatheRoles;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wathe.extra.world.PlayerShopComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;

@Mixin(PlayerShopComponent.class)
public class BanditShopMixin {

    @Inject(method = "tryBuy", at = @At("HEAD"), cancellable = true)
    private void onTryBuy(PlayerEntity player, ItemStack itemStack, int amount, CallbackInfoReturnable<ActionResult> cir) {
        // 调试输出：确认 Mixin 被加载
        System.out.println("[BanditShop] tryBuy called, player=" + player.getName().getString());

        // 仅当玩家是土匪角色时修改商店
        if (KinsWatheRoles.isRole(player, KinsWatheRoles.BANDIT)) {
            System.out.println("[BanditShop] Player is BANDIT, using custom shop.");
            // 调用你自己的购买逻辑（使用土匪商店价格）
            ActionResult result = KinsWatheShops.handlePurchase(player, itemStack, amount, KinsWatheShops.getBanditShop());
            if (result == ActionResult.SUCCESS) {
                player.playSound(SoundEvents.ENTITY_VILLAGER_TRADE, 1.0f, 1.0f);
                cir.setReturnValue(ActionResult.SUCCESS);
            } else if (result == ActionResult.FAIL) {
                player.playSound(SoundEvents.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                cir.setReturnValue(ActionResult.FAIL);
            } else {
                // 如果是 PASS，说明物品不在土匪商店列表中，继续原逻辑
                return;
            }
            cir.cancel();
        } else {
            System.out.println("[BanditShop] Player is NOT BANDIT, skipping.");
        }
    }
}
