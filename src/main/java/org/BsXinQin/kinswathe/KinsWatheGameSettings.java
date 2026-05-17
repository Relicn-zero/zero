package org.BsXinQin.kinswathe;

import dev.doctor4t.wathe.api.WatheGameModes;
import dev.doctor4t.wathe.api.event.AllowPlayerDeath;
import dev.doctor4t.wathe.api.event.AllowPlayerPunching;
import dev.doctor4t.wathe.api.event.GameEvents;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.PlayerPoisonComponent;
import dev.doctor4t.wathe.cca.PlayerPsychoComponent;
import dev.doctor4t.wathe.game.GameConstants;
import dev.doctor4t.wathe.index.WatheItems;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.UseAction;
import org.BsXinQin.kinswathe.component.AbilityPlayerComponent;
import org.BsXinQin.kinswathe.component.GameSafeComponent;
import org.BsXinQin.kinswathe.component.PlayerEffectComponent;
import org.BsXinQin.kinswathe.packet.host.AbilityC2SPacket;
import org.BsXinQin.kinswathe.packet.items.BlowgunC2SPacket;
import org.BsXinQin.kinswathe.packet.items.HuntingKnifeC2SPacket;
import org.BsXinQin.kinswathe.packet.items.PanC2SPacket;
import org.BsXinQin.kinswathe.packet.roles.BodymakerC2SPacket;
import org.BsXinQin.kinswathe.packet.roles.JudgeC2SPacket;
import org.BsXinQin.kinswathe.roles.cook.CookComponent;
import org.BsXinQin.kinswathe.roles.dreamer.DreamerComponent;
import org.BsXinQin.kinswathe.roles.dreamer.DreamerKillerComponent;
import org.BsXinQin.kinswathe.roles.hacker.HackerComponent;
import org.BsXinQin.kinswathe.roles.hacker.HackerPhoneComponent;
import org.BsXinQin.kinswathe.roles.hunter.HunterComponent;
import org.BsXinQin.kinswathe.roles.kidnapper.KidnapperComponent;
import org.BsXinQin.kinswathe.roles.physician.PhysicianComponent;
import org.BsXinQin.kinswathe.roles.technician.TechnicianComponent;
import org.agmas.harpymodloader.events.ResetPlayerEvent;
import org.jetbrains.annotations.NotNull;

public class KinsWatheGameSettings {

    private static boolean GAME_START = false;
    private static boolean GAME_STOP = false;

    public static void initializeConfig() { KinsWatheConfig.HANDLER.load(); }
    public static void initializeCommands() { KinsWatheCommands.register(); }

