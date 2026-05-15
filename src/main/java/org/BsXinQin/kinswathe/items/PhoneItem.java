package org.BsXinQin.kinswathe.items;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.compat.TrainVoicePlugin;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.BsXinQin.kinswathe.KinsWatheVoiceChatPlugin;
import org.BsXinQin.kinswathe.roles.hacker.HackerPhoneComponent;
import org.jetbrains.annotations.NotNull;

public class PhoneItem extends Item {

    public PhoneItem(@NotNull Settings settings) {super(settings);}

    @Override
    public @NotNull TypedActionResult<@NotNull ItemStack> use(@NotNull World world, @NotNull PlayerEntity player, @NotNull Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        HackerPhoneComponent phone = HackerPhoneComponent.KEY.get(player);
        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(player.getWorld());
        if (!world.isClient) {
            if (gameWorld.getRole(player) != null) {
                if (phone.groupKiller) {
                    TrainVoicePlugin.resetPlayer(player.getUuid());
                    phone.groupKiller = false;
                    phone.sync();
                } else {
                    KinsWatheVoiceChatPlugin.addKillerGroup(player.getUuid());
                    phone.groupKiller = true;
                    phone.sync();
                }
                player.playSoundToPlayer(SoundEvents.BLOCK_NOTE_BLOCK_BANJO.value(), SoundCategory.PLAYERS, 1.0f, 1.0f);
            }
        }
        return TypedActionResult.pass(stack);
    }
}