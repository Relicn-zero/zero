package org.BsXinQin.kinswathe.client.mixin.roles.technician;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.BsXinQin.kinswathe.KinsWatheItems;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerEntityRenderer.class)
public class TechnicianHandPosMixin {

    @WrapOperation(method = "getArmPose", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;getStackInHand(Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;"))
    private static ItemStack hideCaptureDeviceHandPos(@NotNull AbstractClientPlayerEntity player, @NotNull Hand hand, @NotNull Operation<@NotNull ItemStack> original) {
        ItemStack stack = original.call(player, hand);
        if (stack.isOf(KinsWatheItems.CAPTURE_DEVICE)) {
            return ItemStack.EMPTY;
        }
        return stack;
    }
}