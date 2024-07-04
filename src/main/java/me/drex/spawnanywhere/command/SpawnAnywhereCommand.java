package me.drex.spawnanywhere.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.drex.spawnanywhere.SpawnAnywhere;
import me.drex.spawnanywhere.data.Location;
import me.drex.spawnanywhere.data.SpawnAnywhereData;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiFunction;

public class SpawnAnywhereCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("spawnanywhere")
                .requires(Permissions.require("spawnanywhere.root", 2))
                .then(
                    Commands.literal("setspawn")
                        .requires(Permissions.require("spawnanywhere.setspawn", 2))
                        .executes(context -> setLocation(context, SpawnAnywhereData::withSpawnLocation, "spawn"))
                )
                .then(
                    Commands.literal("setrespawn")
                        .requires(Permissions.require("spawnanywhere.setrespawn", 2))
                        .executes(context -> setLocation(context, SpawnAnywhereData::withRespawnLocation, "respawn"))
                )
        );
    }

    private static int setLocation(CommandContext<CommandSourceStack> context, BiFunction<SpawnAnywhereData, Location, SpawnAnywhereData> function, String type) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        Location location = new Location(player);
        SpawnAnywhere.DATA = function.apply(SpawnAnywhere.DATA, location);
        BlockPos pos = location.pos();
        context.getSource().sendSuccess(() -> Component.literal("Set %s location to %d, %d, %d [%.1f, %.1f] in %s".formatted(type, pos.getX(), pos.getY(), pos.getZ(), location.yRot(), location.xRot(), location.dimension().location())), true);
        SpawnAnywhere.save(context.getSource().getServer());
        return 1;
    }

}
