package com.fredtargaryen.fragileglass.command;

import com.fredtargaryen.fragileglass.config.behaviour.data.FragilityData;
import com.fredtargaryen.fragileglass.config.behaviour.datamanager.DataManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

import javax.annotation.Nullable;

public class RemoveCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(CommandsBase.baseCommandThen("remove",
                Commands.argument("entry", DataManagerEntryArgument.entry())
                        .then(  CommandsBase.behaviourCommand()
                                .executes(e -> execute(
                                    e.getSource(),
                                    StringArgumentType.getString(e,"manager"),
                                    DataManagerEntryArgument.getEntry(e, "entry"),
                                    FragilityData.parseBehaviour(StringArgumentType.getString(e, "behaviour")))))
                        .executes(e -> execute(
                                e.getSource(),
                                StringArgumentType.getString(e, "manager"),
                                DataManagerEntryArgument.getEntry(e, "entry"),
                                null))));
    }

    private static int execute(CommandSource source, String manager, DataManagerEntry entry, @Nullable FragilityData.FragileBehaviour behaviour) {
        DataManager dm = CommandsBase.getDataManager(manager);
        if(dm == null) {
            return 1;
        }
        if(manager.equals("blocks")) {
            entry.getBlockStateSet().forEach(state ->
                    dm.removeBehaviour(state, behaviour));
        }
        else if(manager.equals("entities")) {
            entry.getEntityTypeSet().forEach(type ->
                    dm.removeBehaviour(type, null));
        }
        else {
            //"tileentities"
            dm.removeBehaviour(entry.getTileEntityType(), behaviour);
        }
        return 0;
    }
}
