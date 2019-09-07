package com.fredtargaryen.fragileglass.command;

import com.fredtargaryen.fragileglass.config.behaviour.configloader.KeyParser;
import com.fredtargaryen.fragileglass.config.behaviour.datamanager.DataManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;

import java.io.IOException;

public class ExportCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(CommandsBase.baseCommandThen("export",
                Commands.argument("entry", DataManagerEntryArgument.entry())
                        .executes(e -> execute(
                                e.getSource(),
                                StringArgumentType.getString(e, "manager"),
                                DataManagerEntryArgument.getEntry(e, "entry")))));
    }

    private static int execute(CommandSource source, String manager, DataManagerEntry entry) {
        DataManager dm = CommandsBase.getDataManager(manager);
        if(dm == null) {
            return 1;
        }
        StringBuilder sb = new StringBuilder();
        if(manager.equals("blocks")) {
            entry.getBlockStateSet().forEach(state -> {
                try {
                    sb.append(dm.stringifyBehaviour(state, null));
                    sb.append("\n");
                } catch (NullPointerException npe) {
                    source.sendFeedback(new StringTextComponent("No existing block state data for " + KeyParser.cleanBlockStateString(state.toString())), false);
                }
            });
        }
        else if(manager.equals("entities")) {
            entry.getEntityTypeSet().forEach(type -> {
                try {
                    sb.append(dm.stringifyBehaviour(type, null));
                    sb.append("\n");
                } catch (NullPointerException npe) {
                    source.sendFeedback(new StringTextComponent("No existing entity data for " + type.getRegistryName()), false);
                }
            });
        }
        else {
            //"tileentities"
            try {
                sb.append(dm.stringifyBehaviour(entry.getTileEntityType(), null));
                sb.append("\n");
            }
            catch(NullPointerException npe) {
                source.sendFeedback(new StringTextComponent("No existing tile entity data for "+entry.getTileEntityType().getRegistryName()), false);
            }
        }
        try {
            dm.export(sb.toString());
            source.sendFeedback(new StringTextComponent("File exported successfully!"), true);
        }
        catch (IOException ioe) {
            source.sendFeedback(new StringTextComponent("File failed to export."), true);
            return 1;
        }
        return 0;
    }
}
