package com.fredtargaryen.fragileglass.command;

import com.fredtargaryen.fragileglass.config.behaviour.configloader.ConfigLoader;
import com.fredtargaryen.fragileglass.config.behaviour.datamanager.DataManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;

public class ModifyCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(CommandsBase.baseCommandThen("modify",
                Commands.argument("configline", StringArgumentType.string())
                .executes(e -> execute(
                        e.getSource(),
                        StringArgumentType.getString(e,"manager"),
                        StringArgumentType.getString(e, "configline")))));
    }

    private static int execute(CommandSource source, String manager, String configLine) {
        DataManager dm = CommandsBase.getDataManager(manager);
        if(dm == null) {
            return 1;
        }
        try {
            dm.parseConfigLine(configLine);
            return 0;
        }
        catch(ConfigLoader.ConfigLoadException cle) {
            source.sendFeedback(new StringTextComponent(cle.getMessage()), true);
            return 1;
        }
    }
}
