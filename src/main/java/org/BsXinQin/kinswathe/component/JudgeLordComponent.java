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

public class JudgeLordComponent implements AutoSyncedComponent, ServerTickingComponent {

    public static final ComponentKey<JudgeLordComponent> KEY = ComponentRegistry.getOrCreate(
        Identifier.of(KinsWathe.MOD_ID, "judgelord"),
        JudgeLordComponent.class
    );

    private final PlayerEntity player;
    private UUID monitoredTarget = null;
    private int monitorTicks = 0;
    private boolean hasKilled = false;

    public JudgeLordComponent(@NotNull PlayerEntity player) {
        this.player = player;
    }

    // 无次数限制，永远返回可用
    public int getRemainingUses() {
        return 999;
    }

    public void decrementRemainingUses() {
        // 无操作
    }

    public void startMonitoring(UUID targetUuid, int durationTicks) {
        this.monitoredTarget = targetUuid;
        this.monitorTicks = durationTicks;
        this.hasKilled = false;
        sync();
    }

    public void reportKill() {
        if (monitoredTarget != null && monitorTicks > 0 && !hasKilled) {
            hasKilled = true;
            ServerPlayerEntity target = player.getServer().getPlayerManager().getPlayer(monitoredTarget);
            if (target != null && target.isAlive() && !target.isSpectator()) {
                target.damage(target.getWorld().getDamageSources().magic(), Float.MAX_VALUE);
                target.sendMessage(Text.translatable("tip.kinswathe.judgelord.verdict_executed").withColor(0xAA0000), false);
            }
            stopMonitoring();
        }
    }

    public void reportKillIfTargetMatches(UUID killerUuid) {
        if (monitoredTarget != null && monitoredTarget.equals(killerUuid) && monitorTicks > 0 && !hasKilled) {
            reportKill();
        }
    }

    private void stopMonitoring() {
        this.monitoredTarget = null;
        this.monitorTicks = 0;
        this.hasKilled = false;
        sync();
    }

    public void reset() {
        stopMonitoring();
    }

    @Override
    public void serverTick() {
        if (monitoredTarget != null && monitorTicks > 0) {
            monitorTicks--;
            if (monitorTicks <= 0) {
                stopMonitoring();
            }
        }
    }

    public void sync() {
        KEY.sync(player);
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag, RegistryWrapper.@NotNull WrapperLookup registryLookup) {
        if (monitoredTarget != null) {
            tag.putUuid("monitoredTarget", monitoredTarget);
            tag.putInt("monitorTicks", monitorTicks);
            tag.putBoolean("hasKilled", hasKilled);
        }
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound tag, RegistryWrapper.@NotNull WrapperLookup registryLookup) {
        if (tag.containsUuid("monitoredTarget")) {
            this.monitoredTarget = tag.getUuid("monitoredTarget");
            this.monitorTicks = tag.getInt("monitorTicks");
            this.hasKilled = tag.getBoolean("hasKilled");
        }
    }
}
