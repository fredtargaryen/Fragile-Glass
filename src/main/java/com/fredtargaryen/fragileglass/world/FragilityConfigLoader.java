package com.fredtargaryen.fragileglass.world;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class FragilityConfigLoader {
    //REGEX CONSTANTS
    private static final String RES_LOC_REGEX = "[a-z]+:[a-z|_]+";
    private static final String VARIANT_REGEX = "[a-z]+=([0-9]+|[a-z|_]+)";
    private static final String VARIANTS_REGEX = "(" + VARIANT_REGEX + ",)*(" + VARIANT_REGEX + ")";
    private static final String BLOCK_STATE_REGEX = RES_LOC_REGEX + "\\[" + VARIANTS_REGEX + "\\]";

    private FragilityDataManager manager;
    private HashMap<BlockState, ArrayList<FragilityData>> blockStates;
    private HashMap<String, ArrayList<FragilityData>> tileEntities;

    public FragilityConfigLoader(FragilityDataManager manager,
                                 HashMap<BlockState, ArrayList<FragilityData>> blockStates,
                                 HashMap<String, ArrayList<FragilityData>> tileEntities) {
        this.manager = manager;
        this.blockStates = blockStates;
        this.tileEntities = tileEntities;
    }

    /**
     * When a behaviour has been validated and confirmed usable, this method is called to conditionally add it to the
     * fragility data map.
     * The maps map to ArrayLists of crash behaviours, which are executed in the order specified in the config file.
     * No two crash behaviours in a list can be the same, i.e. you cannot have two breakages, but you can have a break
     * followed by a block change (an example being ice breaking and being immediately replaced with water).
     * @param map Either this.tileEntities or this.blockStates.
     * @param key The ResourceLocation or block state the fragilitydatas should apply to.
     * @param fragilityData The new crash behaviour to add.
     * @param <T> String for this.tileEntities, or BlockState for this.blockStates.
     */
    private <T> void addNewBehaviour(HashMap<T, ArrayList<FragilityData>> map, T key, FragilityData fragilityData) {
        if(map.containsKey(key)) {
            ArrayList<FragilityData> dataList = map.get(key);
            boolean allowNewBehaviour = true;
            for(FragilityData fdata : dataList) {
                if(fdata.getBehaviour() == fragilityData.getBehaviour()) allowNewBehaviour = false;
            }
            if(allowNewBehaviour) dataList.add(fragilityData);
        }
        else {
            ArrayList<FragilityData> newList = new ArrayList<>();
            newList.add(fragilityData);
            map.put(key, newList);
        }
    }

    /**
     * From addBlockStates:
     * If the (partially-specified) new BlockState has:
     * * The same Block: each specified property has the specified value; each unspecified property
     *   takes its value from the old BlockState.
     * * A different Block: each specified property has the specified value; for each unspecified
     *   property, the value in the old BlockState is taken if the two properties have the same textual name AND the
     *   value in the old BlockState is valid for the new BlockState.
     * @return newState, with the given property set according to the rules above.
     */
    private <P extends Comparable<P>> BlockState applyPropertyValue(BlockState oldState, BlockState newState, IProperty<P> iprop, HashMap newProperties) {
        if(newState.getBlock() == oldState.getBlock()) {
            if(newProperties.containsKey(iprop)) {
                newState = newState.with(iprop, (P) newProperties.get(iprop));
            }
            else {
                newState = newState.with(iprop, oldState.get(iprop));
            }
        } else {
            if(newProperties.containsKey(iprop)) {
                newState = newState.with(iprop, (P) newProperties.get(iprop));
            }
            else {
                //Find a property in oldState with the same textual name as a property here. Works around blocks having
                //different property objects which might be functionally identical.
                String ipropstring = iprop.getName();
                for(IProperty propkey : oldState.getProperties()) {
                    if(propkey.getName().equals(ipropstring)) {
                        //Found two properties with the same string name
                        String propkeystring = oldState.get(propkey).toString();
                        //Check if the value in oldState is valid in newState
                        Optional<P> opt = iprop.parseValue(propkeystring);
                        if(opt.isPresent()) {
                            //Valid value; adjust newState
                            newState = newState.with(iprop, opt.get());
                        }
                    }
                }
            }
        }
        return newState;
    }

    /**
     * Add entries to BlockStates for all applicable BlockStates.
     * @param entryName A String representing the Block, BlockState or partially-specified BlockState.
     *                  If entryName represents:
     *                  * A Block: make an entry for every BlockState of that Block.
     *                  * A BlockState: make one entry for that BlockState.
     *                  * A partially-specified BlockState: make an entry for every BlockState with the properties
     *                    specified.
     * @param behaviour The crash behaviour of the above.
     * @param breakSpeed The minimum speed required to trigger the crash behaviour.
     * @param updateDelay If the crash behaviour is "update", the delay in ticks before a block update is triggered.
     * @param newStateName If the crash behaviour is "change", the Block, BlockState or partially-specified BlockState
     *                     that a state will become.
     *                     If newStateName represents:
     *                     * A Block: the default BlockState of that Block.
     *                     * A BlockState: that exact BlockState.
     *                     * A partially-specified BlockState: a BlockState with the properties specified, but the
     *                       default value for any unspecified properties.
     *                     If the (partially-specified) new BlockState has:
     *                     * The same Block: each specified property has the specified value; each unspecified property
     *                       takes its value from the old BlockState.
     *                     * A different Block: each specified property has the specified value; for each unspecified
     *                       property, the value in the old BlockState is taken if the two properties have the same
     *                       textual name AND the value in the old BlockState is valid for the new BlockState.
     * @param extraData extra data only needed by mod Tile Entities.
     */
    private void addBlockStates(String entryName, FragilityDataManager.FragileBehaviour behaviour,
                                double breakSpeed, int updateDelay, String newStateName, String[] extraData) {
        String[] splitEntryName = entryName.split("\\[");
        //Get all BlockStates with the block named in splitEntryName[0]
        Block oldBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(splitEntryName[0]));
        List<BlockState> allOldStates = new ArrayList<>(oldBlock.getStateContainer().getValidStates());
        //Regex ensures the length will be 1 or 2. If 1, no properties were specified so use all the states.
        if(splitEntryName.length == 2) {
            //Some properties were specified so change allOldStates
            HashMap<IProperty<?>, ?> oldSpecifiedProperties = this.obtainSpecifiedProperties(oldBlock, splitEntryName[1].split("\\]")[0]);
            for(IProperty<?> iprop : oldSpecifiedProperties.keySet()) {
                allOldStates = allOldStates.stream()
                        .filter(state -> state.get(iprop) == oldSpecifiedProperties.get(iprop))
                        .collect(Collectors.toList());
            }
        }
        for(BlockState oldState : allOldStates) {
            //Compute new state, based on old state.
            String[] splitNewStateName = newStateName.split("\\[");
            //Regex ensures length will be 1 or 2
            Block newBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(splitNewStateName[0]));
            //If no properties were specified this value will be used.
            BlockState newState = newBlock.getDefaultState();
            HashMap newSpecifiedProperties = new HashMap();
            if(splitNewStateName.length == 2) {
                newSpecifiedProperties = this.obtainSpecifiedProperties(newBlock, splitNewStateName[1].split("\\]")[0]);
            }
            for(IProperty iprop : newState.getProperties()) {
                newState = this.applyPropertyValue(oldState, newState, iprop, newSpecifiedProperties);
            }
            this.addNewBehaviour(this.blockStates, oldState, new FragilityData(behaviour, breakSpeed, updateDelay, newState, extraData));
        }
    }

    public void loadFile(BufferedReader br, String filename) throws FragilityConfigLoadException, IOException {
        String line;
        int lineNumber = 0;
        while ((line = br.readLine()) != null) {
            ++lineNumber;
            if(!line.equals("") && line.charAt(0) != '#') {
                //Line is supposed to be read
                String[] values = line.split(" ");
                //Validate number of values on row
                if(values.length < 4) {
                    throw new FragilityConfigLoadException(filename, "There must be at least 4 values here.", line, lineNumber);
                }
                else {
                    //Validate first value
                    if(!this.validateEntryName(values[0])) {
                        throw new FragilityConfigLoadException(filename, values[0] + " has the wrong format; please see the examples.", line, lineNumber);
                    } else {
                        try {
                            //Validate behaviour value
                            FragilityDataManager.FragileBehaviour behaviour = FragilityDataManager.FragileBehaviour.valueOf(values[1]);
                            //Validate minSpeed and silently clamp to >= 0
                            double minSpeed = Math.max(Double.parseDouble(values[2]), 0.0);
                            //Validate updateDelay and silently clamp to >= 0
                            int updateDelay = Math.max(Integer.parseInt(values[3]), 0);
                            //Validate newState
                            BlockState newState = Blocks.AIR.getDefaultState();
                            if(!values[4].equals("-")) {
                                if(!this.validateEntryName(values[4])) {
                                    throw new FragilityConfigLoadException(filename, values[4] + " has the wrong format; please see the examples.", line, lineNumber);
                                }
                            }
                            //Determine which registry to add the data to
                            if(this.manager.isResourceLocationValidBlock(values[0].split("\\[")[0])) {
                                //It's a block or blockstate
                                this.addBlockStates(values[0], behaviour, minSpeed, updateDelay, values[4],
                                        Arrays.copyOfRange(values, 5, values.length));
                            }
                            else {
                                //It may or may not be a tile entity, but cannot validate this at this point
                                this.addNewBehaviour(this.tileEntities, values[0], new FragilityData(
                                        behaviour, minSpeed, updateDelay, newState,
                                        Arrays.copyOfRange(values, 5, values.length)));
                            }
                        }
                        catch(NumberFormatException nfe) {
                            //Thrown when the third value can't be parsed as a Double
                            throw new FragilityConfigLoadException(filename, values[2] + " can't be read as a decimal number.", line, lineNumber);
                        }
                        catch(IllegalArgumentException iae) {
                            //Thrown when the second value is not one of the supported ones
                            throw new FragilityConfigLoadException(filename, values[1] + " should be 'break', 'update', 'change', 'fall' or 'mod'.", line, lineNumber);
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
        return block ? name.matches(RES_LOC_REGEX) : name.matches(BLOCK_STATE_REGEX);
    }

    public class FragilityConfigLoadException extends Exception {
        public FragilityConfigLoadException(String filename, String message, String badLine, int lineNumber) {
            super("Could not load " + filename + " because of line " + lineNumber + ":\n" + badLine +"\n" + message +
                    " Default fragility data will be loaded. The file will not be changed.");
        }
    }

    private HashMap<IProperty<?>, ?> obtainSpecifiedProperties(Block block, @Nullable String propertiesString) {
        HashMap<IProperty<?>, ?> properties = new HashMap<>();
        if(propertiesString != null) {
            BlockState state = block.getDefaultState();
            String[] variantInfo = propertiesString.split(",");
            Collection<IProperty<?>> keys = state.getProperties();
            for (String variant : variantInfo) {
                String[] info = variant.split("=");
                for (IProperty<?> iprop : keys) {
                    if (iprop.getName().equals(info[0])) {
                        state = this.parseAndAddProperty(properties, state, iprop, info[1]);
                    }
                }
            }
        }
        return properties;
    }

    private <T extends Comparable<T>> BlockState parseAndAddProperty(HashMap properties, BlockState state, IProperty<T> iprop, String value) {
        if(iprop instanceof BooleanProperty) {
            BooleanProperty pb = (BooleanProperty) iprop;
            Optional<Boolean> opt = pb.parseValue(value);
            if(opt.isPresent()) properties.put(pb, opt.get());
        }
        else if(iprop instanceof IntegerProperty) {
            IntegerProperty pi = (IntegerProperty) iprop;
            Optional<Integer> opt = pi.parseValue(value);
            if(opt.isPresent()) properties.put(pi, opt.get());
        }
        else if(iprop instanceof EnumProperty) {
            EnumProperty pe = (EnumProperty) iprop;
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
