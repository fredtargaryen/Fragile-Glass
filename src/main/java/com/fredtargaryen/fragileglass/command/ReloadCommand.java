package com.fredtargaryen.fragileglass.command;

import com.fredtargaryen.fragileglass.FragileGlassBase;
import com.fredtargaryen.fragileglass.config.behaviour.datamanager.DataManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandSource;

public class ReloadCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(CommandsBase.baseCommand("reload", e -> execute(
                                e.getSource(),
                                StringArgumentType.getString(e, "manager"))));
    }

    private static int execute(CommandSource source, String manager) {
        DataManager dm = CommandsBase.getDataManager(manager);
        if(dm == null) {
            return 1;
        }
        dm.clearData();
        source.sendFeedback(FragileGlassBase.setReloadStatus(dm.loadData()), true);
        return 0;
    }
}
