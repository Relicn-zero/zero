package org.BsXinQin.kinswathe.client.mixin.roles.hunter;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.UseAction;
import org.BsXinQin.kinswathe.KinsWatheRoles;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class HunterFovMixin {

    @Shadow @Final MinecraftClient client;

    @Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
    private void modifyHunterFov(Camera camera, float tickDelta, boolean changingFov, @NotNull CallbackInfoReturnable<Double> cir) {
        if (this.client.player == null) return;
        double fov = cir.getReturnValue();
        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(this.client.player.getWorld());
        if (gameWorld.isRole(this.client.player, KinsWatheRoles.HUNTER) && this.client.player.isUsingItem()) {
            ItemStack stack = this.client.player.getActiveItem();
            Item item = stack.getItem();
            if (item.getUseAction(stack) == UseAction.SPEAR && this.client.player.isSprinting()) {
                cir.setReturnValue(fov * 1.1D);
            }
        }
    }
}