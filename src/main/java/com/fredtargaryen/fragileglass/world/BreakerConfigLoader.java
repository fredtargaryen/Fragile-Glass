package com.fredtargaryen.fragileglass.world;

import com.fredtargaryen.fragileglass.DataReference;
import net.minecraft.entity.EntityType;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

public class BreakerConfigLoader {
    //REGEX CONSTANT
    private static final String RES_LOC_REGEX = "[a-z]+:[a-z|_]+";

    private BreakerDataManager manager;
    private HashMap<EntityType, BreakerData> entities;

    public BreakerConfigLoader(BreakerDataManager manager, HashMap<EntityType, BreakerData> entities) {
        this.manager = manager;
        this.entities = entities;
    }

    public void loadFile(BufferedReader br, String filename) throws BreakerConfigLoadException, IOException {
        String line;
        int lineNumber = 0;
        while ((line = br.readLine()) != null) {
            ++lineNumber;
            if(!line.equals("") && line.charAt(0) != '#') {
                //Line is supposed to be read
                String[] values = line.split(" ");
                //Validate number of values on row
                if(values.length < 3) {
                    throw new BreakerConfigLoadException(filename, "There must be at least 3 values here.", line, lineNumber);
                }
                else {
                    //Validate first value
                    if(!this.validateEntryName(values[0])) {
                        throw new BreakerConfigLoadException(filename, values[0] + " has the wrong format; please see the examples.", line, lineNumber);
                    } else {
                        try {
                            //Validate minSpeed and silently clamp to >= 0
                            double minSpeedSquared = Double.parseDouble(values[1]);
                            minSpeedSquared = Math.max(minSpeedSquared * minSpeedSquared, 0.0);
                            //Validate maxSpeed and silently clamp to <= max speed
                            double maxSpeedSquared = Double.parseDouble(values[2]);
                            maxSpeedSquared = Math.min(maxSpeedSquared * maxSpeedSquared, DataReference.MAXIMUM_ENTITY_SPEED_SQUARED);
                            //Ensure minSpeed <= maxSpeed. If not, silently swap the values
                            if(minSpeedSquared > maxSpeedSquared) {
                                double temp = minSpeedSquared;
                                minSpeedSquared = maxSpeedSquared;
                                maxSpeedSquared = temp;
                            }
                            EntityType entry = this.manager.getEntityType(values[0]);
                            if(entry != null) {
                                //It's a valid entity
                                this.entities.put(entry, new BreakerData(minSpeedSquared, maxSpeedSquared,
                                                Arrays.copyOfRange(values, 3, values.length)));
                            }
                        }
                        catch(NumberFormatException nfe) {
                            //Thrown when speed values can't be parsed as Doubles
                            throw new BreakerConfigLoadException(filename, "One of your speed values can't be read as a decimal number.", line, lineNumber);
                        }
                    }
                }
            }
        }
    }

    public class BreakerConfigLoadException extends Exception {
        public BreakerConfigLoadException(String filename, String message, String badLine, int lineNumber) {
            super("Could not load " + filename + " because of line " + lineNumber + ":\n" + badLine +"\n" + message +
                    " Default breaker data will be loaded. The file will not be changed.");
        }
    }

    /**
     * Validate the Entity ResourceLocation String.
     * Currently simple regex validation; not checking the names have been registered and this is probably impossible.
     * @param entryName
     * @return true iff entryName matches the regex for entities.
     */
    private boolean validateEntryName(String entryName) {
        return entryName.matches(RES_LOC_REGEX);
    }
}
