package org.BsXinQin.kinswathe.client.mixin.roles.hunter;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.doctor4t.wathe.client.WatheClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.UseAction;
import org.BsXinQin.kinswathe.items.HuntingKnifeItem;
import org.BsXinQin.kinswathe.roles.hunter.HunterComponent;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MinecraftClient.class)
public class HunterLockSlotMixin {

    @WrapOperation(method = "handleInputEvents", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerInventory;selectedSlot:I", opcode = Opcodes.PUTFIELD))
    private void lockHunterSlot(@NotNull PlayerInventory inventory, int value, Operation<Void> original) {
        if (MinecraftClient.getInstance().player == null) return;
        int lockSlot = inventory.selectedSlot;
        HunterComponent playerHunter = HunterComponent.KEY.get(MinecraftClient.getInstance().player);
        if (inventory.player.isUsingItem() && WatheClient.isPlayerAliveAndInSurvival() && playerHunter.isSprinting) {
            ItemStack stack = inventory.player.getActiveItem();
            Item item = stack.getItem();
            if (item instanceof @NotNull HuntingKnifeItem knife && knife.getUseAction(stack) == UseAction.SPEAR && (inventory.getStack(lockSlot).isOf(knife)) && (!inventory.getStack(value).isOf(knife))) {
                return;
            }
        }
        original.call(inventory, value);
    }
}
