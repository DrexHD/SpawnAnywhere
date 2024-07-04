package me.drex.spawnanywhere.mixin;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Commands.class)
public class CommandsMixin {
    @Redirect(
        method = "<init>",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/commands/SetWorldSpawnCommand;register(Lcom/mojang/brigadier/CommandDispatcher;)V"
        )
    )
    private void disableSetWorldSpawnCommand(CommandDispatcher<CommandSourceStack> commandDispatcher) {
    }
}
