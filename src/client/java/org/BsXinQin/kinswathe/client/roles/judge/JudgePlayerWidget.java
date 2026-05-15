package org.BsXinQin.kinswathe.client.roles.judge;

import dev.doctor4t.wathe.cca.PlayerShopComponent;
import dev.doctor4t.wathe.client.gui.screen.ingame.LimitedInventoryScreen;
import dev.doctor4t.wathe.util.ShopEntry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import org.BsXinQin.kinswathe.component.AbilityPlayerComponent;
import org.BsXinQin.kinswathe.component.ConfigWorldComponent;
import org.BsXinQin.kinswathe.packet.roles.JudgeC2SPacket;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Arrays;
import java.util.UUID;

public class JudgePlayerWidget extends ButtonWidget {

    public final LimitedInventoryScreen screen;
    public final UUID targetUUID;
    public final PlayerListEntry targetPlayerEntry;

    public JudgePlayerWidget(@NotNull LimitedInventoryScreen screen, int x, int y, @NotNull UUID targetUUID, @NotNull PlayerListEntry targetPlayerEntry) {
        super(x, y, 16, 16, Text.literal(""), (button) -> {
            if (MinecraftClient.getInstance().player == null) return;
            AbilityPlayerComponent ability = AbilityPlayerComponent.KEY.get(MinecraftClient.getInstance().player);
            PlayerShopComponent playerShop = PlayerShopComponent.KEY.get(MinecraftClient.getInstance().player);
            if (ability.cooldown <= 0 && playerShop.balance >= ConfigWorldComponent.KEY.get(MinecraftClient.getInstance().player.getWorld()).JudgeAbilityPrice) {
                ClientPlayNetworking.send(new JudgeC2SPacket(targetUUID));
                screen.close();
            }
        }, DEFAULT_NARRATION_SUPPLIER);
        this.screen = screen;
        this.targetUUID = targetUUID;
        this.targetPlayerEntry = targetPlayerEntry;
    }

    protected void renderWidget(@NotNull DrawContext context, int mouseX, int mouseY, float delta) {
        if (MinecraftClient.getInstance().player == null) return;
        super.renderWidget(context, mouseX, mouseY, delta);
        AbilityPlayerComponent ability = AbilityPlayerComponent.KEY.get(MinecraftClient.getInstance().player);
        PlayerShopComponent playerShop = PlayerShopComponent.KEY.get(MinecraftClient.getInstance().player);
        if (ability.cooldown <= 0 && playerShop.balance >= ConfigWorldComponent.KEY.get(MinecraftClient.getInstance().player.getWorld()).JudgeAbilityPrice) {
            context.drawGuiTexture(ShopEntry.Type.POISON.getTexture(), this.getX() - 7, this.getY() - 7, 30, 30);
            PlayerSkinDrawer.draw(context, targetPlayerEntry.getSkinTextures().texture(), this.getX(), this.getY(), 16);
            if (this.isHovered()) {
                this.drawShopSlotHighlight(context, this.getX(), this.getY());
                context.drawTooltip(MinecraftClient.getInstance().textRenderer, Arrays.asList(Text.of(targetPlayerEntry.getProfile().getName()), Text.translatable("hud.kinswathe.judge.ready").copy().withColor(Color.GREEN.getRGB())), this.getX() - 4 - MinecraftClient.getInstance().textRenderer.getWidth(targetPlayerEntry.getProfile().getName()) / 2, this.getY() - 19);
            }
        } else {
            context.setShaderColor(0.25f, 0.25f, 0.25f, 0.5f);
            context.drawGuiTexture(ShopEntry.Type.POISON.getTexture(), this.getX() - 7, this.getY() - 7, 30, 30);
            PlayerSkinDrawer.draw(context, targetPlayerEntry.getSkinTextures().texture(), this.getX(), this.getY(), 16);
            if (this.isHovered()) {
                this.drawShopSlotHighlight(context, this.getX(), this.getY());
                if (ability.cooldown > 0) {
                    context.drawTooltip(MinecraftClient.getInstance().textRenderer, Text.of(targetPlayerEntry.getProfile().getName()), this.getX() - 4 - MinecraftClient.getInstance().textRenderer.getWidth(targetPlayerEntry.getProfile().getName()) / 2, this.getY() - 19);
                } else {
                    context.drawTooltip(MinecraftClient.getInstance().textRenderer, Arrays.asList(Text.of(targetPlayerEntry.getProfile().getName()), Text.translatable("tip.kinswathe.ability.not_enough_money", ConfigWorldComponent.KEY.get(MinecraftClient.getInstance().player.getWorld()).JudgeAbilityPrice)), this.getX() - 4 - MinecraftClient.getInstance().textRenderer.getWidth(targetPlayerEntry.getProfile().getName()) / 2, this.getY() - 19);
                }
            }
            context.setShaderColor(1f, 1f, 1f, 1f);
            if (ability.cooldown > 0) {
                context.drawText(MinecraftClient.getInstance().textRenderer, ability.cooldown / 20 + "", this.getX(), this.getY(), Color.RED.getRGB(), true);
            } else {
                context.drawText(MinecraftClient.getInstance().textRenderer, "", this.getX(), this.getY(), Color.RED.getRGB(), true);
            }
        }
    }

    private void drawShopSlotHighlight(@NotNull DrawContext context, int x, int y) {
        int color = -1862287543;
        context.fillGradient(RenderLayer.getGuiOverlay(), x, y, x + 16, y + 14, color, color, 0);
        context.fillGradient(RenderLayer.getGuiOverlay(), x, y + 14, x + 15, y + 15, color, color, 0);
        context.fillGradient(RenderLayer.getGuiOverlay(), x, y + 15, x + 14, y + 16, color, color, 0);
    }
}