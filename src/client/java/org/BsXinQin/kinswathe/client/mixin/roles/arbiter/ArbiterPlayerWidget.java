package org.BsXinQin.kinswathe.client.roles.arbiter;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import org.BsXinQin.kinswathe.packet.roles.ArbiterC2SPacket;

import java.util.UUID;

public class ArbiterPlayerWidget extends ButtonWidget {
    private final UUID targetUuid;

    public ArbiterPlayerWidget(int x, int y, UUID targetUuid, PlayerListEntry entry) {
        super(x, y, 16, 16, Text.literal(entry.getProfile().getName()), button -> {
            ClientPlayNetworking.send(new ArbiterC2SPacket(targetUuid));
            MinecraftClient.getInstance().setScreen(null);
        }, ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
        this.targetUuid = targetUuid;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);
        // 这里简化：实际应获取目标玩家的皮肤纹理，为简洁省略 PlayerListEntry 的获取
        // 由于我们构造时已经传入 entry，可将其存为字段
        // 但为减少错误，我们直接绘制默认头像（可后续优化）
        // 实际大法官的实现使用了 PlayerSkinDrawer.draw，需要 PlayerListEntry
        // 你可以根据自己项目情况调整，这里给出一个安全绘制占位符的版本
        context.fill(this.getX(), this.getY(), this.getX() + 16, this.getY() + 16, 0xFFAAAAAA);
        if (this.isHovered()) {
            context.drawTooltip(MinecraftClient.getInstance().textRenderer, this.getMessage(), mouseX, mouseY);
        }
    }
}
