package org.BsXinQin.kinswathe.items;

import dev.doctor4t.wathe.game.GameFunctions;
import dev.doctor4t.wathe.index.WatheSounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.BsXinQin.kinswathe.packet.items.PanC2SPacket;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PanItem extends Item {

    public PanItem(@NotNull Settings settings) {super(settings);}

    @Override
    public @NotNull TypedActionResult<@NotNull ItemStack> use(@NotNull World world, @NotNull PlayerEntity player, @NotNull Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (player.getItemCooldownManager().isCoolingDown(this)) return TypedActionResult.fail(stack);
        player.setCurrentHand(hand);
        player.playSound(WatheSounds.ITEM_KNIFE_PREPARE, 1.0f, 0.1f);
        return TypedActionResult.consume(stack);
    }

    @Override
    public void onStoppedUsing(@NotNull ItemStack stack, @NotNull World world, @NotNull LivingEntity livingEntity, int remainingUseTicks) {
        if (!(livingEntity instanceof @NotNull PlayerEntity player) || player.isSpectator() || remainingUseTicks >= this.getMaxUseTime(stack, player) - 10) return;
        if (world.isClient && remainingUseTicks > 5) {
            HitResult hitResult = ProjectileUtil.getCollision(player, entity -> entity instanceof @NotNull PlayerEntity target && GameFunctions.isPlayerAliveAndSurvival(target), 3.0F);
            if (hitResult instanceof @NotNull EntityHitResult entityHitResult) {
                try {
                    Class<?> networkingClass = Class.forName("net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking");
                    Method getMethod = networkingClass.getMethod("send", net.minecraft.network.packet.CustomPayload.class);
                    getMethod.invoke(null, new PanC2SPacket(entityHitResult.getEntity().getId()));
                } catch (ClassNotFoundException | InvocationTargetException | IllegalAccessException | NoSuchMethodException ignored) {}
            }
        }
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.SPEAR;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity livingEntity) {
        return 100;
    }
}