package org.BsXinQin.kinswathe.client.roles.bodymaker;

import dev.doctor4t.wathe.client.gui.screen.ingame.LimitedInventoryScreen;
import dev.doctor4t.wathe.index.WatheItems;
import dev.doctor4t.wathe.util.ShopEntry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.BsXinQin.kinswathe.component.ConfigWorldComponent;
import org.BsXinQin.kinswathe.packet.roles.BodymakerC2SPacket;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class BodymakerDeathReasonWidget extends ButtonWidget {

    public final LimitedInventoryScreen screen;
    public final ItemStack deathReasonStack;
    public final UUID targetPlayerUuid;
    public final BodymakerScreenCallback callback;
    public final String deathReasonId;

    public BodymakerDeathReasonWidget(@NotNull LimitedInventoryScreen screen, int x, int y, @NotNull Item deathReason, int index, @NotNull UUID targetPlayerUuid, @NotNull BodymakerScreenCallback callback) {
        super(x, y, 16, 16, Text.literal(""), (button) -> {
            if (MinecraftClient.getInstance().player == null) return;
            if (FabricLoader.getInstance().isModLoaded("noellesroles") && ConfigWorldComponent.KEY.get(MinecraftClient.getInstance().player.getWorld()).BodymakerAbilityFakeRole) {
                callback.setSelectedDeathReason(getDeathReasonId(deathReason));
            } else {
                ClientPlayNetworking.send(new BodymakerC2SPacket(targetPlayerUuid, getDeathReasonId(deathReason), ""));
                screen.close();
            }
        }, DEFAULT_NARRATION_SUPPLIER);
        this.screen = screen;
        this.deathReasonStack = deathReason.getDefaultStack();
        this.targetPlayerUuid = targetPlayerUuid;
        this.callback = callback;
        this.deathReasonId = getDeathReasonId(deathReason);
    }

    private static String getDeathReasonId(@NotNull Item item) {
        if (item == WatheItems.KNIFE) return "wathe:knife_stab";
        if (item == WatheItems.REVOLVER) return "wathe:gun_shot";
        if (item == WatheItems.GRENADE) return "wathe:grenade";
        if (item == WatheItems.BAT) return "wathe:bat_hit";
        if (item == WatheItems.POISON_VIAL) return "wathe:poison";
        if (FabricLoader.getInstance().isModLoaded("noellesroles")) {
            if (item == Items.OMINOUS_BOTTLE) return "noellesroles:voodoo";
        }
        if (FabricLoader.getInstance().isModLoaded("starexpress")) {
            if (item == Registries.ITEM.get(Identifier.of("starexpress", "tape"))) return "starexpress:silenced_and_outside";
        }
        if (FabricLoader.getInstance().isModLoaded("stupid_express")) {
            if (item == Registries.ITEM.get(Identifier.of("stupid_express", "lighter"))) return "stupid_express:ignited";
        }
        return "wathe:generic";
    }

    protected void renderWidget(@NotNull DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);
        context.drawItem(deathReasonStack, this.getX(), this.getY());
        context.drawGuiTexture(ShopEntry.Type.WEAPON.getTexture(), this.getX() - 7, this.getY() - 7, 30, 30);
        if (this.isHovered()) {
            this.drawShopSlotHighlight(context, this.getX(), this.getY());
            String translationKey = "death_reason." + deathReasonId.replace(':', '.');
            context.drawTooltip(MinecraftClient.getInstance().textRenderer, Text.translatable(translationKey), mouseX, mouseY);
        }
    }

    private void drawShopSlotHighlight(@NotNull DrawContext context, int x, int y) {
        int color = -1862287543;
        context.fillGradient(RenderLayer.getGuiOverlay(), x, y, x + 16, y + 14, color, color, 0);
        context.fillGradient(RenderLayer.getGuiOverlay(), x, y + 14, x + 15, y + 15, color, color, 0);
        context.fillGradient(RenderLayer.getGuiOverlay(), x, y + 15, x + 14, y + 16, color, color, 0);
    }
}