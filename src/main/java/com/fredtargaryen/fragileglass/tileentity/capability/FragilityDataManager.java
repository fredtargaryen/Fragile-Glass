package com.fredtargaryen.fragileglass.tileentity.capability;

import com.fredtargaryen.fragileglass.DataReference;
import com.fredtargaryen.fragileglass.FragileGlassBase;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.util.*;
import com.google.common.base.Optional;

import static com.fredtargaryen.fragileglass.tileentity.capability.FragilityDataManager.FragileBehaviour.BREAK;
import static com.fredtargaryen.fragileglass.tileentity.capability.FragilityDataManager.FragileBehaviour.MOD;
import static com.fredtargaryen.fragileglass.tileentity.capability.FragilityDataManager.FragileBehaviour.UPDATE;

/**
 * Responsible for everything to do with block fragility data from fragileglassft_blocklist.cfg.
 */
public class FragilityDataManager {
    private static FragilityDataManager INSTANCE;

    private File configDir;
    private File configFile;

    private HashMap<String, FragilityData> tileEntityData;
    private HashMap<String, FragilityData> blockData;
    //TODO
    private HashMap<IBlockState, FragilityData> blockStateData;

    public enum FragileBehaviour {
        //Break if above the break speed
        BREAK,
        //Update after the update delay if above the break speed
        UPDATE,
        //Load the data but don't even construct the capability; let another mod deal with it all
        MOD
    }

