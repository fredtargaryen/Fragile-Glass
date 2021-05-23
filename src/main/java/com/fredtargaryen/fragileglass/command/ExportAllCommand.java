package com.fredtargaryen.fragileglass.command;

import com.fredtargaryen.fragileglass.config.behaviour.configloader.KeyParser;
import com.fredtargaryen.fragileglass.config.behaviour.data.FragilityData;
import com.fredtargaryen.fragileglass.config.behaviour.datamanager.DataManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.EntityType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.StringTextComponent;

import java.io.IOException;
import java.util.List;

public class ExportAllCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(CommandsBase.baseCommand("exportall", e -> execute(
                                e.getSource(),
                                StringArgumentType.getString(e, "manager"))));
    }

    private static int execute(CommandSource source, String manager) {
        DataManager dm = CommandsBase.getDataManager(manager);
        if(dm == null) {
            return 1;
        }
        StringBuilder sb = new StringBuilder();
        if(manager.equals("blocks")) {
            dm.getKeys().forEach(state -> {
                try {
                    List<FragilityData> fdList = (List<FragilityData>) dm.getData(state);
                    fdList.forEach(fragilityData -> {
                        sb.append(dm.stringifyBehaviours(state, null, false));
                        sb.append("\n");
                    });
                } catch (NullPointerException npe) {
                    source.sendFeedback(new StringTextComponent("No existing block state data for " + KeyParser.cleanBlockStateString(state.toString())), false);
                }
            });
        }
        else if(manager.equals("entities")) {
            dm.getKeys().forEach(type -> {
                try {
                    sb.append(dm.stringifyBehaviours(type, null, false));
                    sb.append("\n");
                } catch (NullPointerException npe) {
                    source.sendFeedback(new StringTextComponent("No existing entity data for " + ((EntityType) type).getRegistryName()), false);
                }
            });
        }
        else {
            //"tileentities"
            dm.getKeys().forEach(type -> {
                try {
                    List<FragilityData> fdList = (List<FragilityData>) dm.getData(type);
                    fdList.forEach(fragilityData -> {
                        sb.append(dm.stringifyBehaviours(type, null, false));
                        sb.append("\n");
                    });
                } catch (NullPointerException npe) {
                    source.sendFeedback(new StringTextComponent("No existing tile entity data for " + ((TileEntityType) type).getRegistryName()), false);
                }
            });
        }
        try {
            dm.export(sb.toString());
            source.sendFeedback(new StringTextComponent("File exported successfully!"), true);
            return 0;
        }
        catch (IOException ioe) {
            source.sendFeedback(new StringTextComponent("File failed to export."), true);
            return 1;
        }
    }
}
