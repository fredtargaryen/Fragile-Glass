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

import java.io.BufferedReader;
import java.io.IOException;
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

    protected String filename;
    protected int lineNumber;
    protected String line;

    private <P extends Comparable<P>> BlockState applyParsedProperty(BlockState state, IProperty<P> iprop, HashMap newProperties) {
        return state.with(iprop, (P) newProperties.get(iprop));
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
            //Looks like "-"
            map.put("tag", null);
            map.put("block", "minecraft:air");
            map.put("properties", null);
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
        if(state.equals("-")) {
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
    protected BlockState getNewStateFromOldAndString(BlockState old, String stateString) throws ConfigLoadException {
        HashMap<String, String> description = this.getDescriptionFromString(stateString);
        String blockString = description.get("block");
        //Acquire the default new state to transform into
        BlockState newState = this.getBlockFromString(blockString).getDefaultState();
        if(newState == Blocks.AIR.getDefaultState() &&
                !blockString.equals("minecraft:air") &&
                !blockString.equals("-")) {
            //Registry returned air, but something is fishy
            throw new ConfigLoadException("Could not find a block matching "+blockString);
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

    public final void loadFile(BufferedReader br, String filename) throws ConfigLoadException, IOException {
        this.filename = filename;
        this.lineNumber = 0;
        while ((this.line = br.readLine()) != null) {
            ++this.lineNumber;
            if(!this.line.equals("") && line.charAt(0) != '@') {
                //Line is supposed to be read
                this.parseLine();
            }
        }
        this.filename = null;
        this.line = null;
        this.lineNumber = 0;
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
        public ConfigLoadException(String message) {
            super("Could not load " + ConfigLoader.this.filename + " because of line " + ConfigLoader.this.lineNumber + ":\n" + ConfigLoader.this.line +"\n" + message +
                    "\nThe rest of the file will not be loaded.");
        }
    }
}
