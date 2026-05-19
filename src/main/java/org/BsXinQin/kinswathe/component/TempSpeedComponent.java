package org.BsXinQin.kinswathe.component;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import org.BsXinQin.kinswathe.KinsWathe;
import org.jetbrains.annotations.NotNull;

public class TempSpeedComponent implements ComponentV3, AutoSyncedComponent {
    public static final ComponentKey<TempSpeedComponent> KEY =
            ComponentRegistry.getOrCreate(Identifier.of(KinsWathe.MOD_ID, "temp_speed"), TempSpeedComponent.class);

    private final PlayerEntity player;
    private int remainingTicks = 0;
    private float speedMultiplier = 1.0f;
    private double originalSpeed = 0.1; // 原版玩家基础速度是0.1

    public TempSpeedComponent(PlayerEntity player) {
        this.player = player;
    }

    /**
     * 激活临时速度加成
     * @param durationTicks 持续时间（刻）
     * @param multiplier 速度倍率（例如 1.5 表示增加 50% 速度）
     */
    public void activate(int durationTicks, float multiplier) {
        this.remainingTicks = durationTicks;
        this.speedMultiplier = multiplier;
        applySpeedModifier();
        KEY.sync(player);
    }

    /**
     * 清除当前速度加成
     */
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

    /**
     * 每 tick 由 Mixin 调用，用于减少持续时间
     */
    public void tick() {
        if (remainingTicks > 0) {
            remainingTicks--;
            if (remainingTicks == 0) {
                clear();
            }
        }
    }

    /**
     * 将速度倍率应用到玩家属性上
     */
    private void applySpeedModifier() {
        var instance = player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
        if (instance != null) {
            originalSpeed = instance.getBaseValue();
            instance.setBaseValue(originalSpeed * speedMultiplier);
        }
    }

    /**
     * 移除速度加成，恢复原始速度
     */
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
