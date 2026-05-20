package org.BsXinQin.kinswathe.component;

// ... (必要的 import 语句)
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;
// ...

public class KobeComponent implements AutoSyncedComponent, ServerTickingComponent {
    public static final ComponentKey<KobeComponent> KEY = ComponentRegistry.getOrCreate(
        Identifier.of(KinsWathe.MOD_ID, "kobe"), KobeComponent.class
    );
    private final PlayerEntity player;
    public int speedTicks = 0; // 用于技能速度的倒计时

    public KobeComponent(PlayerEntity player) {
        this.player = player;
    }

    @Override
    public void serverTick() {
        if (this.speedTicks > 0) {
            this.speedTicks--; // 每 tick 减 1
            if (player instanceof ServerPlayerEntity serverPlayer) {
                // 技能激活时持续产生特效
                serverPlayer.getServerWorld().spawnParticles(
                    KinsWathe.SWEEP_ATTACK, // 你可以替换成你想要的粒子效果
                    serverPlayer.getX(), serverPlayer.getY() + 0.5, serverPlayer.getZ(),
                    1, 0.2, 0.2, 0.2, 0
                );
            }
            this.sync(); // 同步数据到客户端
        }
    }

    // 辅助方法
    public void setSpeedTicks(int ticks) { this.speedTicks = ticks; this.sync(); }
    public void reset() { this.speedTicks = 0; this.sync(); }
    public void sync() { KEY.sync(this.player); }

    // writeToNbt / readFromNbt 方法保持不变
}
