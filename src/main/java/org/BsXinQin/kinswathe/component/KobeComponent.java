package org.BsXinQin.kinswathe.component;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.BsXinQin.kinswathe.KinsWathe;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

public class KobeComponent implements AutoSyncedComponent, ServerTickingComponent {
    public static final ComponentKey<KobeComponent> KEY = ComponentRegistry.getOrCreate(
            Identifier.of(KinsWathe.MOD_ID, "kobe"), KobeComponent.class
    );

    private final PlayerEntity player;
    public int speedTicks = 0;

    public KobeComponent(PlayerEntity player) {
        this.player = player;
    }

    @Override
    public void serverTick() {
        if (this.speedTicks > 0) {
            this.speedTicks--;
            if (player instanceof ServerPlayerEntity serverPlayer) {
                serverPlayer.getServerWorld().spawnParticles(
                        ParticleTypes.SWEEP_ATTACK,
                        serverPlayer.getX(), serverPlayer.getY() + 0.5, serverPlayer.getZ(),
                        1, 0.2, 0.2, 0.2, 0
                );
            }
            this.sync();
        }
    }

    public void setSpeedTicks(int ticks) {
        this.speedTicks = ticks;
        this.sync();
    }

    public void reset() {
        this.speedTicks = 0;
        this.sync();
    }

    public void sync() {
        KEY.sync(this.player);
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.putInt("speedTicks", this.speedTicks);
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        this.speedTicks = tag.getInt("speedTicks");
    }
}
