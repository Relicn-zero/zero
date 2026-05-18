package org.BsXinQin.kinswathe.component;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.BsXinQin.kinswathe.KinsWathe;
import org.BsXinQin.kinswathe.KinsWatheConfig;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

import java.util.UUID;

public class JudgeLordComponent implements AutoSyncedComponent, ServerTickingComponent {

    public static final ComponentKey<JudgeLordComponent> KEY = ComponentRegistry.getOrCreate(
        Identifier.of(KinsWathe.MOD_ID, "judgelord"),
        JudgeLordComponent.class
    );

    private final PlayerEntity player;
    private int remainingUses = -1;          // -1 表示未初始化，正数为剩余次数
    private UUID monitoredTarget = null;     // 被监控的目标 UUID
    private int monitorTicks = 0;            // 剩余监控 tick 数
    private boolean hasKilled = false;       // 监控期间目标是否杀过人

    public JudgeLordComponent(@NotNull PlayerEntity player) {
        this.player = player;
    }

    public int getRemainingUses() {
        if (remainingUses == -1) {
            // 首次获取时从配置加载最大次数
            remainingUses = KinsWatheConfig.HANDLER.instance().JudgeLordMaxUses;
            sync();
        }
        return remainingUses;
    }

    public void decrementRemainingUses() {
        if (remainingUses > 0) {
            remainingUses--;
            sync();
        }
    }

    public void reset() {
        remainingUses = -1;          // 下次获取时重新从配置加载
        stopMonitoring();
    }

    /**
     * 开始监控目标
     */
    public void startMonitoring(UUID targetUuid, int durationTicks) {
        this.monitoredTarget = targetUuid;
        this.monitorTicks = durationTicks;
        this.hasKilled = false;
        sync();
    }

    /**
     * 通知组件：目标杀人了
     */
    public void reportKill() {
        if (monitoredTarget != null && monitorTicks > 0 && !hasKilled) {
            hasKilled = true;
            // 立即执行死亡裁决
            ServerPlayerEntity target = player.getServer().getPlayerManager().getPlayer(monitoredTarget);
            if (target != null && target.isAlive() && !target.isSpectator()) {
                target.damage(target.getWorld().getDamageSources().magic(), Float.MAX_VALUE);
                target.sendMessage(Text.translatable("tip.kinswathe.judgelord.verdict_executed").withColor(0xAA0000), false);
                // 全服通告可选
            }
            stopMonitoring();
        }
    }

/**
 * 外部调用：通知组件某个玩家（killerUuid）杀人了。
 * 如果当前正在监控的目标正是这个 killer，且监控尚未结束，则立即执行裁决。
 */
public void reportKillIfTargetMatches(UUID killerUuid) {
    if (monitoredTarget != null && monitoredTarget.equals(killerUuid) && monitorTicks > 0 && !hasKilled) {
        reportKill();   // 会立即杀死目标并清除监控
    }
}
    
    private void stopMonitoring() {
        this.monitoredTarget = null;
        this.monitorTicks = 0;
        this.hasKilled = false;
        sync();
    }

    @Override
    public void serverTick() {
        if (monitoredTarget != null && monitorTicks > 0) {
            monitorTicks--;
            if (monitorTicks <= 0) {
                // 监控结束，没有杀人，无效果
                stopMonitoring();
            }
        }
    }

    public void sync() {
        KEY.sync(player);
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag, RegistryWrapper.@NotNull WrapperLookup registryLookup) {
        tag.putInt("remainingUses", remainingUses);
        if (monitoredTarget != null) {
            tag.putUuid("monitoredTarget", monitoredTarget);
            tag.putInt("monitorTicks", monitorTicks);
            tag.putBoolean("hasKilled", hasKilled);
        }
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound tag, RegistryWrapper.@NotNull WrapperLookup registryLookup) {
        this.remainingUses = tag.getInt("remainingUses");
        if (tag.containsUuid("monitoredTarget")) {
            this.monitoredTarget = tag.getUuid("monitoredTarget");
            this.monitorTicks = tag.getInt("monitorTicks");
            this.hasKilled = tag.getBoolean("hasKilled");
        }
    }
}
