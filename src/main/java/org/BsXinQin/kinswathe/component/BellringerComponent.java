package org.BsXinQin.kinswathe.component;

// ... (必要的 import 语句)
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;
// ...

public class BellringerComponent implements AutoSyncedComponent, ServerTickingComponent {
    public static final ComponentKey<BellringerComponent> KEY = ComponentRegistry.getOrCreate(
        Identifier.of(KinsWathe.MOD_ID, "bellringer"), BellringerComponent.class
    );
    private final PlayerEntity player;
    public int speedTicks = 0; // 用于技能速度的倒计时

    public BellringerComponent(PlayerEntity player) {
        this.player = player;
    }

    @Override
    public void serverTick() {
        if (this.speedTicks > 0) {
            this.speedTicks--;
            // 可以在这里添加类似于追星族的粒子特效，使用 "kinswathe:bell_sparkle"
            this.sync();
        }
    }

    // 辅助方法
    public void setSpeedTicks(int ticks) { this.speedTicks = ticks; this.sync(); }
    public void reset() { this.speedTicks = 0; this.sync(); }
    public void sync() { KEY.sync(this.player); }

    // writeToNbt / readFromNbt 方法保持不变
}
