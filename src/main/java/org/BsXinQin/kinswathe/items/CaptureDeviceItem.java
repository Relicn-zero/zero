package org.BsXinQin.kinswathe.items;

import dev.doctor4t.wathe.util.AdventureUsable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.BsXinQin.kinswathe.KinsWatheEntities;
import org.BsXinQin.kinswathe.KinsWatheItems;
import org.BsXinQin.kinswathe.entities.CaptureDeviceEntity;
import org.jetbrains.annotations.NotNull;

public class CaptureDeviceItem extends Item implements AdventureUsable {

    public CaptureDeviceItem(@NotNull Settings settings) {
        super(settings);
    }

    @Override
    public @NotNull ActionResult useOnBlock(@NotNull ItemUsageContext context) {
        PlayerEntity player = context.getPlayer();
        World world = player.getWorld();
        if (player.getItemCooldownManager().isCoolingDown(this)) return ActionResult.FAIL;
        if (context.getSide() == Direction.UP) {
            KinsWatheItems.setItemAfterUsing(player, this, context.getHand());
            if (!world.isClient) {
                Vec3d spawnPos = context.getHitPos();
                CaptureDeviceEntity entity = KinsWatheEntities.CAPTURE_DEVICE.create(world);
                player.playSoundToPlayer(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 1.0F, 1.0F);
                entity.setPosition(spawnPos.x, spawnPos.y, spawnPos.z);
                entity.setYaw(player.getHeadYaw());
                entity.setOwner(player.getUuid());
                world.spawnEntity(entity);
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
}