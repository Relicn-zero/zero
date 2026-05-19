package org.BsXinQin.kinswathe.component;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import org.BsXinQin.kinswathe.KinsWathe;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

public class SpeedComponent implements AutoSyncedComponent, ServerTickingComponent {
    public static final ComponentKey<SpeedComponent> KEY = ComponentRegistry.getOrCreate(
            Identifier.of(KinsWathe.MOD_ID, "speed"), SpeedComponent.class);

    private final PlayerEntity player;
    private float multiplier = 1.0f;
    private int remainingTicks = 0;
    private boolean useCustomSpeed = false;
    private float customWalkSpeed = -1.0f;
    private float customSprintSpeed = -1.0f;

    public SpeedComponent(PlayerEntity player) { this.player = player; }

    public void setPermanentMultiplier(float mult) {
        this.multiplier = mult;
        this.remainingTicks = 0;
        this.useCustomSpeed = false;
        sync();
    }

    public void setTemporaryMultiplier(float mult, int durationTicks) {
        this.multiplier = mult;
        this.remainingTicks = durationTicks;
        this.useCustomSpeed = false;
        sync();
    }

    public void setCustomSpeed(float walkSpeed, float sprintSpeed, int durationTicks) {
        this.useCustomSpeed = true;
        this.customWalkSpeed = walkSpeed;
        this.customSprintSpeed = sprintSpeed;
        this.remainingTicks = durationTicks;
        this.multiplier = 1.0f;
        sync();
    }

    public boolean hasCustomSpeed() { return useCustomSpeed; }
    public float getCustomWalkSpeed() { return customWalkSpeed; }
    public float getCustomSprintSpeed() { return customSprintSpeed; }
    public float getMultiplier() { return multiplier; }

    @Override
    public void serverTick() {
        if (remainingTicks > 0) {
            remainingTicks--;
            if (remainingTicks <= 0) {
                useCustomSpeed = false;
                multiplier = 1.0f;
                customWalkSpeed = -1.0f;
                customSprintSpeed = -1.0f;
                sync();
            }
        }
    }

    public void sync() { KEY.sync(player); }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup lookup) {
        tag.putFloat("multiplier", multiplier);
        tag.putInt("remainingTicks", remainingTicks);
        tag.putBoolean("useCustomSpeed", useCustomSpeed);
        if (useCustomSpeed) {
            tag.putFloat("customWalkSpeed", customWalkSpeed);
            tag.putFloat("customSprintSpeed", customSprintSpeed);
        }
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup lookup) {
        this.multiplier = tag.getFloat("multiplier");
        this.remainingTicks = tag.getInt("remainingTicks");
        this.useCustomSpeed = tag.getBoolean("useCustomSpeed");
        if (this.useCustomSpeed) {
            this.customWalkSpeed = tag.getFloat("customWalkSpeed");
            this.customSprintSpeed = tag.getFloat("customSprintSpeed");
        }
    }
}
