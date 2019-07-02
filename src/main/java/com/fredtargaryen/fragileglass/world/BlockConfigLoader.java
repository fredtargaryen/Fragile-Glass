package com.fredtargaryen.fragileglass.world;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.IProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class BlockConfigLoader extends ConfigLoader {

    private BlockDataManager manager;
    private HashMap<BlockState, ArrayList<FragilityData>> blockStates;

    public BlockConfigLoader(BlockDataManager manager,
                             HashMap<BlockState, ArrayList<FragilityData>> blockStates) {
        this.manager = manager;
        this.blockStates = blockStates;
    }

    /**
     * When a behaviour has been validated and confirmed usable, this method is called to conditionally add it to the
     * fragility data map.
     * The maps map to ArrayLists of crash behaviours, which are executed in the order specified in the config file.
     * No two crash behaviours in a list can be the same, i.e. you cannot have two breakages, but you can have a break
     * followed by a block change (an example being ice breaking and being immediately replaced with water).
     * @param key The ResourceLocation or block state the fragilitydatas should apply to.
     * @param fragilityData The new crash behaviour to add.
     */
    private void addNewBehaviour(BlockState key, FragilityData fragilityData) {
        if(this.blockStates.containsKey(key)) {
            ArrayList<FragilityData> dataList = this.blockStates.get(key);
            boolean allowNewBehaviour = true;
            for(FragilityData fdata : dataList) {
                if(fdata.getBehaviour() == fragilityData.getBehaviour()) allowNewBehaviour = false;
            }
            if(allowNewBehaviour) dataList.add(fragilityData);
        }
        else {
            ArrayList<FragilityData> newList = new ArrayList<>();
            newList.add(fragilityData);
            this.blockStates.put(key, newList);
        }
    }


    private List<BlockState> getAllBlockStatesForString(String states) throws ConfigLoadException {
        HashMap<String, String> description = this.getDescriptionFromString(states);
        //Get all BlockStates with the block named in splitEntryName[0]
        Block block = this.getBlockFromString(description.get("block"));
        List<BlockState> allStates = new ArrayList<>(block.getStateContainer().getValidStates());
        String propsString = description.get("properties");
        if(propsString != null) {
            //Some properties were specified so change allStates
            HashMap<IProperty<?>, ?> specifiedProperties = this.parseStringPropertyMap(block.getDefaultState(), this.getStringPropertyMapFrom(propsString));
            for(IProperty<?> iprop : specifiedProperties.keySet()) {
                allStates = allStates.stream()
                        .filter(state -> state.get(iprop) == specifiedProperties.get(iprop))
                        .collect(Collectors.toList());
            }
        }
        return allStates;
    }
    
    @Override
    protected void parseLine() throws ConfigLoadException {
        String[] values = this.line.split(" ");
        //Validate number of values on row
        if(values.length < 4) {
            throw new ConfigLoadException("There must be at least 4 values here.");
        }
        else {
            List<BlockState> states = this.getAllBlockStatesForString(values[0]);
            //Validate first value
            if(states.isEmpty()) {
                throw new ConfigLoadException("No BlockStates were found for the description '" + values[0] + "'.");
            } else {
                try {
                    //Validate behaviour value
                    BlockDataManager.FragileBehaviour behaviour = BlockDataManager.FragileBehaviour.valueOf(values[1]);
                    //Validate minSpeed and silently clamp to >= 0
                    double minSpeed = Math.max(Double.parseDouble(values[2]), 0.0);
                    //Validate updateDelay and silently clamp to >= 0
                    int updateDelay = Math.max(Integer.parseInt(values[3]), 0);
                    //For all the states this line describes, work out the new state to transform to then add the
                    //behaviour.
                    for(BlockState state: states) {
                        //Validate newState
                        BlockState newState = this.getNewStateFromOldAndString(state, values[4]);
                        this.addNewBehaviour(
                                state,
                                new FragilityData(
                                        behaviour, minSpeed, updateDelay, newState,
                                        Arrays.copyOfRange(values, 5, values.length)));
                    }
                }
                catch(NumberFormatException nfe) {
                    //Thrown when the third value can't be parsed as a Double
                    throw new ConfigLoadException(values[2] + " can't be read as a decimal number.");
                }
                catch(IllegalArgumentException iae) {
                    //Thrown when the second value is not one of the supported ones
                    throw new ConfigLoadException(values[1] + " should be 'break', 'update', 'change', 'fall' or 'mod'.");
                }
            }
        }
    }
}
