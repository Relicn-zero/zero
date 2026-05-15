package org.BsXinQin.kinswathe.component;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.BsXinQin.kinswathe.KinsWathe;
import org.BsXinQin.kinswathe.KinsWatheConfig;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

public class GameSafeComponent implements AutoSyncedComponent, ServerTickingComponent {

    public static final ComponentKey<GameSafeComponent> KEY = ComponentRegistry.getOrCreate(Identifier.of(KinsWathe.MOD_ID, "safe"), GameSafeComponent.class);

    @NotNull private final World world;
    public boolean isGameSafe = false;
    public int safeTicks = 0;

    public GameSafeComponent(@NotNull World world) {this.world = world;}

    @Override
    public void serverTick() {
        if (this.isGameSafe) {
            this.notInGameReset();
            if (this.world instanceof @NotNull ServerWorld) {
                if (this.safeTicks <= KinsWatheConfig.HANDLER.instance().StartingCooldown * 20) {
                    ++ this.safeTicks;
                    this.sync();
                    if (this.world instanceof @NotNull ServerWorld) {
                        if (this.safeTicks > KinsWatheConfig.HANDLER.instance().StartingCooldown * 20) {
                            this.reset();
                        }
                    }
                } else {
                    this.reset();
                }
            }
        }
    }

    public void notInGameReset() {
        if (!GameWorldComponent.KEY.get(this.world).isRunning()) {
            this.reset();
        }
    }

    public void startGameSafe() {
        this.reset();
        this.isGameSafe = true;
        this.sync();
    }

    public boolean isSafe() {
        return this.isGameSafe && this.safeTicks > 0;
    }

    public void reset() {
        this.isGameSafe = false;
        this.safeTicks = 0;
        this.sync();
    }

    public void sync() {
        KEY.sync(this.world);
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag, RegistryWrapper.@NotNull WrapperLookup registryLookup) {
        tag.putInt("safeTicks", this.safeTicks);
        tag.putBoolean("isGameSafe", this.isGameSafe);
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound tag, RegistryWrapper.@NotNull WrapperLookup registryLookup) {
        this.safeTicks = tag.contains("safeTicks") ? tag.getInt("safeTicks") : 0;
        this.isGameSafe = tag.contains("isGameSafe") && tag.getBoolean("isGameSafe");
    }
}