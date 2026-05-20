package org.BsXinQin.kinswathe.component;

import net.minecraft.entity.attribute.EntityAttributes;
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
    private static final float BASE_SPEED = 0.1f;
    private static final float SPEED_MULTIPLIER = 1.5f;

    public KobeComponent(PlayerEntity player) {
        this.player = player;
    }

    @Override
    public void serverTick() {
        if (this.speedTicks > 0) {
            this.speedTicks--;
            // 应用速度加成
            var instance = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
            if (instance != null && instance.getBaseValue() != BASE_SPEED * SPEED_MULTIPLIER) {
                instance.setBaseValue(BASE_SPEED * SPEED_MULTIPLIER);
            }
            // 特效
            if (player instanceof ServerPlayerEntity serverPlayer) {
                serverPlayer.getServerWorld().spawnParticles(
                        ParticleTypes.SWEEP_ATTACK,
                        serverPlayer.getX(), serverPlayer.getY() + 0.5, serverPlayer.getZ(),
                        1, 0.2, 0.2, 0.2, 0
                );
            }
            this.sync();
        } else {
            // 恢复原始速度
            var instance = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
            if (instance != null && instance.getBaseValue() != BASE_SPEED) {
                instance.setBaseValue(BASE_SPEED);
            }
        }
    }

    public void setSpeedTicks(int ticks) {
        this.speedTicks = ticks;
        if (ticks <= 0) {
            // 立即恢复速度
            var instance = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
            if (instance != null) instance.setBaseValue(BASE_SPEED);
        }
        this.sync();
    }

    public void reset() {
        this.setSpeedTicks(0);
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
        if (this.speedTicks > 0) {
            // 重读后重新应用速度
            var instance = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
            if (instance != null) instance.setBaseValue(BASE_SPEED * SPEED_MULTIPLIER);
        }
    }
}
