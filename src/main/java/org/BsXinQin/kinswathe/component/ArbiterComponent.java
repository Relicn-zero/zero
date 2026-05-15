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

public class ArbiterComponent implements AutoSyncedComponent {

    public static final ComponentKey<ArbiterComponent> KEY = ComponentRegistry.getOrCreate(
            Identifier.of(KinsWathe.MOD_ID, "arbiter"),
            ArbiterComponent.class
    );

    private final PlayerEntity player;
    private int verdictsLeft = -1;

    public ArbiterComponent(@NotNull PlayerEntity player) {
        this.player = player;
    }

    public int getVerdictsLeft() { return verdictsLeft; }
    public void setVerdictsLeft(int uses) { this.verdictsLeft = uses; }
    public void decrementVerdicts() { if (verdictsLeft > 0) verdictsLeft--; }
    public void reset() { this.verdictsLeft = -1; this.sync(); }
    public void sync() { KEY.sync(this.player); }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag, RegistryWrapper.@NotNull WrapperLookup registryLookup) {
        tag.putInt("verdictsLeft", verdictsLeft);
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound tag, RegistryWrapper.@NotNull WrapperLookup registryLookup) {
        this.verdictsLeft = tag.getInt("verdictsLeft");
    }
}