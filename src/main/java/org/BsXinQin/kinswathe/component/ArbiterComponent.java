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
            Identifier.of(KinsWathe.MOD_ID, "arbiter"), ArbiterComponent.class);

    private final PlayerEntity player;
    private int verdictsLeft = -1;
    private int deathTicks = 0;
    private UUID deathTargetUuid = null;

    public ArbiterComponent(@NotNull PlayerEntity player) { this.player = player; }

    public int getVerdictsLeft() { return verdictsLeft; }
    public void setVerdictsLeft(int uses) { this.verdictsLeft = uses; sync(); }
    public void decrementVerdicts() { if (verdictsLeft > 0) verdictsLeft--; sync(); }

    public int getDeathTicks() { return deathTicks; }
    public void setDeathTicks(int ticks) { this.deathTicks = ticks; sync(); }
    public UUID getDeathTargetUuid() { return deathTargetUuid; }
    public void setDeathTargetUuid(UUID uuid) { this.deathTargetUuid = uuid; sync(); }

    public void reset() {
        this.verdictsLeft = -1;
        this.deathTicks = 0;
        this.deathTargetUuid = null;
        sync();
    }

    @Override
    public void serverTick() {
        if (deathTicks > 0 && deathTargetUuid != null) {
            deathTicks--;
            if (deathTicks <= 0) {
                ServerPlayerEntity target = player.getServer().getPlayerManager().getPlayer(deathTargetUuid);
                if (target != null && target.isAlive() && !target.isSpectator()) {
                    target.damage(target.getWorld().getDamageSources().magic(), Float.MAX_VALUE);
                    target.sendMessage(Text.translatable("tip.kinswathe.arbiter.verdict_executed").withColor(0xAA0000), false);
                }
                deathTargetUuid = null;
                sync();
            }
        }
    }

    public void sync() { KEY.sync(this.player); }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup lookup) {
        tag.putInt("verdictsLeft", verdictsLeft);
        tag.putInt("deathTicks", deathTicks);
        if (deathTargetUuid != null) tag.putUuid("deathTargetUuid", deathTargetUuid);
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup lookup) {
        this.verdictsLeft = tag.contains("verdictsLeft") ? tag.getInt("verdictsLeft") : -1;
        this.deathTicks = tag.contains("deathTicks") ? tag.getInt("deathTicks") : 0;
        this.deathTargetUuid = tag.containsUuid("deathTargetUuid") ? tag.getUuid("deathTargetUuid") : null;
    }
}
