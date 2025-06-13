package me.drex.spawnanywhere.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.drex.spawnanywhere.SpawnAnywhere;
import me.drex.spawnanywhere.data.Location;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(ServerConfigurationPacketListenerImpl.class)
public abstract class ServerConfigurationPacketListenerImplMixin {
    @WrapOperation(
        method = "handleConfigurationFinished",
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
}
