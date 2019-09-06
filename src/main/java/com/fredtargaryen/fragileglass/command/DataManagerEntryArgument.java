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

public class DataManagerEntryArgument implements ArgumentType<DataManagerEntryArgument.DataManagerEntry> {
    public static DataManagerEntryArgument blockStateSet() { return new DataManagerEntryArgument(); }
    public static DataManagerEntryArgument entityTypeSet() { return new DataManagerEntryArgument(); }
    public static DataManagerEntryArgument tileEntityType() { return new DataManagerEntryArgument(); }

    @Override
    public DataManagerEntry parse(StringReader reader) throws CommandSyntaxException {
        String s = reader.getString();
        DataManagerEntry dme = new DataManagerEntry();
        try {
            //Try to parse as a set of BlockStates
            List<BlockState> states = KeyParser.getAllBlockStatesForString(s);
            if(states.isEmpty()) {
                //Try to parse as a set of EntityTypes
                Collection<EntityType<?>> entityTypes = KeyParser.getAllEntityTypesForString(s);
                if(entityTypes.isEmpty()) {
                    //Try to parse as a TileEntityType
                    TileEntityType tet = KeyParser.getTileEntityTypeForString(s);
                    if(tet == null) {
                        throw new SimpleCommandExceptionType(new StringTextComponent("String does not correctly describe blockstates, entities or tileentities")).create();
                    }
                    else {
                        dme.tileEntityType = tet;
                    }
                }
                else {
                    dme.entityTypeSet = entityTypes;
                }
            }
            else {
                dme.blockStateSet = states;
            }
        }
        catch(Exception e) {
            throw new SimpleCommandExceptionType(new StringTextComponent("No EntityTypes found for this string.")).create();
        }
        return null;
    }

    public static DataManagerEntry getEntry(CommandContext<CommandSource> context, String name) {
        return context.getArgument(name, DataManagerEntry.class);
    }

    public class DataManagerEntry {
        private List<BlockState> blockStateSet;
        private Collection<EntityType<?>> entityTypeSet;
        private TileEntityType tileEntityType;

        public List<BlockState> getBlockStateSet() { return blockStateSet; }

        public Collection<EntityType<?>> getEntityTypeSet() { return entityTypeSet; }

        public TileEntityType getTileEntityType() { return tileEntityType; }
    }
}
