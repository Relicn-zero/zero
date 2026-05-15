package org.BsXinQin.kinswathe.component;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.BsXinQin.kinswathe.KinsWathe;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

import java.util.UUID;

public class ArbiterComponent implements AutoSyncedComponent, ServerTickingComponent {

    public static final ComponentKey<ArbiterComponent> KEY = ComponentRegistry.getOrCreate(
            Identifier.of(KinsWathe.MOD_ID, "arbiter"),
            ArbiterComponent.class
    );

    private final PlayerEntity player;
    private int verdictsLeft = -1;          // -1 表示未初始化，正数为剩余裁决次数
    private int deathTicks = 0;             // 死亡倒计时（tick）
    private UUID deathTargetUuid = null;    // 待裁决的目标玩家 UUID

    public ArbiterComponent(@NotNull PlayerEntity player) {
        this.player = player;
    }

    // ---------- 裁决次数相关 ----------
    public int getVerdictsLeft() {
        return verdictsLeft;
    }

    public void setVerdictsLeft(int uses) {
        this.verdictsLeft = uses;
        sync();
    }

    public void decrementVerdicts() {
        if (verdictsLeft > 0) {
            verdictsLeft--;
            sync();
        }
    }

    // ---------- 延迟死亡相关 ----------
    public int getDeathTicks() {
        return deathTicks;
    }

    public void setDeathTicks(int ticks) {
        this.deathTicks = ticks;
        sync();
    }

    public UUID getDeathTargetUuid() {
        return deathTargetUuid;
    }

    public void setDeathTargetUuid(UUID uuid) {
        this.deathTargetUuid = uuid;
        sync();
    }

    /**
     * 清除当前裁决（死亡倒计时中途取消）
     */
    public void clearDeathSentence() {
        this.deathTicks = 0;
        this.deathTargetUuid = null;
        sync();
    }

    // ---------- 重置所有数据 ----------
    public void reset() {
        this.verdictsLeft = -1;
        this.deathTicks = 0;
        this.deathTargetUuid = null;
        sync();
    }

    // ---------- 服务器 tick 逻辑 ----------
    @Override
    public void serverTick() {
        // 延迟死亡倒计时
        if (deathTicks > 0 && deathTargetUuid != null) {
            deathTicks--;
            if (deathTicks <= 0) {
                // 时间到，执行死亡
                ServerPlayerEntity target = player.getServer().getPlayerManager().getPlayer(deathTargetUuid);
                if (target != null && target.isAlive() && !target.isSpectator()) {
                    target.damage(target.getWorld().getDamageSources().magic(), Float.MAX_VALUE);
                    target.sendMessage(Text.translatable("tip.kinswathe.arbiter.verdict_executed").withColor(0xAA0000), false);
                }
                // 清除标记
                deathTargetUuid = null;
                sync();
            }
        }
    }

    // ---------- 数据同步 ----------
    public void sync() {
        KEY.sync(this.player);
    }

    // ---------- NBT 读写 ----------
    @Override
    public void writeToNbt(@NotNull NbtCompound tag, RegistryWrapper.@NotNull WrapperLookup registryLookup) {
        tag.putInt("verdictsLeft", verdictsLeft);
        tag.putInt("deathTicks", deathTicks);
        if (deathTargetUuid != null) {
            tag.putUuid("deathTargetUuid", deathTargetUuid);
        }
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound tag, RegistryWrapper.@NotNull WrapperLookup registryLookup) {
        this.verdictsLeft = tag.contains("verdictsLeft") ? tag.getInt("verdictsLeft") : -1;
        this.deathTicks = tag.contains("deathTicks") ? tag.getInt("deathTicks") : 0;
        this.deathTargetUuid = tag.containsUuid("deathTargetUuid") ? tag.getUuid("deathTargetUuid") : null;
    }
}
