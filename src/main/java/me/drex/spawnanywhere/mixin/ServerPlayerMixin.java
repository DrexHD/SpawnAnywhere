package me.drex.spawnanywhere.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.authlib.GameProfile;
import me.drex.spawnanywhere.SpawnAnywhere;
import me.drex.spawnanywhere.data.Location;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.TeleportTransition;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {
    @Shadow
    @Final
    private MinecraftServer server;

    public ServerPlayerMixin(Level level, GameProfile gameProfile) {
        super(level, gameProfile);
    }

    @WrapOperation(
        method = "findRespawnPositionAndUseSpawnBlock",
        at = {
            @At(
                value = "INVOKE",
                target = "Lnet/minecraft/world/level/portal/TeleportTransition;missingRespawnBlock(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/level/portal/TeleportTransition$PostTeleportTransition;)Lnet/minecraft/world/level/portal/TeleportTransition;"
            ),
            @At(
                value = "INVOKE",
                target = "Lnet/minecraft/world/level/portal/TeleportTransition;createDefault(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/level/portal/TeleportTransition$PostTeleportTransition;)Lnet/minecraft/world/level/portal/TeleportTransition;"
            )
        }
    )
    private TeleportTransition replaceRespawnLocation(ServerPlayer serverPlayer, TeleportTransition.PostTeleportTransition postTeleportTransition, Operation<TeleportTransition> original) {
        Optional<Location> optional = SpawnAnywhere.DATA.respawnLocation();
        Optional<ServerLevel> spawnLocation = optional.map(location -> server.getLevel(location.dimension()));
        if (spawnLocation.isPresent()) {
            return optional.get().dimensionTransition(server, postTeleportTransition);
        } else {
            return original.call(serverPlayer,  postTeleportTransition);
        }
    }
}
