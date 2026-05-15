package org.BsXinQin.kinswathe.client.roles.technician;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.client.WatheClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.BsXinQin.kinswathe.KinsWatheItems;
import org.BsXinQin.kinswathe.KinsWatheRoles;
import org.BsXinQin.kinswathe.entities.CaptureDeviceEntity;
import org.jetbrains.annotations.NotNull;

public class CaptureDeviceEntityRenderer extends EntityRenderer<CaptureDeviceEntity> {

    private final ItemRenderer itemRenderer;
    private final float scale;

    public CaptureDeviceEntityRenderer(@NotNull EntityRendererFactory.@NotNull Context context, float scale) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
        this.scale = scale;

    }

    public CaptureDeviceEntityRenderer(@NotNull EntityRendererFactory.@NotNull Context context) {
        this(context, 1.0F);
    }

    @Override
    public Identifier getTexture(@NotNull CaptureDeviceEntity entity) {
        return PlayerScreenHandler.BLOCK_ATLAS_TEXTURE;
    }

    @Override
    public void render(@NotNull CaptureDeviceEntity entity, float yaw, float tickDelta, @NotNull MatrixStack matrices, @NotNull VertexConsumerProvider vertexConsumers, int light) {
        if (MinecraftClient.getInstance().player == null) return;
        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(MinecraftClient.getInstance().player.getWorld());
        if (gameWorld.isRole(MinecraftClient.getInstance().player, KinsWatheRoles.TECHNICIAN) || WatheClient.isPlayerSpectatingOrCreative()) {
            matrices.push();
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-entity.getYaw()));
            matrices.translate(0.0F, (float) entity.hashCode() % 24.0F * 1.0E-4F, 0.0F);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F));
            matrices.scale(this.scale * 0.4F, this.scale * 0.4F, this.scale * 0.4F);
            this.itemRenderer.renderItem(KinsWatheItems.CAPTURE_DEVICE.getDefaultStack(), ModelTransformationMode.FIXED, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, entity.getWorld(), entity.getId());
            matrices.pop();
            super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        }
    }
}