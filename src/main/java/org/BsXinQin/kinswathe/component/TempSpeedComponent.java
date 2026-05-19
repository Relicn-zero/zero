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

public class TempSpeedComponent implements AutoSyncedComponent, ServerTickingComponent {
    public static final ComponentKey<TempSpeedComponent> KEY = ComponentRegistry.getOrCreate(
            Identifier.of(KinsWathe.MOD_ID, "temp_speed"), TempSpeedComponent.class);

    private final PlayerEntity player;
    private int activeTicks = 0;   // 剩余激活时间（tick），0 表示未激活

    public TempSpeedComponent(PlayerEntity player) { this.player = player; }

    public void activate(int ticks) { this.activeTicks = ticks; sync(); }
    public boolean isActive() { return activeTicks > 0; }

    @Override
    public void serverTick() {
        if (activeTicks > 0) {
            activeTicks--;
            if (activeTicks <= 0) sync();
        }
    }

    public void sync() { KEY.sync(player); }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup lookup) {
        tag.putInt("activeTicks", activeTicks);
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup lookup) {
        activeTicks = tag.getInt("activeTicks");
    }
}
