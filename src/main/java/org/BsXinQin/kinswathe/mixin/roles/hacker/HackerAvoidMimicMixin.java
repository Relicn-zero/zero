package org.BsXinQin.kinswathe.mixin.roles.hacker;

import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.BsXinQin.kinswathe.KinsWatheConfig;
import org.BsXinQin.kinswathe.KinsWatheRoles;
import org.agmas.harpymodloader.modded_murder.ModdedMurderGameMode;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mixin(ModdedMurderGameMode.class)
public class HackerAvoidMimicMixin {

    @Unique private static final Set<Role> ASSIGNED_ROLES = new HashSet<>();
    @Unique private static boolean isAssigning = false;

    @Inject(method = "assignCivilianReplacingRoles", at = @At("HEAD"), remap = false)
    private void resetAssignedRoles(int desiredRoleCount, ServerWorld serverWorld, GameWorldComponent gameWorld, List<ServerPlayerEntity> players, CallbackInfo ci) {
        if (!FabricLoader.getInstance().isModLoaded("noellesroles")) return;
        if (KinsWatheConfig.HANDLER.instance().HackerGenerateWithMimic) return;
        isAssigning = true;
        ASSIGNED_ROLES.clear();
    }

    @Inject(method = "assignCivilianReplacingRoles", at = @At("RETURN"), remap = false)
    private void finishAssigning(int desiredRoleCount, ServerWorld serverWorld, GameWorldComponent gameWorld, List<ServerPlayerEntity> players, CallbackInfo ci) {
        if (!FabricLoader.getInstance().isModLoaded("noellesroles")) return;
        if (KinsWatheConfig.HANDLER.instance().HackerGenerateWithMimic) return;
        isAssigning = false;
    }

    @Inject(method = "assignKillerReplacingRoles", at = @At("HEAD"), remap = false)
    private void resetAssignedRolesKiller(int desiredRoleCount, ServerWorld serverWorld, GameWorldComponent gameWorld, List<ServerPlayerEntity> players, CallbackInfo ci) {
        if (!FabricLoader.getInstance().isModLoaded("noellesroles")) return;
        if (KinsWatheConfig.HANDLER.instance().HackerGenerateWithMimic) return;
        isAssigning = true;
        ASSIGNED_ROLES.clear();
    }

    @Inject(method = "assignKillerReplacingRoles", at = @At("RETURN"), remap = false)
    private void finishAssigningKiller(int desiredRoleCount, ServerWorld serverWorld, GameWorldComponent gameWorld, List<ServerPlayerEntity> players, CallbackInfo ci) {
        if (!FabricLoader.getInstance().isModLoaded("noellesroles")) return;
        if (KinsWatheConfig.HANDLER.instance().HackerGenerateWithMimic) return;
        isAssigning = false;
    }

    @Inject(method = "findAndAssignPlayers", at = @At("HEAD"), cancellable = true, remap = false)
    private static void preventConflictingRoles(int desiredRoleCount, @NotNull Role role, List<ServerPlayerEntity> players, GameWorldComponent gameWorld, World world, @NotNull CallbackInfoReturnable<Integer> cir) {
        if (!FabricLoader.getInstance().isModLoaded("noellesroles")) return;
        if (KinsWatheConfig.HANDLER.instance().HackerGenerateWithMimic) return;
        if (!isAssigning) return;
        for (Role assigned : ASSIGNED_ROLES) {
            if (assigned != null && KinsWatheRoles.conflictRolesGenerate(role, assigned)) {
                cir.setReturnValue(0);
                return;
            }
        }
    }

    @Inject(method = "findAndAssignPlayers", at = @At("RETURN"), remap = false)
    private static void onRoleAssigned(int desiredRoleCount, @NotNull Role role, List<ServerPlayerEntity> players, GameWorldComponent gameWorld, World world, @NotNull CallbackInfoReturnable<Integer> cir) {
        if (!FabricLoader.getInstance().isModLoaded("noellesroles")) return;
        if (KinsWatheConfig.HANDLER.instance().HackerGenerateWithMimic) return;
        if (!isAssigning) return;
        if (cir.getReturnValue() != null && cir.getReturnValue() > 0) {
            ASSIGNED_ROLES.add(role);
        }
    }
}