package org.BsXinQin.kinswathe.items;

import dev.doctor4t.wathe.block.SmallDoorBlock;
import dev.doctor4t.wathe.block_entity.SmallDoorBlockEntity;
import dev.doctor4t.wathe.util.AdventureUsable;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.BsXinQin.kinswathe.KinsWatheItems;
import org.jetbrains.annotations.NotNull;

public class WrenchItem extends Item implements AdventureUsable {

    public WrenchItem(@NotNull Settings settings) {
        super(settings);
    }

    @Override
    public @NotNull ActionResult useOnBlock(@NotNull ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        PlayerEntity player = context.getPlayer();
        BlockState state = world.getBlockState(pos);
        if (player.getItemCooldownManager().isCoolingDown(this)) return ActionResult.FAIL;
        if (!world.isClient && state.getBlock() instanceof @NotNull SmallDoorBlock) {
            BlockPos blockPos = state.get(SmallDoorBlock.HALF) == DoubleBlockHalf.LOWER ? pos : pos.down();
            if (world.getBlockEntity(blockPos) instanceof @NotNull SmallDoorBlockEntity entity) {
                if (entity.isJammed() || entity.isBlasted()) {
                    KinsWatheItems.setItemAfterUsing(player, this, null);
                    if (entity.isJammed()) {
                        entity.setJammed(0);
                        player.sendMessage(Text.literal(""), true);
                    } else if (entity.isBlasted()) {
                        entity.setBlasted(false);
                    }
                    if (!entity.isOpen()) {
                        entity.toggle(true);
                    }
                    entity.markDirty();
                    SmallDoorBlockEntity neighborDoorEntity = SmallDoorBlock.getNeighborDoorEntity(state, world, blockPos);
                    if (neighborDoorEntity != null) {
                        if (neighborDoorEntity.isJammed()) {
                            neighborDoorEntity.setJammed(0);
                            player.sendMessage(Text.literal(""), true);
                        } else if (neighborDoorEntity.isBlasted()) {
                            neighborDoorEntity.setBlasted(false);
                        }
                        if (!neighborDoorEntity.isOpen()) {
                            neighborDoorEntity.toggle(true);
                        }
                        neighborDoorEntity.markDirty();
                    }
                    world.playSound(null, blockPos, SoundEvents.BLOCK_PISTON_CONTRACT, SoundCategory.PLAYERS, 0.8F, 1.0F);
                    return ActionResult.SUCCESS;
                }
            }
            return ActionResult.PASS;
        }
        return super.useOnBlock(context);
    }
}