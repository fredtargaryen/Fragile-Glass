package com.fredtargaryen.fragileglass.command;

import com.fredtargaryen.fragileglass.config.behaviour.configloader.KeyParser;
import com.fredtargaryen.fragileglass.config.behaviour.data.FragilityData;
import com.fredtargaryen.fragileglass.config.behaviour.datamanager.DataManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nullable;

public class ViewCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(CommandsBase.baseCommandThen("view",
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
            entry.getBlockStateSet().forEach(state -> {
                try {
                    source.sendFeedback(new StringTextComponent(dm.stringifyBehaviours(state, behaviour, true)), false);
                } catch (NullPointerException npe) {
                    source.sendFeedback(new StringTextComponent("No existing block state data for " + KeyParser.cleanBlockStateString(state.toString())), false);
                }
            });
        }
        else if(manager.equals("entities")) {
            entry.getEntityTypeSet().forEach(type -> {
                try {
                    source.sendFeedback(new StringTextComponent(dm.stringifyBehaviours(type, null, false)), false);
                } catch (NullPointerException npe) {
                    source.sendFeedback(new StringTextComponent("No existing entity data for " + type.getRegistryName()), false);
                }
            });
        }
        else {
            //"tileentities"
            try {
                source.sendFeedback(new StringTextComponent(dm.stringifyBehaviours(entry.getTileEntityType(), behaviour, true)), false);
            }
            catch(NullPointerException npe) {
                source.sendFeedback(new StringTextComponent("No existing tile entity data for "+entry.getTileEntityType().getRegistryName()), false);
            }
        }
        return 0;
    }
}
