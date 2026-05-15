package org.BsXinQin.kinswathe.client.mixin.roles.arbiter;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.client.gui.screen.ingame.LimitedHandledScreen;
import dev.doctor4t.wathe.client.gui.screen.ingame.LimitedInventoryScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import org.BsXinQin.kinswathe.KinsWatheRoles;
import org.BsXinQin.kinswathe.client.roles.arbiter.ArbiterPlayerWidget;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mixin(LimitedInventoryScreen.class)
public abstract class ArbiterScreenMixin extends LimitedHandledScreen<PlayerScreenHandler> {

    @Shadow @NotNull public ClientPlayerEntity player;

    public ArbiterScreenMixin(@NotNull PlayerScreenHandler handler, @NotNull PlayerInventory inventory, @NotNull Text title) {
        super(handler, inventory, title);
    }

    @Inject(method = "init", at = @At("HEAD"))
    void renderArbiterHeads(CallbackInfo ci) {
        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(this.player.getWorld());
        if (gameWorld.isRole(this.player, KinsWatheRoles.ARBITER)) {
            List<UUID> players = new ArrayList<>(MinecraftClient.getInstance().player.networkHandler.getPlayerUuids());
            // 移除自己
            players.removeIf(uuid -> uuid.equals(this.player.getUuid()));

            int apart = 36;
            int x = this.width / 2 - (players.size()) * apart / 2 + 9;
            int shouldBeY = (this.height - 32) / 2;
            int y = shouldBeY + 80;
            for (int i = 0; i < players.size(); ++i) {
                ArbiterPlayerWidget widget = new ArbiterPlayerWidget(
                        x + apart * i,
                        y,
                        players.get(i),
                        MinecraftClient.getInstance().player.networkHandler.getPlayerListEntry(players.get(i))
                );
                addDrawableChild(widget);
            }
        }
    }
}