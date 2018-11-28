package com.fredtargaryen.fragileglass.world;

import com.fredtargaryen.fragileglass.DataReference;
import com.google.common.base.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class BreakerConfigLoader {
    //REGEX CONSTANT
    private static final String RES_LOC_REGEX = "[a-z]+:[a-z|_]+";

    private BreakerDataManager manager;
    private HashMap<EntityEntry, BreakerData> entities;

    public BreakerConfigLoader(BreakerDataManager manager, HashMap<EntityEntry, BreakerData> entities) {
        this.manager = manager;
        this.entities = entities;
    }

    public void loadFile(BufferedReader br) throws BreakerConfigLoadException, IOException {
        String line;
        int lineNumber = 0;
        while ((line = br.readLine()) != null) {
            ++lineNumber;
            if(!line.equals("") && line.charAt(0) != '#') {
                //Line is supposed to be read
                String[] values = line.split(" ");
                //Validate number of values on row
                if(values.length < 3) {
                    throw new BreakerConfigLoadException("There must be at least 3 values here.", line, lineNumber);
                }
                else {
                    //Validate first value
                    if(!this.validateEntryName(values[0])) {
                        throw new BreakerConfigLoadException(values[0] + " has the wrong format; please see the examples.", line, lineNumber);
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
                            EntityEntry entry = this.manager.getEntityEntry(values[0]);
                            if(entry != null) {
                                //It's a valid entity
                                this.entities.put(entry, new BreakerData(minSpeedSquared, maxSpeedSquared,
                                                Arrays.copyOfRange(values, 3, values.length)));
                            }
                        }
                        catch(NumberFormatException nfe) {
                            //Thrown when speed values can't be parsed as Doubles
                            throw new BreakerConfigLoadException("One of your speed values can't be read as a decimal number.", line, lineNumber);
                        }
                    }
                }
            }
        }
    }

    public class BreakerConfigLoadException extends Exception {
        public BreakerConfigLoadException(String message, String badLine, int lineNumber) {
            super("Could not load the .cfg file because of line "+lineNumber+":\n" + badLine +"\n" + message +
                    " Default breaker data will be loaded. No changes to the file will take effect.");
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
