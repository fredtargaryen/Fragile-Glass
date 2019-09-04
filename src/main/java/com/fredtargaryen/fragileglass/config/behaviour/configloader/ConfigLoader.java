package com.fredtargaryen.fragileglass.config.behaviour.configloader;

import com.fredtargaryen.fragileglass.FragileGlassBase;
import com.fredtargaryen.fragileglass.config.behaviour.data.*;
import com.fredtargaryen.fragileglass.config.behaviour.datamanager.DataManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public abstract class ConfigLoader {
    //REGEX CONSTANTS
    private static final String RES_LOC_REGEX = "[a-z]+:[a-z|_]+";
    private static final String VARIANT_REGEX = "[a-z]+=([0-9]+|[a-z|_]+)";
    private static final String VARIANTS_REGEX = "(" + VARIANT_REGEX + ",)*(" + VARIANT_REGEX + ")";
    private static final String BLOCK_STATES_REGEX = RES_LOC_REGEX + "\\[" + VARIANTS_REGEX + "\\]";
    private static final String TAGS_RES_LOC_REGEX = "#" + RES_LOC_REGEX;
    private static final String TAGS_BLOCK_STATES_REGEX = "#" + BLOCK_STATES_REGEX;
    private static final String OLD_WITHPROPS_REGEX = "\\-\\[" + VARIANTS_REGEX + "\\]";

    protected String filename;
    protected int lineNumber;
    protected String line;

    private <P extends Comparable<P>> BlockState applyParsedProperty(BlockState state, IProperty<P> iprop, HashMap newProperties) {
        return state.with(iprop, (P) newProperties.get(iprop));
    }

    protected FragilityData createDataFromBehaviour(DataManager.FragileBehaviour behaviour, double minSpeed) {
        switch(behaviour) {
            case BREAK:
                return new BreakData(minSpeed);
            case CHANGE:
                return new ChangeData(minSpeed);
            case UPDATE:
                return new UpdateData(minSpeed);
            case FALL:
                return new FallData(minSpeed);
            default: //MOD
                return new ModData(minSpeed);
        }
    }

    /**
     * Check the string matches any of the valid regexes for BlockState set descriptions.
     * If so, split it into the Block ResourceLocation, and properties if any are available
     * @param string the raw string
     * @return A map of each part of the string
     */
    protected HashMap<String, String> getDescriptionFromString(String string) {
        HashMap<String, String> map = new HashMap<>();
        if(string.equals("-")) {
            //Looks like "-", i.e. whatever the block was before
            map.put("tag", null);
            map.put("block", "-");
            map.put("properties", null);
        }
        else if(string.matches(OLD_WITHPROPS_REGEX)) {
            //Looks like "-[open=true]", i.e. whatever the block was but open
            String[] splitString = string.split("\\[");
            map.put("tag", null);
            map.put("block", "-");
            map.put("properties", splitString[1].substring(0, splitString[1].length() - 1));
        }
        else if(string.matches(RES_LOC_REGEX)) {
            //Looks like "minecraft:acacia_button"
            map.put("tag", null);
            map.put("block", string);
            map.put("properties", null);
        }
        else if(string.matches(TAGS_RES_LOC_REGEX)) {
            //Looks like "#minecraft:dirt_like"
            map.put("tag", string.substring(1));
            map.put("block", null);
            map.put("properties", null);
        }
        else if(string.matches(BLOCK_STATES_REGEX)) {
            //Looks like "minecraft:acacia_button[face=wall]"
            String[] splitString = string.split("\\[");
            map.put("tag", null);
            map.put("block", splitString[0]);
            map.put("properties", splitString[1].substring(0, splitString[1].length() - 1));
        }
        else if(string.matches(TAGS_BLOCK_STATES_REGEX)) {
            //Looks like "#minecraft:dirt_like[snowy=true]"
            String[] splitString = string.split("\\[");
            map.put("tag", splitString[0].substring(1));
            map.put("block", null);
            map.put("properties", splitString[1].substring(0, splitString[1].length() - 1));
        }
        else {
            return null;
        }
        return map;
    }

    protected Block getBlockFromString(String state) {
        if(state == null) {
            return Blocks.AIR;
        }
        else {
            return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(state));
        }
    }

    /**
     * Use the pre-crash BlockState to determine the new state to transform into.
     * @param old the pre-crash BlockState.
     * @param stateString a String (partial or whole) description of the new state
     * @return
     * @throws ConfigLoadException
     */
    public BlockState getNewStateFromOldAndString(BlockState old, String stateString) throws ConfigLoadException {
        HashMap<String, String> description = this.getDescriptionFromString(stateString);
        String blockString = description.get("block");
        //Acquire the default new state to transform into
        BlockState newState = this.getBlockFromString(blockString).getDefaultState();
        if(newState == Blocks.AIR.getDefaultState() && !blockString.equals("minecraft:air")) {
            if(blockString.equals("-")) {
                //- means use the old state
                newState = old;
            }
            else {
                //Registry returned air, but something is fishy
                throw new ConfigLoadException("Could not find a block state matching " + blockString);
            }
        }
        //Get property map of this default state
        HashMap<String, String> newMap = this.getStringPropertyMapFrom(newState);
        //Get property map of the old state
        HashMap<String, String> oldMap = this.getStringPropertyMapFrom(old);
        //Get property map of the state being set
        HashMap<String, String> setMap = this.getStringPropertyMapFrom(description.get("properties"));
        //Update newDefaultMap with common properties from oldMap, then with common properties from setMap.
        //This ensures that only properties are set which the new state has,
        //that any unspecified properties are taken from the old BlockState,
        //and that the new properties specified in the config overwrite all others.
        //Using Strings means we can save conversion back to IProperty until the very end, and also lets BlockState
        //properties with the same String name be transferred to the new state, even if they are different IProperties.
        this.updateStringPropertyMap(newMap, oldMap);
        this.updateStringPropertyMap(newMap, setMap);
        HashMap<IProperty<?>, ?> parsedMap = this.parseStringPropertyMap(newState, newMap);
        for(IProperty<?> prop : parsedMap.keySet()) {
            newState = this.applyParsedProperty(newState, prop, parsedMap);
        }
        return newState;
    }

    public BlockState getSingleBlockStateFromString(String stateString) throws ConfigLoadException {
        HashMap<String, String> description = this.getDescriptionFromString(stateString);
        String blockString = description.get("block");
        if(blockString.equals("-")) {
            return Blocks.AIR.getDefaultState();
        }
        else {
            BlockState state = this.getBlockFromString(blockString).getDefaultState();
            return this.getNewStateFromOldAndString(state, stateString);
        }
    }

    /**
     * Get a map of all the state's properties and their values. Get them as Strings; the
     * @param state
     * @return
     */
    protected HashMap<String, String> getStringPropertyMapFrom(BlockState state) {
        HashMap<String, String> map = new HashMap<>();
        for(IProperty<?> prop : state.getProperties()) {
            map.put(prop.getName(), state.get(prop).toString());
        }
        return map;
    }

    protected HashMap<String, String> getStringPropertyMapFrom(String string) {
        HashMap<String, String> map = new HashMap<>();
        //Using the example propertiesString "face=floor,facing=east,powered=false"
        if(string != null) {
            //["face=floor", "facing=east", "powered=false"]
            String[] variantInfo = string.split(",");
            for (String variant : variantInfo) {
                //["face", "floor"]
                String[] info = variant.split("=");
                map.put(info[0], info[1]);
            }
        }
        return map;
    }

    public final boolean loadFile(BufferedReader br, File configDir, String filename) throws IOException {
        this.filename = filename;
        this.lineNumber = 0;
        ArrayList<String> errors = new ArrayList<>();

        //Delete previous error file
        String errorFileName = configDir.getAbsolutePath() + "/ERRORS_" + filename + ".txt";
        File errorFile = new File(errorFileName);
        if(errorFile.exists()) {
            errorFile.delete();
        }

        //Read file and collect errors from invalid lines
        while ((this.line = br.readLine()) != null) {
            ++this.lineNumber;
            if(!this.line.equals("") && line.charAt(0) != '@') {
                try {
                    //Line is supposed to be read
                    this.parseLine();
                }
                catch(ConfigLoadException cle) {
                    errors.add(cle.getMessage());
                }
            }
        }
        if(!errors.isEmpty()) {
            BufferedWriter bw = new BufferedWriter(new FileWriter(errorFile));
            for(String s : errors) {
                bw.write(s + "\n");
            }
            bw.close();
            FragileGlassBase.error("[FRAGILE GLASS] ERRORS FOUND IN "+filename+"!");
            FragileGlassBase.error("[FRAGILE GLASS] Please check config/ERRORS_"+filename+".txt for more information.");
        }
        this.filename = null;
        this.line = null;
        this.lineNumber = 0;
        return errors.isEmpty();
    }

    public void parseArbitraryString(String string) throws ConfigLoadException {
        this.line = string;
        this.lineNumber = -1;
        this.filename = "";
        this.parseLine();
    }

    /**
     * Parse a single line of text (this.line).
     * @throws ConfigLoadException
     */
    protected abstract void parseLine() throws ConfigLoadException;

    protected HashMap<IProperty<?>, Object> parseStringPropertyMap(BlockState reference, HashMap<String, String> map) {
        if(map == null) map = new HashMap<>();
        HashMap<IProperty<?>, Object> properties = new HashMap<>();
        Collection<IProperty<?>> existingProps = reference.getProperties();
        for(IProperty<?> prop : existingProps) {
            String propName = prop.getName();
            if(map.containsKey(propName)) {
                String value = map.get(propName);
                if(prop instanceof BooleanProperty) {
                    BooleanProperty bp = (BooleanProperty) prop;
                    bp.parseValue(value).ifPresent(b -> properties.put(bp, b));
                }
                else if(prop instanceof IntegerProperty) {
                    IntegerProperty ip = (IntegerProperty) prop;
                    ip.parseValue(value).ifPresent(i -> properties.put(ip, i));
                }
                else if(prop instanceof EnumProperty) {
                    EnumProperty ep = (EnumProperty) prop;
                    ep.parseValue(value).ifPresent(e -> properties.put(ep, e));
                }
            }
        }
        return properties;
    }

    /**
     * For any properties which both maps have, the value is set to that of newProps.
     */
    protected void updateStringPropertyMap(HashMap<String, String> toUpdate, HashMap<String, String> newProps) {
        for(String key : newProps.keySet()) {
            toUpdate.replace(key, newProps.get(key));
        }
    }

    /**
     * Class for exceptions caught while reading config files.
     */
    public class ConfigLoadException extends Exception {
        public String shortMessage;

        public ConfigLoadException(String message) {
            super(ConfigLoader.this.lineNumber == -1 ?
                    "Could not parse command: \n" + ConfigLoader.this.line + "\n" + message + "\nNo changes have been made." :
                    "Error parsing " + ConfigLoader.this.filename + " line " + ConfigLoader.this.lineNumber + ":\n"
                            + ConfigLoader.this.line +"\n" + message + "\n");
            this.shortMessage = message;
        }
    }
}
