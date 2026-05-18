package org.BsXinQin.kinswathe.mixin.roles.kobe;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.game.GameFunctions;
import dev.doctor4t.wathe.game.gamemode.MurderGameMode;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.BsXinQin.kinswathe.KinsWatheRoles;
import org.BsXinQin.kinswathe.component.CustomWinnerComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(MurderGameMode.class)
public class KobeWinConditionMixin {

    @Inject(method = "tickServerGameLoop", at = @At("TAIL"))
    private void checkKobeWin(ServerWorld world, GameWorldComponent gameWorld, CallbackInfo ci) {
        if (!gameWorld.isRunning()) return;

        List<ServerPlayerEntity> alivePlayers = world.getPlayers().stream()
                .filter(GameFunctions::isPlayerAliveAndSurvival)
                .toList();

        boolean kobeAlive = alivePlayers.stream().anyMatch(p -> gameWorld.isRole(p, KinsWatheRoles.KOBE));
        if (!kobeAlive) return;

        // 存活玩家总数 ≤ 2 且科比存活 → 科比获胜
        if (alivePlayers.size() <= 2) {
            CustomWinnerComponent customWinner = CustomWinnerComponent.KEY.get(world);
            if (customWinner.hasCustomWinner()) return;
            customWinner.setWinningTextId("kobe");
            customWinner.setWinners(alivePlayers.stream().filter(p -> gameWorld.isRole(p, KinsWatheRoles.KOBE)).toList());
            customWinner.setColor(KinsWatheRoles.KOBE.color());
            customWinner.sync();
            GameFunctions.stopGame(world);
        }
    }
}
