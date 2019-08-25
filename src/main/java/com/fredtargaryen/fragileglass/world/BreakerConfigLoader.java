package com.fredtargaryen.fragileglass.world;

import com.fredtargaryen.fragileglass.DataReference;
import net.minecraftforge.fml.common.registry.EntityEntry;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class BreakerConfigLoader {
    //REGEX CONSTANT
    private static final String RES_LOC_REGEX = "[a-z]+:[a-z|_]+";

    private BreakerDataManager manager;
    private HashMap<EntityEntry, BreakerData> entities;

    public BreakerConfigLoader(BreakerDataManager manager, HashMap<EntityEntry, BreakerData> entities) {
        this.manager = manager;
        this.entities = entities;
    }

    public void loadFile(BufferedReader br, File configDir, String filename) throws IOException {
        String line;
        int lineNumber = 0;
        ArrayList<String> errors = new ArrayList<>();

        //Delete previous error file
        String errorFileName = configDir.getAbsolutePath() + "/ERRORS_" + filename + ".txt";
        File errorFile = new File(errorFileName);
        if(errorFile.exists()) {
            errorFile.delete();
        }

        //Read file and collect errors from invalid lines
        while ((line = br.readLine()) != null) {
            ++lineNumber;
            if(!line.equals("") && line.charAt(0) != '#') {
                try {
                    //Line is supposed to be read
                    String[] values = line.split(" ");
                    //Validate number of values on row
                    if (values.length < 3) {
                        throw new BreakerConfigLoadException(filename, "There must be at least 3 values here.", line, lineNumber);
                    } else {
                        //Validate first value
                        if (!this.validateEntryName(values[0])) {
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
                                if (minSpeedSquared > maxSpeedSquared) {
                                    double temp = minSpeedSquared;
                                    minSpeedSquared = maxSpeedSquared;
                                    maxSpeedSquared = temp;
                                }
                                EntityEntry entry = this.manager.getEntityEntry(values[0]);
                                if (entry != null) {
                                    //It's a valid entity
                                    this.entities.put(entry, new BreakerData(minSpeedSquared, maxSpeedSquared,
                                            Arrays.copyOfRange(values, 3, values.length)));
                                }
                            } catch (NumberFormatException nfe) {
                                //Thrown when speed values can't be parsed as Doubles
                                throw new BreakerConfigLoadException(filename, "One of your speed values can't be read as a decimal number.", line, lineNumber);
                            }
                        }
                    }
                }
                catch(BreakerConfigLoadException bcle) {
                    errors.add(bcle.getMessage());
                }
            }
        }
        if(!errors.isEmpty()) {
            BufferedWriter bw = new BufferedWriter(new FileWriter(errorFile));
            for(String s : errors) {
                bw.write(s + "\n");
            }
            bw.close();
        }
    }

    public class BreakerConfigLoadException extends Exception {
        public BreakerConfigLoadException(String filename, String message, String badLine, int lineNumber) {
            super("Error parsing " + filename + " line " + lineNumber + ":\n" + badLine +"\n" + message + "\n");
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
