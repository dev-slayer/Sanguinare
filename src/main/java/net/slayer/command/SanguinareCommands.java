package net.slayer.command;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.*;
import static net.slayer.SanguinareMain.getSanguinareStatus;
import static net.slayer.SanguinareMain.setSanguinareStatus;

public class SanguinareCommands {

    public static void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("sanguinare")
                .executes(context -> {
                    context.getSource().sendMessage(Text.literal("Running Sanguinare v1.0.0, for more commands run /sanguinare help"));
                    return 1;
                })));

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("sanguinare")
                .then(literal("status").executes(context -> {
                    if (getSanguinareStatus(context.getSource().getPlayer())) {
                        context.getSource().sendMessage(Text.literal("You are a Sanguinare"));
                    } else {
                        context.getSource().sendMessage(Text.literal("You are not a Sanguinare"));
                    }
                    return 1;
                }))));

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("sanguinare")
                .then(literal("status").then(literal("set").requires(source -> source.hasPermissionLevel(4)).then(literal("true").executes(context -> {
                    if (getSanguinareStatus(context.getSource().getPlayer())) {
                        context.getSource().sendMessage(Text.literal("You are already a Sanguinare"));
                    } else {
                        setSanguinareStatus(context.getSource().getWorld(), context.getSource().getPlayer(), true);
                        context.getSource().sendMessage(Text.literal("Set to Sanguinare"));
                    }
                    return 1;
                }))))));
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("sanguinare")
                .then(literal("status").then(literal("set").requires(source -> source.hasPermissionLevel(4)).then(literal("false").executes(context -> {
                    if (getSanguinareStatus(context.getSource().getPlayer())) {
                        setSanguinareStatus(context.getSource().getWorld(), context.getSource().getPlayer(), false);
                        context.getSource().sendMessage(Text.literal("Set to not Sanguinare"));
                    } else {
                        context.getSource().sendMessage(Text.literal("You are already not a Sanguinare"));
                    }
                    return 1;
                }))))));
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("sanguinare")
                .then(literal("status").then(literal("set").requires(source -> source.hasPermissionLevel(4)).then(literal("true").executes(context -> {
                    if (getSanguinareStatus(context.getSource().getPlayer())) {
                        context.getSource().sendMessage(Text.literal("You are already a Sanguinare"));
                    } else {
                        setSanguinareStatus(context.getSource().getWorld(), context.getSource().getPlayer(), true);
                        context.getSource().sendMessage(Text.literal("Set to Sanguinare"));
                    }
                    return 1;
                }))))));
    }
}
