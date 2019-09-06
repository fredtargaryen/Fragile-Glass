package com.fredtargaryen.fragileglass.command;

import com.fredtargaryen.fragileglass.config.behaviour.configloader.KeyParser;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.block.BlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.EntityType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.StringTextComponent;

import java.util.Collection;
import java.util.List;

public class DataManagerEntryArgument implements ArgumentType<DataManagerEntry> {
    public static DataManagerEntryArgument entry() { return new DataManagerEntryArgument(); }

    @Override
    public DataManagerEntry parse(StringReader reader) throws CommandSyntaxException {
        //TODO Currently the first value of a command is always "/fgxxx", the second is always "blocks" or "entities" or
        //TODO "tileentities". This argument is always the third one if it appears so FOR NOW this is ok. May need a
        //TODO field later on explaining what position it is on.
        String[] values = reader.getString().split(" ");
        String manager = values[1];
        String entry = values[2];
        DataManagerEntry dme = new DataManagerEntry();
        CommandSyntaxException cse = new SimpleCommandExceptionType(new StringTextComponent("No "+manager+" found matching this string")).create();
        if(manager.equals("blocks")) {
            //Try to parse as a set of BlockStates
            try {
                List<BlockState> states = KeyParser.getAllBlockStatesForString(entry);
                if (states.isEmpty()) {
                    throw cse;
                } else {
                    dme.setBlockStateSet(states);
                }
            }
            catch(Exception e) {
                throw cse;
            }
        }
        else if(manager.equals("entities")) {
            //Try to parse as a set of EntityTypes
            try {
                Collection<EntityType<?>> entityTypes = KeyParser.getAllEntityTypesForString(entry);
                if (entityTypes.isEmpty()) {
                    throw cse;
                }
                else {
                    dme.setEntityTypeSet(entityTypes);
                }
            } catch (Exception e) {
                throw cse;
            }
        }
        else if(manager.equals("tileentities")){
            //Try to parse as a TileEntityType
            TileEntityType tet = KeyParser.getTileEntityTypeForString(entry);
            if(tet == null) {
                throw cse;
            }
            else {
                dme.setTileEntityType(tet);
            }
        }
        while(reader.canRead() && reader.peek() != ' ') reader.skip();
        return dme;
    }

    public static DataManagerEntry getEntry(CommandContext<CommandSource> context, String name) {
        return context.getArgument(name, DataManagerEntry.class);
    }
}
