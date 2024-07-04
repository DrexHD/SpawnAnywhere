package me.drex.spawnanywhere.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.drex.spawnanywhere.SpawnAnywhere;
import me.drex.spawnanywhere.data.Location;
import net.minecraft.network.Connection;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {
    @WrapOperation(
        method = "getPlayerForLogin",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/MinecraftServer;overworld()Lnet/minecraft/server/level/ServerLevel;"
        )
    )
    public ServerLevel replaceSpawnDimension(MinecraftServer server, Operation<ServerLevel> original) {
        Optional<Location> optional = SpawnAnywhere.DATA.spawnLocation();
        Optional<ServerLevel> spawnDimension = optional.map(location -> server.getLevel(location.dimension()));
        return spawnDimension.orElseGet(() -> original.call(server));
    }

    @WrapOperation(
        method = "placeNewPlayer",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/world/level/Level;OVERWORLD:Lnet/minecraft/resources/ResourceKey;"
        )
    )
    public ResourceKey<Level> replaceSpawnDimension(Operation<ResourceKey<Level>> original) {
        Optional<Location> optional = SpawnAnywhere.DATA.spawnLocation();
        Optional<ResourceKey<Level>> spawnDimension = optional.map(Location::dimension);
        return spawnDimension.orElseGet(original::call);
    }

    @Inject(
        method = "placeNewPlayer",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerPlayer;setServerLevel(Lnet/minecraft/server/level/ServerLevel;)V",
            shift = At.Shift.AFTER
        )
    )
    private void replaceSpawnLocationRotation(Connection connection, ServerPlayer serverPlayer, CommonListenerCookie commonListenerCookie, CallbackInfo ci) {
        Optional<Location> optional = SpawnAnywhere.DATA.spawnLocation();
        Optional<ResourceKey<Level>> spawnDimension = optional.map(Location::dimension);
        if (spawnDimension.isPresent()) {
            serverPlayer.setYRot(optional.get().yRot());
            serverPlayer.setXRot(optional.get().xRot());
        }
    }
}
