package org.BsXinQin.kinswathe.items;

import dev.doctor4t.wathe.game.GameFunctions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.BsXinQin.kinswathe.KinsWatheItems;
import org.BsXinQin.kinswathe.packet.items.BlowgunC2SPacket;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;

public class BlowgunItem extends Item {

    public BlowgunItem(@NotNull Settings settings) {super(settings);}
    private static final Random random = new Random();

    @Override
    public @NotNull TypedActionResult<@NotNull ItemStack> use(@NotNull World world, @NotNull PlayerEntity player, @NotNull Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (player.getItemCooldownManager().isCoolingDown(this)) return TypedActionResult.fail(stack);
        KinsWatheItems.setItemAfterUsing(player, KinsWatheItems.BLOWGUN, null);
        player.playSound(SoundEvents.ENTITY_PUFFER_FISH_BLOW_OUT, 0.5f, 1.5f);
        if (world.isClient) {
            HitResult hitResult = ProjectileUtil.getCollision(player, entity -> entity instanceof @NotNull PlayerEntity target && GameFunctions.isPlayerAliveAndSurvival(target), 15.0F);
            if (hitResult instanceof @NotNull EntityHitResult entityHitResult) {
                try {
                    Class<?> networkingClass = Class.forName("net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking");
                    Method getMethod = networkingClass.getMethod("send", net.minecraft.network.packet.CustomPayload.class);
                    getMethod.invoke(null, new BlowgunC2SPacket(entityHitResult.getEntity().getId()));
                } catch (ClassNotFoundException | InvocationTargetException | IllegalAccessException | NoSuchMethodException ignored) {}
            }
        }
        return TypedActionResult.success(stack, false);
    }
}