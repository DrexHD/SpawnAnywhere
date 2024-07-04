package me.drex.spawnanywhere;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import me.drex.spawnanywhere.command.SpawnAnywhereCommand;
import me.drex.spawnanywhere.data.SpawnAnywhereData;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.nbt.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class SpawnAnywhere implements ModInitializer {

    public static final String MOD_ID = "spawnanywhere";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static SpawnAnywhereData DATA;

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTING.register(SpawnAnywhere::load);
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            SpawnAnywhereCommand.register(dispatcher);
        });
    }

    public static void load(MinecraftServer server) {
        Path path = server.getWorldPath(LevelResource.ROOT).resolve("spawnanyhwere.dat");
        if (!Files.exists(path)) {
            DATA = new SpawnAnywhereData(Optional.empty(), Optional.empty());
            return;
        }
        try {
            Dynamic<Tag> dynamic = new Dynamic<>(NbtOps.INSTANCE, NbtIo.readCompressed(path, NbtAccounter.unlimitedHeap()));
            DATA = SpawnAnywhereData.CODEC.decode(NbtOps.INSTANCE, dynamic.getValue()).getOrThrow().getFirst();
        } catch (IOException exception) {
            LOGGER.error("Failed to load {} data!", MOD_ID, exception);
        }
    }

    public static void save(MinecraftServer server) {
        Path path = server.getWorldPath(LevelResource.ROOT).resolve("spawnanyhwere.dat");
        DataResult<Tag> dataResult = SpawnAnywhereData.CODEC.encodeStart(NbtOps.INSTANCE, DATA);
        Tag tag = dataResult.result().orElseThrow();
        try {
            NbtIo.writeCompressed((CompoundTag) tag, Files.newOutputStream(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}