package org.BsXinQin.kinswathe.client.roles.bodymaker;

import dev.doctor4t.wathe.client.gui.screen.ingame.LimitedInventoryScreen;
import dev.doctor4t.wathe.util.ShopEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import org.BsXinQin.kinswathe.component.AbilityPlayerComponent;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.UUID;

public class BodymakerPlayerWidget extends ButtonWidget {

    public final LimitedInventoryScreen screen;
    public final UUID targetUUID;
    public final PlayerListEntry targetPlayerEntry;
    public final BodymakerScreenCallback callback;

    public BodymakerPlayerWidget(@NotNull LimitedInventoryScreen screen, int x, int y, @NotNull UUID targetUUID, @NotNull PlayerListEntry targetPlayerEntry, @NotNull BodymakerScreenCallback callback) {
        super(x, y, 16, 16, Text.literal(""), (button) -> {
            if (MinecraftClient.getInstance().player == null) return;
            AbilityPlayerComponent ability = AbilityPlayerComponent.KEY.get(MinecraftClient.getInstance().player);
            if (ability.cooldown <= 0) callback.setSelectedPlayer(targetUUID);
        }, DEFAULT_NARRATION_SUPPLIER);
        this.screen = screen;
        this.targetUUID = targetUUID;
        this.targetPlayerEntry = targetPlayerEntry;
        this.callback = callback;
    }

    protected void renderWidget(@NotNull DrawContext context, int mouseX, int mouseY, float delta) {
        if (MinecraftClient.getInstance().player == null) return;
        super.renderWidget(context, mouseX, mouseY, delta);
        AbilityPlayerComponent ability = AbilityPlayerComponent.KEY.get(MinecraftClient.getInstance().player);
        if (ability.cooldown <= 0) {
            context.drawGuiTexture(ShopEntry.Type.POISON.getTexture(), this.getX() - 7, this.getY() - 7, 30, 30);
            PlayerSkinDrawer.draw(context, targetPlayerEntry.getSkinTextures().texture(), this.getX(), this.getY(), 16);
            if (this.isHovered()) {
                this.drawShopSlotHighlight(context, this.getX(), this.getY());
                context.drawTooltip(MinecraftClient.getInstance().textRenderer, Text.of(targetPlayerEntry.getProfile().getName()), this.getX() - 4 - MinecraftClient.getInstance().textRenderer.getWidth(targetPlayerEntry.getProfile().getName()) / 2, this.getY() - 9);
            }
        } else {
            context.setShaderColor(0.25f,0.25f,0.25f,0.5f);
            context.drawGuiTexture(ShopEntry.Type.POISON.getTexture(), this.getX() - 7, this.getY() - 7, 30, 30);
            PlayerSkinDrawer.draw(context, targetPlayerEntry.getSkinTextures().texture(), this.getX(), this.getY(), 16);
            if (this.isHovered()) {
                this.drawShopSlotHighlight(context, this.getX(), this.getY());
                context.drawTooltip(MinecraftClient.getInstance().textRenderer, Text.of(targetPlayerEntry.getProfile().getName()), this.getX() - 4 - MinecraftClient.getInstance().textRenderer.getWidth(targetPlayerEntry.getProfile().getName()) / 2, this.getY() - 9);
            }
            context.setShaderColor(1f,1f,1f,1f);
            context.drawText(MinecraftClient.getInstance().textRenderer, ability.cooldown / 20 + "", this.getX(), this.getY(), Color.RED.getRGB(), true);
        }
    }

    private void drawShopSlotHighlight(@NotNull DrawContext context, int x, int y) {
        int color = -1862287543;
        context.fillGradient(RenderLayer.getGuiOverlay(), x, y, x + 16, y + 14, color, color, 0);
        context.fillGradient(RenderLayer.getGuiOverlay(), x, y + 14, x + 15, y + 15, color, color, 0);
        context.fillGradient(RenderLayer.getGuiOverlay(), x, y + 15, x + 14, y + 16, color, color, 0);
    }
}