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
    private float permanentMultiplier = 1.0f;
    private float temporaryMultiplier = 1.0f;
    private int temporaryTicksLeft = 0;

    public SpeedComponent(PlayerEntity player) {
        this.player = player;
    }

    // 永久速度（持续整局）
    public void setPermanentMultiplier(float multiplier) {
        this.permanentMultiplier = multiplier;
        KEY.sync(player);
    }

    public float getPermanentMultiplier() {
        return permanentMultiplier;
    }

    // 临时速度（持续一段时间后自动清除）
    public void setTemporaryMultiplier(float multiplier, int durationTicks) {
        this.temporaryMultiplier = multiplier;
        this.temporaryTicksLeft = durationTicks;
        KEY.sync(player);
    }

    public void clearTemporaryMultiplier() {
        this.temporaryMultiplier = 1.0f;
        this.temporaryTicksLeft = 0;
        KEY.sync(player);
    }

    // 获取最终速度倍率（永久 × 临时）
    public float getFinalMultiplier() {
        return permanentMultiplier * temporaryMultiplier;
    }

    @Override
    public void serverTick() {
        if (temporaryTicksLeft > 0) {
            temporaryTicksLeft--;
            if (temporaryTicksLeft <= 0) {
                clearTemporaryMultiplier();
            }
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.putFloat("permanentMultiplier", permanentMultiplier);
        tag.putFloat("temporaryMultiplier", temporaryMultiplier);
        tag.putInt("temporaryTicksLeft", temporaryTicksLeft);
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        this.permanentMultiplier = tag.getFloat("permanentMultiplier");
        this.temporaryMultiplier = tag.getFloat("temporaryMultiplier");
        this.temporaryTicksLeft = tag.getInt("temporaryTicksLeft");
    }
}
