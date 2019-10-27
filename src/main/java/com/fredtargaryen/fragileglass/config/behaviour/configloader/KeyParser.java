package com.fredtargaryen.fragileglass.config.behaviour.configloader;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Que pasa?
 */
public class KeyParser {
    //REGEX CONSTANTS
    private static final String RES_LOC_REGEX = "[a-z]+:[a-z|_]+";
    private static final String VARIANT_REGEX = "[a-z]+=([0-9]+|[a-z|_]+)";
    private static final String VARIANTS_REGEX = "(" + VARIANT_REGEX + ",)*(" + VARIANT_REGEX + ")";
    private static final String BLOCK_STATES_REGEX = RES_LOC_REGEX + "\\[" + VARIANTS_REGEX + "\\]";
    private static final String TAGS_RES_LOC_REGEX = "#" + RES_LOC_REGEX;
    private static final String TAGS_BLOCK_STATES_REGEX = "#" + BLOCK_STATES_REGEX;
    private static final String OLD_WITHPROPS_REGEX = "\\-\\[" + VARIANTS_REGEX + "\\]";

    /**
     * Purely to remove the "Block{" and "}" from BlockState#toString().
     * This format is then usable in config files.
     * @param blockStateString e.g. "Block{fragileglassft:blackstainedfragileglasspane[facing=north]}"
     * @return e.g. "fragileglassft:blackstainedfragileglasspane[facing=north]"
     */
    public static String cleanBlockStateString(String blockStateString) {
        String[] parts = blockStateString.substring(6).split("}");
        if(parts.length == 1) {
            return parts[0];
        }
        return parts[0] + parts[1];
    }

    ///////////////////////////////////
    //METHODS FOR PARSING BLOCKSTATES//
    ///////////////////////////////////
    public static List<BlockState> getAllBlockStatesForString(String states) {
        HashMap<String, String> description = getDescriptionFromString(states);
        if(description == null) {
            return new ArrayList<>();
        }
        List<BlockState> allStates = new ArrayList<>();
        Collection<Block> blocks;
        String tag = description.get("tag");
        if(tag == null) {
            //Represents a single block
            //Get all BlockStates with the block named in splitEntryName[0]
            Block block = getBlockFromString(description.get("block"));
            blocks = new ArrayList<>();
            if(block == Blocks.AIR) {
                //The registry potentially doesn't recognise this block
                String blockString = description.get("block");
                if(blockString.equals("minecraft:air") || blockString.equals("-")) {
                    //The user actually wanted an air block
                    blocks.add(block);
                }
                //Else, the block string wasn't found in the registry, so ignore it.
            }
            else {
                //The block was found in the registry
                blocks.add(block);
            }
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
                HashMap<IProperty<?>, ?> specifiedProperties = parseStringPropertyMap(
                        block.getDefaultState(), getStringPropertyMapFrom(propsString));
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

    public static Block getBlockFromString(String state) {
        if(state == null) {
            return Blocks.AIR;
        }
        else {
            return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(state));
        }
    }

    /**
     * Check the string matches any of the valid regexes for BlockState set descriptions.
     * If so, split it into the Block ResourceLocation, and properties if any are available
     * @param string the raw string
     * @return A map of each part of the string
     */
    public static HashMap<String, String> getDescriptionFromString(String string) {
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

    /**
     * Get a map of all the state's properties and their values. Get them as Strings; the
     * @param state
     * @return
     */
    public static HashMap<String, String> getStringPropertyMapFrom(BlockState state) {
        HashMap<String, String> map = new HashMap<>();
        for(IProperty<?> prop : state.getProperties()) {
            map.put(prop.getName(), state.get(prop).toString());
        }
        return map;
    }

    public static HashMap<String, String> getStringPropertyMapFrom(String string) {
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

    public static HashMap<IProperty<?>, Object> parseStringPropertyMap(BlockState reference, HashMap<String, String> map) {
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

    ///////////////////////////////////
    //METHODS FOR PARSING ENTITYTYPES//
    ///////////////////////////////////
    public static Collection<EntityType<?>> getAllEntityTypesForString(String value) throws Exception {
        Collection<EntityType<?>> entityTypes = new ArrayList<>();
        if (value.charAt(0) == '#') {
            //values[0] is a tag representing multiple entities
            entityTypes = EntityTypeTags.getCollection()
                    .getOrCreate(new ResourceLocation(value.substring(1)))
                    .getAllElements();
        } else {
            //value is a single entity
            entityTypes = new ArrayList<>();
            //Check the first value is a ResourceLocation in the Forge EntityType registry, i.e. refers to a valid entity
            EntityType entry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(value));
            if (entry == null) {
                throw new Exception("There is no entity type with the resource location " + value + ".");
            } else {
                entityTypes.add(entry);
            }
        }
        return entityTypes;
    }

    ///////////////////////////////////////
    //METHODS FOR PARSING TILEENTITYTYPES//
    ///////////////////////////////////////
    public static TileEntityType getTileEntityTypeForString(String value) {
        return ForgeRegistries.TILE_ENTITIES.getValue(new ResourceLocation(value));
    }
}
