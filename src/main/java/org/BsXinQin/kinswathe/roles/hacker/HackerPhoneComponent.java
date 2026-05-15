package org.BsXinQin.kinswathe.roles.hacker;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.client.util.WatheItemTooltips;
import dev.doctor4t.wathe.compat.TrainVoicePlugin;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.BsXinQin.kinswathe.KinsWathe;
import org.BsXinQin.kinswathe.KinsWatheItems;
import org.BsXinQin.kinswathe.KinsWatheRoles;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

import java.util.ArrayList;
import java.util.List;

public class HackerPhoneComponent implements AutoSyncedComponent, ServerTickingComponent {

    public static final ComponentKey<HackerPhoneComponent> KEY = ComponentRegistry.getOrCreate(Identifier.of(KinsWathe.MOD_ID, "hacker_phone"), HackerPhoneComponent.class);

    @NotNull private final PlayerEntity player;
    public boolean groupKiller = false;

    public HackerPhoneComponent(@NotNull PlayerEntity player) {this.player = player;}

    @Override
    public void serverTick() {
        if (this.groupKiller) {
            this.notInGameReset();
            this.quitKillerGroup();
        }
    }

    public void notInGameReset() {
        if (GameWorldComponent.KEY.get(this.player.getWorld()).getRole(this.player) == null) {
            this.reset();
        }
    }

    public void quitKillerGroup() {
        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(this.player.getWorld());
        if (!(gameWorld.canUseKillerFeatures(this.player) || gameWorld.isRole(this.player, KinsWatheRoles.HACKER))) {
            TrainVoicePlugin.resetPlayer(this.player.getUuid());
            this.reset();
        }
    }

    public ItemStack getPhone() {
        ItemStack stack = KinsWatheItems.PHONE.getDefaultStack();
        Text tooltipText = Text.translatable("item.kinswathe.phone.tooltip").setStyle(Style.EMPTY.withColor(WatheItemTooltips.REGULAR_TOOLTIP_COLOR).withItalic(false));
        List<Text> loreLines = new ArrayList<>();
        loreLines.add(tooltipText);
        stack.set(DataComponentTypes.LORE, new LoreComponent(loreLines));
        return stack;
    }

    public void reset() {
        this.groupKiller = false;
        this.sync();
    }

    public void sync() {
        KEY.sync(this.player);
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag, RegistryWrapper.@NotNull WrapperLookup registryLookup) {
        tag.putBoolean("groupKiller", this.groupKiller);
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound tag, RegistryWrapper.@NotNull WrapperLookup registryLookup) {
        this.groupKiller = tag.contains("groupKiller") && tag.getBoolean("groupKiller");
    }
}