    public static void betterGameSettings() {
        GameEvents.ON_GAME_START.register((gameMode) -> GAME_START = true);
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            if (GAME_START) {
                for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) player.getInventory().selectedSlot = 8;
                setCommands(server);
                setGameSafeTime(server);
                GAME_START = false;
            }
        });
        GameEvents.ON_GAME_STOP.register((gameMode) -> GAME_STOP = true);
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            if (GAME_STOP) {
                setCommands(server);
                GameSafeComponent.KEY.get(server.getOverworld()).reset();
                GAME_STOP = false;
            }
        });
    }

    public static void setCommands(@NotNull MinecraftServer server) {
        server.getCommandManager().executeWithPrefix(server.getCommandSource().withSilent(), "kill @e[type=kinswathe:capture_device]");
        server.getCommandManager().executeWithPrefix(server.getCommandSource().withSilent(), "kill @e[type=wathe:player_body]");
        server.getCommandManager().executeWithPrefix(server.getCommandSource().withSilent(), "kill @e[type=item]");
        if (GAME_STOP) {
            server.getCommandManager().executeWithPrefix(server.getCommandSource().withSilent(), "clear @a");
            server.getCommandManager().executeWithPrefix(server.getCommandSource().withSilent(), "effect clear @a");
        }
        if (FabricLoader.getInstance().isModLoaded("noellesroles")) {
            server.getCommandManager().executeWithPrefix(server.getCommandSource().withSilent(), "kill @e[type=noellesroles:cube]");
        }
    }

    public static void setGameSafeTime(@NotNull MinecraftServer server) {
        if (!KinsWatheConfig.HANDLER.instance().EnableStartSafeTime) return;
        if (GameWorldComponent.KEY.get(server.getOverworld()).getGameMode() == WatheGameModes.DISCOVERY || GameWorldComponent.KEY.get(server.getOverworld()).getGameMode() == WatheGameModes.LOOSE_ENDS) return;
        GameSafeComponent.KEY.get(server.getOverworld()).startGameSafe();
        for (ServerPlayerEntity serverPlayer : server.getPlayerManager().getPlayerList()) {
            if (serverPlayer == null) continue;
            serverPlayer.getItemCooldownManager().set(KinsWatheItems.BLOWGUN, GameConstants.getInTicks(0, KinsWatheConfig.HANDLER.instance().StartingCooldown));
            serverPlayer.getItemCooldownManager().set(KinsWatheItems.HUNTING_KNIFE, GameConstants.getInTicks(0, KinsWatheConfig.HANDLER.instance().StartingCooldown));
            serverPlayer.getItemCooldownManager().set(KinsWatheItems.KNOCKOUT_DRUG, GameConstants.getInTicks(0, KinsWatheConfig.HANDLER.instance().StartingCooldown));
            serverPlayer.getItemCooldownManager().set(KinsWatheItems.POISON_INJECTOR, GameConstants.getInTicks(0, KinsWatheConfig.HANDLER.instance().StartingCooldown));
            serverPlayer.getItemCooldownManager().set(WatheItems.DERRINGER, GameConstants.getInTicks(0, KinsWatheConfig.HANDLER.instance().StartingCooldown));
            serverPlayer.getItemCooldownManager().set(WatheItems.KNIFE, GameConstants.getInTicks(0, KinsWatheConfig.HANDLER.instance().StartingCooldown));
            serverPlayer.getItemCooldownManager().set(WatheItems.GRENADE, GameConstants.getInTicks(0, KinsWatheConfig.HANDLER.instance().StartingCooldown));
            serverPlayer.getItemCooldownManager().set(WatheItems.REVOLVER, GameConstants.getInTicks(0, KinsWatheConfig.HANDLER.instance().StartingCooldown));
            serverPlayer.getItemCooldownManager().set(WatheItems.PSYCHO_MODE, GameConstants.getInTicks(0, KinsWatheConfig.HANDLER.instance().StartingCooldown));
            if (FabricLoader.getInstance().isModLoaded("harpysimpleroles")) {
                serverPlayer.getItemCooldownManager().set(Registries.ITEM.get(Identifier.of("harpysimpleroles", "toxin")), GameConstants.getInTicks(0, KinsWatheConfig.HANDLER.instance().StartingCooldown));
                serverPlayer.getItemCooldownManager().set(Registries.ITEM.get(Identifier.of("harpysimpleroles", "bandit_revolver")), GameConstants.getInTicks(0, KinsWatheConfig.HANDLER.instance().StartingCooldown));
            }
            if (FabricLoader.getInstance().isModLoaded("starexpress")) {
                serverPlayer.getItemCooldownManager().set(Registries.ITEM.get(Identifier.of("starexpress", "tape")), GameConstants.getInTicks(0, KinsWatheConfig.HANDLER.instance().StartingCooldown));
            }
            if (FabricLoader.getInstance().isModLoaded("stupid_express")) {
                serverPlayer.getItemCooldownManager().set(Registries.ITEM.get(Identifier.of("stupid_express", "lighter")), GameConstants.getInTicks(0, KinsWatheConfig.HANDLER.instance().StartingCooldown));
            }
        }
    }

    public static void registerPackets() {
        PayloadTypeRegistry.playC2S().register(AbilityC2SPacket.ID, AbilityC2SPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(BodymakerC2SPacket.ID, BodymakerC2SPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(JudgeC2SPacket.ID, JudgeC2SPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(BlowgunC2SPacket.ID, BlowgunC2SPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(HuntingKnifeC2SPacket.ID, HuntingKnifeC2SPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(PanC2SPacket.ID, PanC2SPacket.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(BlowgunC2SPacket.ID, new BlowgunC2SPacket.Receiver());
        ServerPlayNetworking.registerGlobalReceiver(HuntingKnifeC2SPacket.ID, new HuntingKnifeC2SPacket.Receiver());
        ServerPlayNetworking.registerGlobalReceiver(PanC2SPacket.ID, new PanC2SPacket.Receiver());
        // 已删除 ArbiterC2SPacket 的所有注册
    }

    public static void registerEvents() {
        AllowPlayerDeath.EVENT.register(((player, killer, identifier) -> {
            GameWorldComponent gameWorld = GameWorldComponent.KEY.get(player.getWorld());
            DreamerComponent playerDream = DreamerComponent.KEY.get(player);
            PhysicianComponent playerPhysician = PhysicianComponent.KEY.get(player);
            PlayerPoisonComponent.KEY.get(player).reset();
            if (GameSafeComponent.KEY.get(player.getWorld()).isSafe()) return identifier == GameConstants.DeathReasons.FELL_OUT_OF_TRAIN;
            if (player.getMainHandStack().isOf(KinsWatheItems.PAN) && player.isUsingItem() && player.getActiveItem().getItem().getUseAction(player.getActiveItem()) == UseAction.SPEAR) {
                if (identifier == GameConstants.DeathReasons.GUN) {
                    KinsWatheItems.setItemAfterUsing(player, KinsWatheItems.PAN, Hand.MAIN_HAND);
                    player.playSound(SoundEvents.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
                    return false;
                }
            }
            if (playerDream.dreamArmor > 0) { playerDream.teleportToDreamer(); playerDream.reset(); return false; }
            if (playerPhysician.physicianArmor > 0) {
                if (identifier == GameConstants.DeathReasons.FELL_OUT_OF_TRAIN) return true;
                playerPhysician.armorSound(); playerPhysician.reset(); return false;
            }
            if (FabricLoader.getInstance().isModLoaded("noellesroles")) {
                if (KinsWatheConfig.HANDLER.instance().EnableNoellesRolesModify && KinsWatheConfig.HANDLER.instance().JesterAttackKillerModify) {
                    if (killer != null && gameWorld.getRole(player).canUseKiller() && gameWorld.isRole(killer, KinsWatheRoles.noellesrolesRoles("JESTER")) && PlayerPsychoComponent.KEY.get(killer).psychoTicks > 0) {
                        return identifier != GameConstants.DeathReasons.BAT;
                    }
                }
            }
            return true;
        }));
        AllowPlayerPunching.EVENT.register(((attacker, victim) -> attacker.getMainHandStack().isOf(KinsWatheItems.HUNTING_KNIFE)));
    }

    /// 重置事件
public static void resetEvents() {
    ResetPlayerEvent.EVENT.register(player -> {
        GameSafeComponent.KEY.get(player.getWorld()).reset();
        PlayerEffectComponent.KEY.get(player).reset();
        AbilityPlayerComponent.KEY.get(player).reset();
        CookComponent.KEY.get(player).reset();
        DreamerComponent.KEY.get(player).reset();
        DreamerKillerComponent.KEY.get(player).reset();
        HackerComponent.KEY.get(player).reset();
        HackerPhoneComponent.KEY.get(player).reset();
        HunterComponent.KEY.get(player).reset();
        KidnapperComponent.KEY.get(player).reset();
        PhysicianComponent.KEY.get(player).reset();
        TechnicianComponent.KEY.get(player).reset();

        // 机器人永久速度效果（重新添加）
        if (KinsWatheConfig.HANDLER.instance().EnableRobotSpeedEffect) {
            GameWorldComponent gameWorld = GameWorldComponent.KEY.get(player.getWorld());
            if (gameWorld.isRole(player, KinsWatheRoles.ROBOT)) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, Integer.MAX_VALUE, 0, false, false, true));
            }
        }
    });
}
    public static void init() {
        initializeConfig();
        initializeCommands();
        betterGameSettings();
        registerPackets();
        registerEvents();
        resetEvents();
    }
}
