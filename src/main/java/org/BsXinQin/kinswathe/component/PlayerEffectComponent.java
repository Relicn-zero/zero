package org.BsXinQin.kinswathe.component;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
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

public class PlayerEffectComponent implements AutoSyncedComponent, ServerTickingComponent {

    public static final ComponentKey<PlayerEffectComponent> KEY = ComponentRegistry.getOrCreate(Identifier.of(KinsWathe.MOD_ID, "effect"), PlayerEffectComponent.class);

    @NotNull private final PlayerEntity player;
    public int stunTicks = 0;

    public PlayerEffectComponent(@NotNull PlayerEntity player) {this.player = player;}

    @Override
    public void serverTick() {
        if (this.stunTicks > 0) {
            -- this.stunTicks;
            this.sync();
        }
    }

    public void setStunTicks(int ticks) {
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, ticks, 5, false, true, true));
        this.stunTicks = ticks;
        this.sync();
    }

    public void reset() {
        this.stunTicks = 0;
        this.sync();
    }

    public void sync() {
        KEY.sync(this.player);
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag, RegistryWrapper.@NotNull WrapperLookup registryLookup) {
        tag.putInt("stunTicks", this.stunTicks);
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound tag, RegistryWrapper.@NotNull WrapperLookup registryLookup) {
        this.stunTicks = tag.contains("stunTicks") ? tag.getInt("stunTicks") : 0;
    }
}