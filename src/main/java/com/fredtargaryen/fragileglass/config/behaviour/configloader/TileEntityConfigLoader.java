package com.fredtargaryen.fragileglass.config.behaviour.configloader;

import com.fredtargaryen.fragileglass.config.behaviour.data.FragilityData;
import com.fredtargaryen.fragileglass.config.behaviour.datamanager.TileEntityDataManager;
import net.minecraft.tileentity.TileEntityType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class TileEntityConfigLoader extends ConfigLoader{

    private TileEntityDataManager manager;
    private HashMap<TileEntityType, ArrayList<FragilityData>> tileEntities;

    public TileEntityConfigLoader(TileEntityDataManager manager,
                             HashMap<TileEntityType, ArrayList<FragilityData>> tileEntities) {
        this.manager = manager;
        this.tileEntities = tileEntities;
    }

    /**
     * When a behaviour has been validated and confirmed usable, this method is called to add it to the fragility data map.
     * The maps map to ArrayLists of crash behaviours, which are executed in the order specified in the config file.
     * The only thing that can stop a behaviour being added at this point, is whether a WAIT behaviour was added first.
     * @param key The ResourceLocation or block state the fragilitydatas should apply to.
     * @param fragilityData The new crash behaviour to add.
     */
    private void tryAddNewBehaviour(TileEntityType key, FragilityData fragilityData) throws ConfigLoadException {
        if(this.tileEntities.containsKey(key)) {
            ArrayList<FragilityData> dataList = this.tileEntities.get(key);
            if(fragilityData.canBeQueued())
            {
                //Add, no question
                dataList.add(fragilityData);
            }
            else {
                // Check for a WAIT behaviour
                boolean add = false;
                for (FragilityData fd : dataList) {
                    add |= fd.getBehaviour() == FragilityData.FragileBehaviour.WAIT;
                }
                if (add) {
                    dataList.add(fragilityData);
                } else {
                    throw new ConfigLoadException("This behaviour type can't be added when a wait behaviour has been previously added.\nIt may depend on data which may not exist by the time the wait is over.\nCheck the config file for more information on wait.");
                }
            }
        }
        else {
            ArrayList<FragilityData> newList = new ArrayList<>();
            newList.add(fragilityData);
            this.tileEntities.put(key, newList);
        }
    }

    @Override
    protected void parseLine() throws ConfigLoadException {
        String[] values = line.split(" ");
        //Validate number of values on row
        if(values.length < 3) {
            throw new ConfigLoadException("There must be at least 3 values here.");
        }
        else {
            //Check the first value is a ResourceLocation in the Forge TileEntityType registry, i.e. refers to a valid tile entity
            TileEntityType entry = KeyParser.getTileEntityTypeForString(values[0]);
            if(entry == null) {
                throw new ConfigLoadException(values[0] + " has the wrong format; please see the examples.");
            } else {
                try {
                    //Validate behaviour value
                    FragilityData.FragileBehaviour behaviour = FragilityData.FragileBehaviour.valueOf(values[1].toUpperCase());
                    //Validate minSpeed and silently clamp to >= 0
                    double minSpeed = Math.max(Double.parseDouble(values[2]), 0.0);

                    FragilityData newData = this.createDataFromBehaviour(behaviour, minSpeed);
                    newData.parseExtraData(null, this, Arrays.copyOfRange(values, 3, values.length));
                    this.tryAddNewBehaviour(entry, newData);
                }
                catch(FragilityData.FragilityDataParseException fdpe) {
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