    public static FragilityDataManager getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new FragilityDataManager();
        }
        return INSTANCE;
    }

    public FragilityDataManager() {
        this.tileEntityData = new HashMap<>();
        this.blockData = new HashMap<>();
        this.blockStateData = new HashMap<>();
    }

    public void addCapabilityIfPossible(TileEntity te, AttachCapabilitiesEvent<TileEntity> evt) {
        FragilityData fragData = this.getTileEntityFragilityData(te);
        if (fragData != null) {
            if (fragData.behaviour == FragileBehaviour.BREAK) {
                ICapabilityProvider iCapProv = new ICapabilityProvider() {
                    IFragileCapability inst = new IFragileCapability() {
                        @Override
                        public void onCrash(IBlockState state, TileEntity te, Entity crasher, double speed) {
                            if (speed > fragData.breakSpeed) {
                                te.getWorld().destroyBlock(te.getPos(), true);
                            }
                        }
                    };

                    @Override
                    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
                        return capability == FragileGlassBase.FRAGILECAP;
                    }

                    @Nullable
                    @Override
                    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
                        return capability == FragileGlassBase.FRAGILECAP ? FragileGlassBase.FRAGILECAP.<T>cast(inst) : null;
                    }
                };
                evt.addCapability(DataReference.FRAGILE_CAP_LOCATION, iCapProv);
            } else if (fragData.behaviour == FragileBehaviour.UPDATE) {
                ICapabilityProvider iCapProv = new ICapabilityProvider() {
                    IFragileCapability inst = new IFragileCapability() {
                        @Override
                        public void onCrash(IBlockState state, TileEntity te, Entity crasher, double speed) {
                            if (speed > fragData.breakSpeed) {
                                World w = te.getWorld();
                                BlockPos tilePos = te.getPos();
                                w.scheduleUpdate(tilePos, w.getBlockState(tilePos).getBlock(), fragData.updateDelay);
                            }
                        }
                    };

                    @Override
                    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
                        return capability == FragileGlassBase.FRAGILECAP;
                    }

                    @Nullable
                    @Override
                    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
                        return capability == FragileGlassBase.FRAGILECAP ? FragileGlassBase.FRAGILECAP.<T>cast(inst) : null;
                    }
                };
                evt.addCapability(DataReference.FRAGILE_CAP_LOCATION, iCapProv);
            }
        }
    }

    public FragilityData getBlockFragilityData(Block b) {
        //Use the block in the Forge Block registry to get its ResourceLocation
        String resourceLocationString = ForgeRegistries.BLOCKS.getKey(b).toString();
        //Check the ResourceLocation string is in the manager, i.e. if the cfg was valid, it was in the cfg
        if(resourceLocationString != null) {
            if(this.blockData.containsKey(resourceLocationString)) {
                //If the cfg was valid and the string was in the cfg, there must be fragility data
                return this.blockData.get(resourceLocationString);
            }
        }
        return null;
    }

    public FragilityData getBlockStateFragilityData(IBlockState state) {
        if(this.blockStateData.containsKey(state)) {
            return this.blockStateData.get(state);
        }
        return null;
    }

    public FragilityData getTileEntityFragilityData(TileEntity te) {
        //Consider me the ambassador for using "clarse" instead of "clazz"
        Class<? extends TileEntity> clarse = te.getClass();
        //Use the tile entity's class in the TileEntityRegistry to get its ResourceLocation
        String resourceLocationString = TileEntity.getKey(clarse).toString();
        //Check the ResourceLocation string is in the manager, i.e. if the cfg was valid, it was in the cfg
        if(resourceLocationString != null) {
            if (this.tileEntityData.containsKey(resourceLocationString)) {
                //If the cfg was valid and the string was in the cfg, there must be fragility data
                return this.tileEntityData.get(resourceLocationString);
            }
        }
        return null;
    }

    private void handleConfigFileException(Exception e) {
        FMLLog.bigWarning("Could not load "+DataReference.MODID+"_blocklist.cfg! " +
                "Default block behaviour will be loaded. No custom data will take effect.");
        e.printStackTrace();
        this.loadDefaultData();
    }

    public boolean hasBlockFragilityData() {
        return !this.blockData.isEmpty();
    }

    public boolean hasBlockStateFragilityData() {
        return !this.blockStateData.isEmpty();
    }

    public boolean hasTileEntityFragilityData() {
        return !this.tileEntityData.isEmpty();
    }

    private boolean isResourceLocationValidBlock(String resourceLocation) {
        return ForgeRegistries.BLOCKS.containsKey(new ResourceLocation(resourceLocation));
    }

    private void loadDefaultData() {
        this.blockData.clear();
        this.tileEntityData.clear();
        this.tileEntityData.put(DataReference.MODID + ":tefg", new FragilityData(BREAK, 0.165, 0, new String[]{}));
        this.tileEntityData.put(DataReference.MODID + ":teti", new FragilityData(BREAK, 0.0, 0, new String[]{}));
        this.tileEntityData.put(DataReference.MODID + ":tews", new FragilityData(UPDATE, 0.0, 10, new String[]{}));
    }

    public void loadBlockData() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(this.configFile));
            String line;
            while ((line = br.readLine()) != null) {
                if(!line.equals("") && line.charAt(0) != '#') {
                    //Line is supposed to be read
                    String[] values = line.split(" ");
                    //Validation
                    if(values.length < 4) {
                        FMLLog.bigWarning("[FRAGILITY CONFIG] You need at least four values! Please check the file comments again.");
                        throw new Exception();
                    }
                    else {
                        if (values[0].split(":").length == 2) {
                            FragileBehaviour behaviour;
                            if (values[1].equals("mod")) {
                                behaviour = MOD;
                            } else if (values[1].equals("update")) {
                                behaviour = UPDATE;
                            } else {
                                behaviour = BREAK;
                                if (!values[1].equals("break")) {
                                    FMLLog.log.error("[FRAGILITY CONFIG] '" + values[1] + "' should be 'break', 'update' or 'mod'. Assuming you mean 'break'");
                                }
                            }
                            if(this.tileEntityData.containsKey(values[0]) || this.blockData.containsKey(values[0])) {
                                FMLLog.log.warn("[FRAGILITY CONFIG] '" + values[0] + "' is already in the file - using the first entry only");
                            }
                            else {
                                IBlockState state = this.parseBlockState(values[0]);
                                if(state != null) {
                                    this.blockStateData.put(state,
                                            new FragilityData(behaviour,
                                                    Double.parseDouble(values[2]),
                                                    Integer.parseInt(values[3]),
                                                    Arrays.copyOfRange(values, 4, values.length)));
                                }
                                else if(this.isResourceLocationValidBlock(values[0])) {
                                    //The ResourceLocation is for a block
                                    this.blockData.put(values[0],
                                            new FragilityData(behaviour,
                                                    Double.parseDouble(values[2]),
                                                    Integer.parseInt(values[3]),
                                                    Arrays.copyOfRange(values, 4, values.length)));
                                }
                                else {
                                    //Can't do the same check for tile entities, so add it anyway, and it simply won't
                                    //be used if invalid.
                                    this.tileEntityData.put(values[0],
                                            new FragilityData(behaviour,
                                                    Double.parseDouble(values[2]),
                                                    Integer.parseInt(values[3]),
                                                    Arrays.copyOfRange(values, 4, values.length)));
                                }
                            }
                        } else {
                            FMLLog.log.error("[FRAGILITY CONFIG] '" + values[0] + "' should have the form modid:registryid - ignoring");
                        }
                    }
                }
            }
        }
        catch(NumberFormatException nfe) {
            FMLLog.bigWarning("[FRAGILITY CONFIG] One of your values is not the right kind of number! Please check the file comments again.");
            this.handleConfigFileException(new Exception("One of your values is not the right kind of number"));
        }
        catch(Exception e) {
            this.handleConfigFileException(e);
        }
    }

    /**
     * Takes a String representing a BlockState (same format as that returned by BlockState#toString()) and tries to build a BlockState from it.
     * @param blockData The string. Example: "minecraft:dirt[snowy=false]"
     * @return null if the IBlockState could not be created
     */
    private IBlockState parseBlockState(String blockData) {
        //{"minecraft:dirt","snowy=false]"}
        String[] bracketSplit = blockData.split("\\[");
        //"minecraft:dirt"
        String blockName = bracketSplit[0];
        if(this.isResourceLocationValidBlock(blockName)) {
            IBlockState state = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockName)).getDefaultState();
            //{"snowy=false"}
            String[] variantInfo = bracketSplit[1].split("\\]")[0].split(",");
            Collection<IProperty<?>> keys = state.getPropertyKeys();
            for(String variant : variantInfo) {
                //{"snowy","false"}
                String[] info = variant.split("=");
                for(IProperty<?> iprop : keys) {
                    if(iprop.getName().equals(info[0])) {
                        state = this.parseAndSetProperty(state, iprop, info[1]);
                    }
                }
            }
            return state;
        }
        return null;
    }

    private <T extends Comparable<T>> IBlockState parseAndSetProperty(IBlockState state, IProperty<T> iprop, String value) {
        if(iprop instanceof PropertyBool) {
            PropertyBool pb = (PropertyBool) iprop;
            Optional<Boolean> opt = pb.parseValue(value);
            if(opt.isPresent()) return state.withProperty(pb, opt.get());
        }
        else if(iprop instanceof PropertyInteger) {
            PropertyInteger pi = (PropertyInteger) iprop;
            Optional<Integer> opt = pi.parseValue(value);
            if(opt.isPresent()) return state.withProperty(pi, opt.get());
        }
        else if(iprop instanceof PropertyEnum) {
            PropertyEnum pe = (PropertyEnum) iprop;
            Optional<Enum> opt = pe.parseValue(value);
            if(opt.isPresent()) return state.withProperty(pe, opt.get());
        }
        return state;
    }

    public void setupDirsAndFiles(File configDir) {
        this.configDir = configDir;
        this.configFile = new File(this.configDir, DataReference.MODID + "_blocklist.cfg");
        if(!this.configFile.exists()) {
            try {
                //Config file is not in config folder! Write from defaultFileData (see bottom of file)
                FMLLog.log.warn("[FRAGILITY CONFIG] No config file found! Writing a new one.");
                FileWriter fw = new FileWriter(this.configFile);
                for(String s : defaultFileData) {
                    fw.write(s);
                }
                fw.close();
            }
            catch(IOException ioe) {
                this.handleConfigFileException(ioe);
            }
        }
    }

    public class FragilityData {
        private FragileBehaviour behaviour;
        private double breakSpeed;
        private int updateDelay;
        private String[] extraData;

        public FragilityData(FragileBehaviour behaviour, double breakSpeed, int updateDelay, String[] extraData) {
            this.behaviour = behaviour;
            this.breakSpeed = breakSpeed;
            this.updateDelay = updateDelay;
            this.extraData = extraData;
        }

        public FragileBehaviour getBehaviour() { return this.behaviour; }

        public double getBreakSpeed() { return this.breakSpeed; }

        public int getUpdateDelay() { return this.updateDelay; }

        public String[] getExtraData() { return this.extraData; }
    }

    //Doesn't look like I can read from assets so sadly this is needed for now
    private static final String[] defaultFileData = new String[] {
            "########################################\n",
            "#FRAGILE GLASS AND THIN ICE CONFIG FILE#\n",
            "########################################\n",
            "#THINK VERY CAREFULLY AND BACK UP YOUR WORLDS BEFORE ADDING ENTRIES HERE!\n",
            "#(You probably don't really want to make ALL DIRT BLOCKS fragile, for example.)\n",
            "#Here is where you can configure which blocks are fragile and which are not, and modify basic behaviour.\n",
            "#--Limitations--\n",
            "#This will not work for blocks which are basically air blocks, e.g. Air blocks and 'logic' blocks.\n",
            "#--How to customise--\n",
            "#To add a comment to the file, start the line with a # symbol.\n",
            "#To make a block fragile, add a new row in this file following this format:\n",
            "#<modid>:<ID> <break/update/mod> <min speed> <update delay> <extra values>\n",
            "#* 'modid:ID' is the ResourceLocation string used to register with Forge.\n",
            "#  - 'modid' can be found by looking in the 'modid' entry of the mod's mcmod.info file.\n",
            "#    For vanilla Minecraft this is just 'minecraft'.\n",
            "#  - For blocks WITH tile entities 'ID' is the name used to register the Tile Entity with Forge.\n",
            "#    You can find these by searching for 'GameRegistry.registerTileEntity' in the mod's source code...\n",
            "#    or by asking the developer. These are easy to guess in vanilla Minecraft.\n",
            "#  - For blocks WITHOUT tile entities you need the block's registry name. You can usually find this by\n",
            "#    looking at the block in-game with the F3 menu on.\n",
            "#* You must choose one of 'break', 'update' or 'mod'; the block will have one of the\n",
            "#  following 'crash behaviours':\n",
            "#  - All blocks' crash behaviours will trigger (but not necessarily break) when the 'breaker' is\n",
            "#    moving fast enough to be able to break things. If the breaker isn't fast enough, the block won't\n",
            "#    do anything. This 'breaking speed' depends on the breaker.\n",
            "#  - 'break' means the block will simply break immediately.\n",
            "#  - 'update' means a block update will trigger (it won't break unless that is in the update code).\n",
            "#  - 'mod' is for mod blocks with more advanced behaviours. Modders should make tile entities for these\n",
            "#    and implement IFragileCapability with the behaviour they want. This mod will load all the extra\n",
            "#    values and it is up to the modder how they are used.\n",
            "#* The first number is a minimum speed (must be decimal). The breaker must be moving above their breaking\n",
            "#  speed, AND above this speed, to trigger the crash behaviour. Speed is measured in blocks per tick,\n",
            "#  which is metres per second divided by 20.\n",
            "#* The second number (must be integer) is only used by 'update' blocks, and it specifies the delay\n",
            "#  between the collision and their block update. Delays are measured in ticks and there are 20 ticks per second.\n",
            "#* You can add extra values of any format, separated by spaces, for any mod blocks that might require\n",
            "#  them.\n",
            "#--Default values, in case you break something--\n",
            "#All fragile glass blocks:\n",
            "#fragileglassft:tefg break 0.165 0\n",
            "#Thin ice:\n",
            "#fragileglassft:teti break 0.0 0\n",
            "#Weak stone:\n",
            "#fragileglassft:tews update 0.0 10\n",
            "fragileglassft:tefg break 0.165 0\n",
            "fragileglassft:teti break 0.0 0\n",
            "fragileglassft:tews update 0.0 10\n",
    };
}
