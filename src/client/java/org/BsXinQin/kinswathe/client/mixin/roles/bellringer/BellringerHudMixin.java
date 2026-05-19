package org.BsXinQin.kinswathe.client.mixin.roles.bellringer;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.PlayerShopComponent;
import dev.doctor4t.wathe.client.WatheClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import org.BsXinQin.kinswathe.KinsWatheConfig;
import org.BsXinQin.kinswathe.KinsWatheRoles;
import org.BsXinQin.kinswathe.client.KinsWatheInitializeClient;
import org.BsXinQin.kinswathe.component.AbilityPlayerComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class BellringerHudMixin {

    @Shadow public abstract TextRenderer getTextRenderer();

    @Inject(method = "render", at = @At("TAIL"))
    public void getAbilityHud(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        var player = MinecraftClient.getInstance().player;
        if (player == null) return;

        var gameWorld = GameWorldComponent.KEY.get(player.getWorld());
        var ability = AbilityPlayerComponent.KEY.get(player);
        var playerShop = PlayerShopComponent.KEY.get(player);

        if (gameWorld.isRole(player, KinsWatheRoles.BELLRINGER) && WatheClient.isPlayerAliveAndInSurvival()) {
            int drawY = context.getScaledWindowHeight();
            Text line;
            int price = KinsWatheConfig.HANDLER.instance().BellringerAbilityPrice;
            if (playerShop.balance < price) {
                line = Text.translatable("tip.kinswathe.ability.not_enough_money", price);
            } else if (ability.cooldown > 0) {
                line = Text.translatable("tip.kinswathe.cooldown", ability.cooldown / 20);
            } else {
                line = Text.translatable("tip.kinswathe.ability.can_use", KinsWatheInitializeClient.abilityBind.getBoundKeyLocalizedText());
            }
            drawY -= getTextRenderer().getWrappedLinesHeight(line, 999999);
            context.drawTextWithShadow(getTextRenderer(), line,
                context.getScaledWindowWidth() - getTextRenderer().getWidth(line),
                drawY, KinsWatheRoles.BELLRINGER.color());
        }
    }
}
