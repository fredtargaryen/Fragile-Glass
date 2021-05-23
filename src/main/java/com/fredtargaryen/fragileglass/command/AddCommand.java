package com.fredtargaryen.fragileglass.command;

import com.fredtargaryen.fragileglass.config.behaviour.configloader.ConfigLoader;
import com.fredtargaryen.fragileglass.config.behaviour.datamanager.DataManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;

public class AddCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(CommandsBase.baseCommandThen("add",
                Commands.argument("configline", StringArgumentType.string())
                        .then(Commands.argument("behaviour_number", IntegerArgumentType.integer(1))
                                .executes(e -> execute(
                                        e.getSource(),
                                        StringArgumentType.getString(e,"manager"),
                                        StringArgumentType.getString(e, "configline"),
                                        IntegerArgumentType.getInteger(e, "behaviour_number"))))
                .executes(e -> execute(
                        e.getSource(),
                        StringArgumentType.getString(e,"manager"),
                        StringArgumentType.getString(e, "configline"),
                        -1))));
    }

    private static int execute(CommandSource source, String manager, String configLine, int changeIndex) {
        DataManager dm = CommandsBase.getDataManager(manager);
        if(dm == null) {
            return 1;
        }
        try {
            dm.parseConfigLine(configLine, true, changeIndex);
            return 0;
        }
        catch(ConfigLoader.ConfigLoadException cle) {
            source.sendFeedback(new StringTextComponent(cle.getMessage()), true);
            return 1;
        }
    }
}
