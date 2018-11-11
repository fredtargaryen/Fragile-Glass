package com.fredtargaryen.fragileglass.world;

import com.google.common.base.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class FragilityConfigLoader {
    private FragilityDataManager manager;
    private HashMap<IBlockState, FragilityData> blockStates;
    private HashMap<String, FragilityData> tileEntities;

    public FragilityConfigLoader(FragilityDataManager manager,
                                 HashMap<IBlockState, FragilityData> blockStates,
                                 HashMap<String, FragilityData> tileEntities) {
        this.manager = manager;
        this.blockStates = blockStates;
        this.tileEntities = tileEntities;
    }

    private void addBlockStates(String entryName, FragilityDataManager.FragileBehaviour behaviour,
                                double breakSpeed, int updateDelay, IBlockState newState, String[] extraData) {
        String[] splitEntryName = entryName.split("\\[");
        Block b = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(entryName));
        List<IBlockState> allOldStates = new ArrayList<>(b.getBlockState().getValidStates());
        if(splitEntryName.length == 2) {
            //Some properties were specified so change allOldStates
            HashMap<IProperty<?>, ?> specifiedProperties = this.obtainSpecifiedProperties(b, splitEntryName[1].split("\\]")[0]);
            for(IProperty<?> iprop : specifiedProperties.keySet()) {
                allOldStates = allOldStates.stream()
                        .filter(state -> state.getValue(iprop) == specifiedProperties.get(iprop))
                        .collect(Collectors.toList());
            }
        }
        for(IBlockState oldState : allOldStates) {
            this.blockStates.put(oldState, new FragilityData(behaviour, breakSpeed, updateDelay, newState, extraData));
        }
    }

    public void loadFile(BufferedReader br) throws FragilityConfigLoadException, IOException {
        String line;
        int lineNumber = -1;
        while ((line = br.readLine()) != null) {
            ++lineNumber;
            if(!line.equals("") && line.charAt(0) != '#') {
                //Line is supposed to be read
                String[] values = line.split(" ");
                //Validate number of values on row
                if(values.length < 4) {
                    throw new FragilityConfigLoadException("There must be at least 4 values here.", line, lineNumber);
                }
                else {
                    //Validate first value
                    if(!this.validateEntryName(values[0])) {
                        throw new FragilityConfigLoadException(values[0] + " has the wrong format; please see the examples.", line, lineNumber);
                    } else {
                        try {
                            //Validate behaviour value
                            FragilityDataManager.FragileBehaviour behaviour = FragilityDataManager.FragileBehaviour.valueOf(values[1]);
                            //Validate minSpeed and silently clamp to >= 0
                            double minSpeed = Math.max(Double.parseDouble(values[2]), 0.0);
                            //Validate updateDelay and silently clamp to >= 0
                            int updateDelay = Math.max(Integer.parseInt(values[3]), 0);
                            //Validate newState
                            IBlockState newState = null;
                            if(!values[4].equals("-")) {
                                if(!this.validateEntryName(values[4])) {
                                    throw new FragilityConfigLoadException(values[4] + " has the wrong format; please see the examples.", line, lineNumber);
                                }
                            }
                            //Determine which registry to add the data to
                            if(this.manager.isResourceLocationValidBlock(values[0].split("\\[")[0])) {
                                //It's a block or blockstate
                                this.addBlockStates(values[0], behaviour, minSpeed, updateDelay, newState,
                                        Arrays.copyOfRange(values, 5, values.length));
                            }
                            else {
                                //It may or may not be a tile entity, but cannot validate this at this point
                                this.tileEntities.put(values[0], new FragilityData(
                                        behaviour, minSpeed, updateDelay, newState,
                                        Arrays.copyOfRange(values, 5, values.length)));
                            }
                        }
                        catch(NumberFormatException nfe) {
                            //Thrown when the third value can't be parsed as a Double
                            throw new FragilityConfigLoadException(values[2] + " can't be read as a decimal number.", line, lineNumber);
                        }
                        catch(IllegalArgumentException iae) {
                            //Thrown when the second value is not one of the supported ones
                            throw new FragilityConfigLoadException(values[1] + " should be 'break', 'update', 'change' or 'mod'.", line, lineNumber);
                        }
                    }
                }
            }
        }
    }

    /**
     * @param name the string representing the potential block or block state
     * @param block true if should check if name is a block; false if should check if name is a block state
     * @return true if the name represents the type asked for
     */
    private boolean isValidBlockOrBlockState(String name, boolean block) {
        String resLocRegex = "[a-z]+:[a-z]+";
        String variantRegex = "[a-z]+=([0-9]+|[a-z]+)";
        String variantsRegex = "(" + variantRegex + ",)*(" + variantRegex + ")";
        String blockStateRegex = resLocRegex + "\\[" + variantsRegex + "\\]";
        return block ? name.matches(resLocRegex) : name.matches(blockStateRegex);
    }

    public class FragilityConfigLoadException extends Exception {
        public FragilityConfigLoadException(String message, String badLine, int lineNumber) {
            super("Could not load the .cfg file because of line "+lineNumber+":\n" + badLine +"\n" + message +
                    "Default fragility data will be loaded. No changes to the file will take effect.");
        }
    }

    private HashMap<IProperty<?>, ?> obtainSpecifiedProperties(Block block, String propertiesString) {
        IBlockState state = block.getDefaultState();
        String[] variantInfo = propertiesString.split(",");
        Collection<IProperty<?>> keys = state.getPropertyKeys();
        HashMap<IProperty<?>, ?> properties = new HashMap<>();
        for (String variant : variantInfo) {
            String[] info = variant.split("=");
            for (IProperty<?> iprop : keys) {
                if (iprop.getName().equals(info[0])) {
                    state = this.parseAndAddProperty(properties, state, iprop, info[1]);
                }
            }
        }
        return properties;
    }

    private <T extends Comparable<T>> IBlockState parseAndAddProperty(HashMap properties, IBlockState state, IProperty<T> iprop, String value) {
        if(iprop instanceof PropertyBool) {
            PropertyBool pb = (PropertyBool) iprop;
            Optional<Boolean> opt = pb.parseValue(value);
            if(opt.isPresent()) properties.put(pb, opt.get());
        }
        else if(iprop instanceof PropertyInteger) {
            PropertyInteger pi = (PropertyInteger) iprop;
            Optional<Integer> opt = pi.parseValue(value);
            if(opt.isPresent()) properties.put(pi, opt.get());
        }
        else if(iprop instanceof PropertyEnum) {
            PropertyEnum pe = (PropertyEnum) iprop;
            Optional<Enum> opt = pe.parseValue(value);
            if(opt.isPresent()) properties.put(pe, opt.get());
        }
        return state;
    }

    /**
     * Validate the TileEntity ResourceLocation String (for tile entities), Block ResourceLocation String (for blocks),
     * or string description of applicable BlockStates (for block states).
     * Currently simple regex validation; not checking the names have been registered and this is probably impossible.
     * @param entryName
     * @return true iff entryName matches the regex for tile entities, blocks or block states.
     */
    private boolean validateEntryName(String entryName) {
        return this.isValidBlockOrBlockState(entryName, true)
                || this.isValidBlockOrBlockState(entryName, false);
    }
}
