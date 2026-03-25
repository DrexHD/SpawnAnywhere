package me.drex.spawnanywhere.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.drex.spawnanywhere.SpawnAnywhere;
import me.drex.spawnanywhere.data.Location;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.config.PrepareSpawnTask;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Mixin(PrepareSpawnTask.class)
public abstract class PrepareSpawnTaskMixin {
    @WrapOperation(
        method = "lambda$start$3",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/level/PlayerSpawnFinder;findSpawn(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;)Ljava/util/concurrent/CompletableFuture;"
        )
    )
    private static CompletableFuture<Vec3> replaceSpawnLocation(ServerLevel serverLevel, BlockPos blockPos, Operation<CompletableFuture<Vec3>> original) {
        Optional<Location> optional = SpawnAnywhere.DATA.spawnLocation();
        return optional.map(location -> CompletableFuture.completedFuture(location.pos().getBottomCenter())).orElseGet(() -> original.call(serverLevel, blockPos));
    }

    @WrapOperation(
        method = "lambda$start$2",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/storage/LevelData$RespawnData;dimension()Lnet/minecraft/resources/ResourceKey;"
        )
    )
    private static ResourceKey<Level> replaceSpawnDimension(LevelData.RespawnData instance, Operation<ResourceKey<Level>> original) {
        Optional<Location> optional = SpawnAnywhere.DATA.spawnLocation();
        return optional.map(Location::dimension).orElseGet(() -> original.call(instance));
    }

    @WrapOperation(
        method = "start",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/storage/LevelData$RespawnData;yaw()F"
        )
    )
    private static float replaceSpawnYaw(LevelData.RespawnData instance, Operation<Float> original) {
        Optional<Location> optional = SpawnAnywhere.DATA.spawnLocation();
        return optional.map(Location::yRot).orElseGet(() -> original.call(instance));
    }

    @WrapOperation(
        method = "start",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/storage/LevelData$RespawnData;pitch()F"
        )
    )
    private static float replaceSpawnPitch(LevelData.RespawnData instance, Operation<Float> original) {
        Optional<Location> optional = SpawnAnywhere.DATA.spawnLocation();
        return optional.map(Location::xRot).orElseGet(() -> original.call(instance));
    }
}
