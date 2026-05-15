package org.BsXinQin.kinswathe.client.roles.arbiter;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import org.BsXinQin.kinswathe.component.AbilityPlayerComponent;
import org.BsXinQin.kinswathe.component.ConfigWorldComponent;
import org.BsXinQin.kinswathe.packet.roles.ArbiterC2SPacket;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ArbiterPlayerWidget extends ButtonWidget {

    private final UUID targetUuid;
    private final PlayerListEntry targetEntry;

    public ArbiterPlayerWidget(int x, int y, UUID targetUuid, PlayerListEntry targetEntry) {
        super(x, y, 16, 16, Text.literal(""), (button) -> {
            if (MinecraftClient.getInstance().player == null) return;
            var player = MinecraftClient.getInstance().player;
            var ability = AbilityPlayerComponent.KEY.get(player);
            var config = ConfigWorldComponent.KEY.get(player.getWorld());
            // 可选：客户端简单检查冷却时间，避免无意义发送（但最终服务端还会再检查）
            if (ability.cooldown > 0) return;
            // 发送裁决数据包
            ClientPlayNetworking.send(new ArbiterC2SPacket(targetUuid));
            // 关闭背包界面
            if (MinecraftClient.getInstance().currentScreen != null) {
                MinecraftClient.getInstance().currentScreen.close();
            }
        }, DEFAULT_NARRATION_SUPPLIER);
        this.targetUuid = targetUuid;
        this.targetEntry = targetEntry;
    }

    @Override
    protected void renderWidget(@NotNull DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);
        if (targetEntry != null) {
            PlayerSkinDrawer.draw(context, targetEntry.getSkinTextures().texture(), this.getX(), this.getY(), 16);
        }
        if (this.isHovered()) {
            context.drawTooltip(MinecraftClient.getInstance().textRenderer, Text.of(targetEntry.getProfile().getName()), mouseX, mouseY);
        }
    }
}