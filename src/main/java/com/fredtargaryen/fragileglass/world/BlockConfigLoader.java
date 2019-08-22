package com.fredtargaryen.fragileglass.world;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.IProperty;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;

import java.util.*;
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


    private List<BlockState> getAllBlockStatesForString(String states) {
        HashMap<String, String> description = this.getDescriptionFromString(states);
        List<BlockState> allStates = new ArrayList<>();
        Collection<Block> blocks;
        String tag = description.get("tag");
        if(tag == null) {
            //Represents a single block
            //Get all BlockStates with the block named in splitEntryName[0]
            Block block = this.getBlockFromString(description.get("block"));
            blocks = new ArrayList<>();
            blocks.add(block);
        }
        else {
            //Represents the set of blocks under the given tag
            blocks = BlockTags.getCollection().getOrCreate(new ResourceLocation(tag)).getAllElements();
        }
        String propsString = description.get("properties");
        for(Block block : blocks) {
            //Get all valid states of the block
            Collection<BlockState> filteredStates = block.getStateContainer().getValidStates();
            if (propsString != null) {
                //Some properties were specified so change filteredStates
                //Get the properties specified by the variant text in the config file
                HashMap<IProperty<?>, ?> specifiedProperties = this.parseStringPropertyMap(
                        block.getDefaultState(), this.getStringPropertyMapFrom(propsString));
                for (IProperty<?> iprop : specifiedProperties.keySet()) {
                    //For each property, filter the states and keep the ones which have the same value for that property
                    filteredStates = filteredStates.stream()
                            .filter(state -> state.get(iprop) == specifiedProperties.get(iprop))
                            .collect(Collectors.toList());
                }
            }
            allStates.addAll(filteredStates);
        }
        return allStates;
    }
    
    @Override
    protected void parseLine() throws ConfigLoadException {
        String[] values = this.line.split(" ");
        //Validate number of values on row
        if(values.length < 5) {
            throw new ConfigLoadException("There must be at least 5 values here.");
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
                    throw new ConfigLoadException(values[1] + " should be 'BREAK', 'UPDATE', 'CHANGE', 'FALL' or 'MOD'.");
                }
            }
        }
    }
}
