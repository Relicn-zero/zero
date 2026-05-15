package org.BsXinQin.kinswathe.client.mixin.roles.bodymaker;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.client.gui.screen.ingame.LimitedHandledScreen;
import dev.doctor4t.wathe.client.gui.screen.ingame.LimitedInventoryScreen;
import dev.doctor4t.wathe.index.WatheItems;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.BsXinQin.kinswathe.KinsWatheRoles;
import org.BsXinQin.kinswathe.client.roles.bodymaker.BodymakerDeathReasonWidget;
import org.BsXinQin.kinswathe.client.roles.bodymaker.BodymakerPlayerWidget;
import org.BsXinQin.kinswathe.client.roles.bodymaker.BodymakerRoleWidget;
import org.BsXinQin.kinswathe.client.roles.bodymaker.BodymakerScreenCallback;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mixin(LimitedInventoryScreen.class)
public abstract class BodymakerScreenMixin extends LimitedHandledScreen<PlayerScreenHandler> implements BodymakerScreenCallback {

    @Shadow @Final public ClientPlayerEntity player;
    @Unique private int selectedLevel = 0;
    @Unique private UUID selectedPlayerUuid = null;
    @Unique private String selectedDeathReason = null;

    public BodymakerScreenMixin(@NotNull PlayerScreenHandler handler, @NotNull PlayerInventory inventory, @NotNull Text title) {super(handler, inventory, title);}

    @Inject(method = "init", at = @At("HEAD"))
    void renderBodymakerHeads(CallbackInfo ci) {
        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(this.player.getWorld());
        if (gameWorld.isRole(this.player, KinsWatheRoles.BODYMAKER)) {
            int apart = 36;
            int shouldBeY = (this.height - 32) / 2;
            int y = shouldBeY + 80;
            if (selectedLevel == 0) {
                List<UUID> players = new ArrayList<>(MinecraftClient.getInstance().player.networkHandler.getPlayerUuids());
                int x = this.width / 2 - (players.size()) * apart / 2 + 9;
                for (int i = 0; i < players.size(); ++i) {
                    BodymakerPlayerWidget widget = new BodymakerPlayerWidget(((LimitedInventoryScreen) (Object) this), x + apart * i, y, players.get(i), MinecraftClient.getInstance().player.networkHandler.getPlayerListEntry(players.get(i)), this);
                    addDrawableChild(widget);
                }
            }
            else if (selectedLevel == 1) {
                List<Item> deathReasons = new ArrayList<>();
                deathReasons.add(WatheItems.KNIFE);
                deathReasons.add(WatheItems.REVOLVER);
                deathReasons.add(WatheItems.GRENADE);
                deathReasons.add(WatheItems.BAT);
                deathReasons.add(WatheItems.POISON_VIAL);
                if (FabricLoader.getInstance().isModLoaded("noellesroles")) {
                    deathReasons.add(Items.OMINOUS_BOTTLE);
                }
                if (FabricLoader.getInstance().isModLoaded("starexpress")) {
                    deathReasons.add(Registries.ITEM.get(Identifier.of("starexpress", "tape")));
                }
                if (FabricLoader.getInstance().isModLoaded("stupid_express")) {
                    deathReasons.add(Registries.ITEM.get(Identifier.of("stupid_express", "lighter")));
                }

                int x = this.width / 2 - (deathReasons.size()) * apart / 2 + 9;
                for(int i = 0; i < deathReasons.size(); ++i) {
                    BodymakerDeathReasonWidget widget = new BodymakerDeathReasonWidget(((LimitedInventoryScreen) (Object) this), x + apart * i, y, deathReasons.get(i), i, selectedPlayerUuid, this);
                    addDrawableChild(widget);
                }
            }
            else if (selectedLevel == 2) {
                int x = this.width / 2 - 100;
                BodymakerRoleWidget widget = new BodymakerRoleWidget(((LimitedInventoryScreen) (Object) this), textRenderer, x, y, selectedPlayerUuid, selectedDeathReason);
                addDrawableChild(widget);
                widget.setFocused(true);
            }
        }
    }

    @Override
    @Unique
    public void setSelectedPlayer(@NotNull UUID uuid) {
        this.selectedPlayerUuid = uuid;
        this.selectedLevel = 1;
        this.clearChildren();
        this.init();
    }

    @Unique
    @Override
    public void setSelectedDeathReason(@NotNull String deathReason) {
        this.selectedDeathReason = deathReason;
        this.selectedLevel = 2;
        this.clearChildren();
        this.init();
    }
}