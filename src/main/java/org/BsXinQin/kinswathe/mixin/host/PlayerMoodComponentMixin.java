package org.BsXinQin.kinswathe.mixin.host;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.PlayerMoodComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.BsXinQin.kinswathe.KinsWatheConfig;
import org.BsXinQin.kinswathe.KinsWatheRoles;
import org.BsXinQin.kinswathe.component.AbilityPlayerComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerMoodComponent.class)
public class PlayerMoodComponentMixin {

    @Shadow @Final private PlayerEntity player;

    @Inject(method = "setMood", at = @At("HEAD"))
    private void onMoodChange(float mood, CallbackInfo ci) {
        if (!KinsWatheConfig.HANDLER.instance().EnableJudgeLordCooldownReduction) return;
        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(player.getWorld());
        if (gameWorld.isRole(player, KinsWatheRoles.JUDGELORD)) {
            AbilityPlayerComponent ability = AbilityPlayerComponent.KEY.get(player);
            if (ability.cooldown > 0) {
                int reductionTicks = KinsWatheConfig.HANDLER.instance().JudgeLordCooldownReductionAmount * 20;
                ability.cooldown = Math.max(0, ability.cooldown - reductionTicks);
                ability.sync();
                if (player instanceof ServerPlayerEntity serverPlayer) {
                    serverPlayer.sendMessage(Text.translatable("tip.kinswathe.judgelord.cooldown_reduced", KinsWatheConfig.HANDLER.instance().JudgeLordCooldownReductionAmount), true);
                }
            }
        }
    }
}
