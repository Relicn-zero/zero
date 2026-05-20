package org.BsXinQin.kinswathe.component;

import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import org.BsXinQin.kinswathe.KinsWathe;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

public class BellringerComponent implements AutoSyncedComponent, ServerTickingComponent {
    public static final ComponentKey<BellringerComponent> KEY = ComponentRegistry.getOrCreate(
            Identifier.of(KinsWathe.MOD_ID, "bellringer"), BellringerComponent.class
    );

    private final PlayerEntity player;
    public int speedTicks = 0;
    private static final float BASE_SPEED = 0.1f;
    private static final float SPEED_MULTIPLIER = 1.3f;

    public BellringerComponent(PlayerEntity player) {
        this.player = player;
    }

    @Override
    public void serverTick() {
        if (this.speedTicks > 0) {
            this.speedTicks--;
            var instance = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
            if (instance != null && instance.getBaseValue() != BASE_SPEED * SPEED_MULTIPLIER) {
                instance.setBaseValue(BASE_SPEED * SPEED_MULTIPLIER);
            }
            this.sync();
        } else {
            var instance = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
            if (instance != null && instance.getBaseValue() != BASE_SPEED) {
                instance.setBaseValue(BASE_SPEED);
            }
        }
    }

    public void setSpeedTicks(int ticks) {
        this.speedTicks = ticks;
        if (ticks <= 0) {
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
            var instance = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
            if (instance != null) instance.setBaseValue(BASE_SPEED * SPEED_MULTIPLIER);
        }
    }
}
