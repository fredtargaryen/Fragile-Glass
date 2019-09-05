package com.fredtargaryen.fragileglass.command;

import com.fredtargaryen.fragileglass.config.behaviour.configloader.ConfigLoader;
import com.fredtargaryen.fragileglass.config.behaviour.data.FragilityData;
import com.fredtargaryen.fragileglass.config.behaviour.datamanager.DataManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockStateArgument;
import net.minecraft.command.arguments.BlockStateInput;
import net.minecraft.util.text.StringTextComponent;

public class RemoveCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(CommandsBase.baseCommandThen("remove",
                Commands.argument("entry", BlockStateArgument.blockState())
                        .then(Commands.argument("behaviour", StringArgumentType.word()))
                        .executes(e -> execute(
                                e.getSource(),
                                StringArgumentType.getString(e,"manager"),
                                BlockStateArgument.getBlockState(e, "entry"),
                                StringArgumentType.getString(e, "behaviour")))));
    }

    private static int execute(CommandSource source, String manager, BlockStateInput entry, String behaviour) {
        DataManager dm = CommandsBase.getDataManager(manager);
        if(dm == null) {
            return 1;
        }
        dm.removeBehaviour(entry.getState(), FragilityData.FragileBehaviour.valueOf(behaviour.toUpperCase()));
        return 0;
    }
}
