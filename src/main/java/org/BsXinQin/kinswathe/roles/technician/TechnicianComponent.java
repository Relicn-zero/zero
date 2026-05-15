package org.BsXinQin.kinswathe.roles.technician;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.WorldBlackoutComponent;
import dev.doctor4t.wathe.game.GameConstants;
import dev.doctor4t.wathe.index.WatheSounds;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import org.BsXinQin.kinswathe.KinsWathe;
import org.BsXinQin.kinswathe.KinsWatheItems;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

public class TechnicianComponent implements AutoSyncedComponent, ServerTickingComponent {

    public static final ComponentKey<TechnicianComponent> KEY = ComponentRegistry.getOrCreate(Identifier.of(KinsWathe.MOD_ID, "technician"), TechnicianComponent.class);

    @NotNull private final PlayerEntity player;
    public int technicianTicks = 0;
    public int blackoutTicks = 0;

    public TechnicianComponent(@NotNull PlayerEntity player) {this.player = player;}

    @Override
    public void serverTick() {
        if (this.technicianTicks > 0) {
            this.notInGameReset();
            -- this.technicianTicks;
            this.sync();
        }
        if (this.blackoutTicks > 0) {
            this.notInGameReset();
            -- this.blackoutTicks;
            this.sync();
        }
    }

    public void notInGameReset() {
        if (GameWorldComponent.KEY.get(this.player.getWorld()).getRole(this.player) == null) {
            this.reset();
        }
    }

    public void setBlackoutTicks(int ticks) {
        this.blackoutTicks = ticks;
        this.sync();
    }

    public void setCapturedTicks(int ticks) {
        this.technicianTicks = ticks;
        this.sync();
    }

    public static void stopBlackout(@NotNull PlayerEntity player) {
        player.getItemCooldownManager().set(KinsWatheItems.ICON_POWER_RESTORATION, GameConstants.ITEM_COOLDOWNS.get(KinsWatheItems.ICON_POWER_RESTORATION));
        WorldBlackoutComponent.KEY.get(player.getWorld()).reset();
        for (ServerPlayerEntity serverPlayer : player.getWorld().getServer().getPlayerManager().getPlayerList()) {
            if (serverPlayer == null) continue;
            TechnicianComponent playerBlackout = TechnicianComponent.KEY.get(serverPlayer);
            serverPlayer.playSoundToPlayer(WatheSounds.BLOCK_LIGHT_TOGGLE, SoundCategory.PLAYERS, 1.0F, 1.0F);
            playerBlackout.blackoutTicks = 0;
            playerBlackout.sync();
        }
    }

    public void reset() {
        this.technicianTicks = 0;
        this.blackoutTicks = 0;
    }

    public void sync() {
        KEY.sync(this.player);
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag, RegistryWrapper.@NotNull WrapperLookup registryLookup) {
        tag.putInt("technicianTicks", this.technicianTicks);
        tag.putInt("blackoutTicks", this.blackoutTicks);
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound tag, RegistryWrapper.@NotNull WrapperLookup registryLookup) {
        this.technicianTicks = tag.contains("technicianTicks") ? tag.getInt("technicianTicks") : 0;
        this.blackoutTicks = tag.contains("blackoutTicks") ? tag.getInt("blackoutTicks") : 0;
    }
}