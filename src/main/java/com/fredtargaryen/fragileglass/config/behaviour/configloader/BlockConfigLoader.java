package com.fredtargaryen.fragileglass.config.behaviour.configloader;

import com.fredtargaryen.fragileglass.config.behaviour.data.FragilityData;
import com.fredtargaryen.fragileglass.config.behaviour.datamanager.BlockDataManager;
import net.minecraft.block.BlockState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class BlockConfigLoader extends ConfigLoader {

    private BlockDataManager manager;
    private HashMap<BlockState, ArrayList<FragilityData>> blockStates;

    public BlockConfigLoader(BlockDataManager manager,
                             HashMap<BlockState, ArrayList<FragilityData>> blockStates) {
        this.manager = manager;
        this.blockStates = blockStates;
    }

    /**
     * When a behaviour has been validated and confirmed usable, this method is called to add it to the fragility data map.
     * The maps map to ArrayLists of crash behaviours, which are executed in the order specified in the config file.
     * The only exception to adding behaviours is that a "wait" behaviour should only precede a behaviour if canBeQueued() returns true.
     * @param key The ResourceLocation or block state the fragilitydatas should apply to.
     * @param fragilityData The new crash behaviour to add.
     * @param changeIndex The index at which to insert the behaviour. To the user, the first item is at position 1, so this value must be decremented. -1 defaults to the end of the list.
     */
    private void tryAddNewBehaviour(BlockState key, FragilityData fragilityData, int changeIndex) throws ConfigLoadException {
        if(this.blockStates.containsKey(key)) {
            ArrayList<FragilityData> dataList = this.blockStates.get(key);
            // Set changeIndex to a "List-friendly" value
            if(changeIndex == -1 || changeIndex > dataList.size()) {
                // Default to adding to the end of the list
                changeIndex = dataList.size();
            }
            else {
                changeIndex -= 1;
            }
            // Check for wait behaviours before adding
            if(fragilityData.getBehaviour() == FragilityData.FragileBehaviour.WAIT) {
                // Throw an exception if there would be any non-queueable behaviours after this wait
                // Won't run if the wait is to be added to the end, but that's ok
                for(int i = changeIndex; i < dataList.size(); i++) {
                    if(!dataList.get(i).canBeQueued()) {
                        throw new ConfigLoadException("This wait behaviour can't be added because of behaviours which would come after it.\nThese may depend on data which may not exist by the time the wait is over.\nCheck the config file for more information on wait.");
                    }
                }
            }
            else if(!fragilityData.canBeQueued()) {
                // Throw an exception if there would be any wait behaviours before this one
                for(int i = 0; i < changeIndex; i++) {
                    if(dataList.get(i).getBehaviour() == FragilityData.FragileBehaviour.WAIT) {
                        throw new ConfigLoadException("This behaviour type can't be added when a wait behaviour precedes it in the list.\nIt may depend on data which may not exist by the time the wait is over.\nCheck the config file for more information on wait.");
                    }
                }
            }
            // At this point the behaviour is either queueable whenever, or non-queueable but not preceded by any waits.
            dataList.add(changeIndex, fragilityData);
        }
        else {
            ArrayList<FragilityData> newList = new ArrayList<>();
            newList.add(fragilityData);
            this.blockStates.put(key, newList);
        }
    }

    /**
     * When a behaviour has been validated and confirmed usable, this method is called to replace an element of the fragility data map with it.
     * The maps map to ArrayLists of crash behaviours, which are executed in the order specified in the config file.
     * The only exception to modifying behaviours is that a "wait" behaviour should only precede a behaviour if canBeQueued() returns true.
     * @param key The ResourceLocation or block state the fragilitydatas should apply to.
     * @param fragilityData The new crash behaviour to add.
     * @param changeIndex The index at which to replace a behaviour. To the user, the first item is at position 1, so this value must be decremented. -1 defaults to 0.
     */
    private void tryModifyBehaviour(BlockState key, FragilityData fragilityData, int changeIndex) throws ConfigLoadException {
        if(this.blockStates.containsKey(key)) {
            ArrayList<FragilityData> dataList = this.blockStates.get(key);
            if(changeIndex > dataList.size())
            {
                throw new ConfigLoadException("Cannot modify item " + changeIndex + " as this BlockState only has " + dataList.size() + " behaviours!");
            }
            // Set changeIndex to a "List-friendly" value
            if(changeIndex == -1) {
                // Default to the first item in the list
                changeIndex = 0;
            }
            else {
                changeIndex -= 1;
            }
            // Check for wait behaviours before modifying
            if(fragilityData.getBehaviour() == FragilityData.FragileBehaviour.WAIT) {
                // Throw an exception if there would be any non-queueable behaviours after this wait
                // Won't run if the wait is to be placed at the end, but that's ok
                for(int i = changeIndex + 1; i < dataList.size(); i++) {
                    if(!dataList.get(i).canBeQueued()) {
                        throw new ConfigLoadException("This wait behaviour can't be placed here because of behaviours which would come after it.\nThese may depend on data which may not exist by the time the wait is over.\nCheck the config file for more information on wait.");
                    }
                }
            }
            else if(!fragilityData.canBeQueued()) {
                // Throw an exception if there would be any wait behaviours before this one
                for(int i = 0; i < changeIndex; i++) {
                    if(dataList.get(i).getBehaviour() == FragilityData.FragileBehaviour.WAIT) {
                        throw new ConfigLoadException("This behaviour type can't be placed here when a wait behaviour precedes it in the list.\nIt may depend on data which may not exist by the time the wait is over.\nCheck the config file for more information on wait.");
                    }
                }
            }
            // At this point the behaviour is either queueable whenever, or non-queueable but not preceded by any waits.
            dataList.set(changeIndex, fragilityData);
        }
        else {
            throw new ConfigLoadException("No behaviours exist for this BlockState, so there is nothing to modify.");
        }
    }
    
    @Override
    protected void parseLine(boolean add, int changeIndex) throws ConfigLoadException {
        String[] values = this.line.split(" ");
        //Validate number of values on row
        if(values.length < 3) {
            throw new ConfigLoadException("There must be at least 3 values here.");
        }
        else {
            List<BlockState> states = KeyParser.getAllBlockStatesForString(values[0]);
            //Validate first value
            if(states.isEmpty()) {
                throw new ConfigLoadException("No BlockStates were found for the description '" + values[0] + "'.");
            } else {
                try {
                    //Validate behaviour value
                    FragilityData.FragileBehaviour behaviour = FragilityData.FragileBehaviour.valueOf(values[1].toUpperCase());
                    //Validate minSpeed and silently clamp to >= 0
                    double minSpeed = Math.max(Double.parseDouble(values[2]), 0.0);

                    //For all the states this line describes, compute and add the behaviour.
                    for(BlockState state: states) {
                        FragilityData newData = this.createDataFromBehaviour(behaviour, minSpeed);
                        newData.parseExtraData(state, this, Arrays.copyOfRange(values, 3, values.length));
                        if(add) {
                            this.tryAddNewBehaviour(state, newData, changeIndex);
                        }
                        else {
                            this.tryModifyBehaviour(state, newData, changeIndex);
                        }
                    }
                }
                catch(FragilityData.FragilityDataParseException fdpe) {
                    //Thrown when the FragilityData subclass can't parse an extra parameter.
                    //Contains just a message; wrap it in a ConfigLoadException that is handled normally
                    throw new ConfigLoadException(fdpe.getMessage());
                }
                catch(NumberFormatException nfe) {
                    //Thrown when the third value can't be parsed as a Double
                    throw new ConfigLoadException(values[2] + " can't be read as a decimal number.");
                }
                catch(IllegalArgumentException iae) {
                    //Thrown when the second value is not one of the supported ones
                    throw new ConfigLoadException(values[1] + " should be break, change, command, damage, explode, fall, mod, update or wait.");
                }
            }
        }
    }
}
