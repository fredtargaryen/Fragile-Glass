package com.fredtargaryen.fragileglass.command;

import com.fredtargaryen.fragileglass.FragileGlassBase;
import com.fredtargaryen.fragileglass.config.behaviour.datamanager.DataManager;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;

public class CommandsBase {
    private static final String[] MANAGER_SUGGESTIONS = new String[] {
            "blocks",
            "entities",
            "tileentities"
    };

    protected static final SuggestionProvider<CommandSource> MANAGER_SUGGESTER = (context, builder) -> {
        ISuggestionProvider.suggest(MANAGER_SUGGESTIONS, builder);
        return builder.buildFuture();
    };

    private static final String[] BEHAVIOUR_SUGGESTIONS = new String[] {
            "break",
            "change",
            "fall",
            "mod",
            "update"
    };

    protected static final SuggestionProvider<CommandSource> BEHAVIOUR_SUGGESTER = (context, builder) -> {
        ISuggestionProvider.suggest(BEHAVIOUR_SUGGESTIONS, builder);
        return builder.buildFuture();
    };

    static DataManager getDataManager(String managerString) {
        switch(managerString) {
            case "blocks":
                return FragileGlassBase.getBlockDataManager();
            case "entities":
                return FragileGlassBase.getEntityDataManager();
            case "tileentities":
                return FragileGlassBase.getTileEntityDataManager();
        }
        return null;
    }

    public static void registerCommands(CommandDispatcher<CommandSource> dispatcher) {
        ModifyCommand.register(dispatcher);
        RemoveCommand.register(dispatcher);
        ViewCommand.register(dispatcher);
        ReloadCommand.register(dispatcher);
        ExportCommand.register(dispatcher);
        RemoveAllCommand.register(dispatcher);
//        FullExportCommand.register(dispatcher);
    }

    static LiteralArgumentBuilder<CommandSource> baseCommand(String literal, Command<CommandSource> command) {
        return Commands.literal("fg"+literal)
                .requires(e -> e.hasPermissionLevel(2))
                .then(  Commands.argument("manager", StringArgumentType.word())
                        .suggests(MANAGER_SUGGESTER)
                        .executes(command));
    }

    static LiteralArgumentBuilder<CommandSource> baseCommandThen(String literal, RequiredArgumentBuilder<CommandSource, ?> restOfCommand) {
        return  Commands.literal("fg"+literal)
                .requires(e -> e.hasPermissionLevel(2))
                .then(  Commands.argument("manager", StringArgumentType.word())
                        .suggests(MANAGER_SUGGESTER)
                        .then(restOfCommand));
    }

    static ArgumentBuilder<CommandSource, ?> behaviourCommand() {
        return Commands.argument("behaviour", StringArgumentType.word())
                .suggests(BEHAVIOUR_SUGGESTER);
    }
}
