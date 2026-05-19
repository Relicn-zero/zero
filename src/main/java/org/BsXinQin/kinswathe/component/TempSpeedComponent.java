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

public class TempSpeedComponent implements AutoSyncedComponent, ServerTickingComponent {
    public static final ComponentKey<TempSpeedComponent> KEY = ComponentRegistry.getOrCreate(
            Identifier.of(KinsWathe.MOD_ID, "temp_speed"), TempSpeedComponent.class);

    private final PlayerEntity player;
    private int remainingTicks = 0;
    private float speedMultiplier = 1.0f;
    private double originalSpeed = 0.1;

    public TempSpeedComponent(PlayerEntity player) {
        this.player = player;
    }

    public void activate(int durationTicks, float multiplier) {
        this.remainingTicks = durationTicks;
        this.speedMultiplier = multiplier;
        applySpeedModifier();
        KEY.sync(player);
    }

    public void clear() {
        this.remainingTicks = 0;
        this.speedMultiplier = 1.0f;
        removeSpeedModifier();
        KEY.sync(player);
    }

    public boolean isActive() {
        return remainingTicks > 0;
    }

    public int getRemainingTicks() {
        return remainingTicks;
    }

    @Override
    public void serverTick() {
        if (remainingTicks > 0) {
            remainingTicks--;
            if (remainingTicks == 0) {
                clear();
            }
        }
    }

    private void applySpeedModifier() {
        var instance = player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
        if (instance != null) {
            originalSpeed = instance.getBaseValue();
            instance.setBaseValue(originalSpeed * speedMultiplier);
        }
    }

    private void removeSpeedModifier() {
        var instance = player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
        if (instance != null) {
            instance.setBaseValue(originalSpeed);
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.putInt("remainingTicks", remainingTicks);
        tag.putFloat("speedMultiplier", speedMultiplier);
        tag.putDouble("originalSpeed", originalSpeed);
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        this.remainingTicks = tag.getInt("remainingTicks");
        this.speedMultiplier = tag.getFloat("speedMultiplier");
        this.originalSpeed = tag.getDouble("originalSpeed");
        if (remainingTicks > 0) {
            applySpeedModifier();
        }
    }
}
