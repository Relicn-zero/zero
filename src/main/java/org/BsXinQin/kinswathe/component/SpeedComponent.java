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
    private int remainingTicks = 0;  // 剩余时间（tick），0 表示永久

    public SpeedComponent(PlayerEntity player) { this.player = player; }

    public void setMultiplier(float mult, int durationTicks) {
        this.multiplier = mult;
        this.remainingTicks = durationTicks;
        sync();
    }

    public float getMultiplier() { return multiplier; }

    @Override
    public void serverTick() {
        if (remainingTicks > 0) {
            remainingTicks--;
            if (remainingTicks <= 0) {
                multiplier = 1.0f;
                sync();
            }
        }
    }

    public void sync() { KEY.sync(player); }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup lookup) {
        tag.putFloat("multiplier", multiplier);
        tag.putInt("remainingTicks", remainingTicks);
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup lookup) {
        this.multiplier = tag.getFloat("multiplier");
        this.remainingTicks = tag.getInt("remainingTicks");
    }
}
