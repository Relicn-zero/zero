package org.BsXinQin.kinswathe.entities;

import dev.doctor4t.wathe.game.GameFunctions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.BsXinQin.kinswathe.KinsWatheConfig;
import org.BsXinQin.kinswathe.KinsWatheRoles;
import org.BsXinQin.kinswathe.component.PlayerEffectComponent;
import org.BsXinQin.kinswathe.roles.technician.TechnicianComponent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class CaptureDeviceEntity extends Entity {

    protected void initDataTracker(DataTracker.Builder builder) {}

    private UUID technicianUUID = null;
    private int technicianLifeTime = 0;

    public CaptureDeviceEntity(@NotNull EntityType<?> type, @NotNull World world) {
        super(type, world);
    }

    @Override
    public void tick() {
        if (this.getWorld().isClient) return;
        if (this.technicianUUID == null) return;
        if (this.technicianLifeTime <= KinsWatheConfig.HANDLER.instance().TechnicianCaptureDeviceLifetimeSeconds * 20) {
            this.technicianLifeTime ++;
            List<ServerPlayerEntity> players = this.getWorld().getEntitiesByClass(ServerPlayerEntity.class, this.getBoundingBox().expand(0), player -> GameFunctions.isPlayerAliveAndSurvival(player) && !player.getUuid().equals(this.technicianUUID));
            if (!players.isEmpty()) {
                ServerPlayerEntity target = players.get(this.getWorld().random.nextInt(players.size()));
                PlayerEntity technician = this.getWorld().getPlayerByUuid(this.technicianUUID);
                if (target != null) {
                    PlayerEffectComponent.KEY.get(target).setStunTicks(KinsWatheConfig.HANDLER.instance().TechnicianCaptureDeviceStunTime * 20);
                    TechnicianComponent.KEY.get(target).setCapturedTicks(KinsWatheConfig.HANDLER.instance().TechnicianCaptureDeviceStunTime * 20);
                    this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.PLAYERS, 0.8F, 1.5F);
                    if (GameFunctions.isPlayerAliveAndSurvival(technician)) {
                        technician.sendMessage(Text.translatable("tip.kinswathe.technician.captured", target.getName().getString()).withColor(KinsWatheRoles.TECHNICIAN.color()), true);
                        technician.playSoundToPlayer(SoundEvents.BLOCK_NOTE_BLOCK_BANJO.value(), SoundCategory.PLAYERS, 1.0F, 1.5F);
                    }
                }
                this.discard();
            }
        } else {
            this.discard();
        }
    }

    public void setOwner(@NotNull UUID technicianUUID) {
        this.technicianUUID = technicianUUID;
    }

    @Override
    protected void writeCustomDataToNbt(@NotNull NbtCompound nbt) {
        nbt.putInt("technicianLifeTime", this.technicianLifeTime);
        if (this.technicianUUID != null) nbt.putUuid("technicianUUID", this.technicianUUID);
    }

    @Override
    protected void readCustomDataFromNbt(@NotNull NbtCompound nbt) {
        this.technicianLifeTime = nbt.contains("technicianLifeTime") ? nbt.getInt("technicianLifeTime") : 0;
        this.technicianUUID = nbt.contains("technicianUUID") ? nbt.getUuid("technicianUUID") : null;
    }
}