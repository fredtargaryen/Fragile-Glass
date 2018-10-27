package com.fredtargaryen.fragileglass.tileentity.capability;

import com.fredtargaryen.fragileglass.DataReference;
import com.fredtargaryen.fragileglass.FragileGlassBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.FMLLog;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.util.Arrays;
import java.util.HashMap;

import static com.fredtargaryen.fragileglass.tileentity.capability.FragilityDataManager.FragileBehaviour.GLASS;
import static com.fredtargaryen.fragileglass.tileentity.capability.FragilityDataManager.FragileBehaviour.MOD;
import static com.fredtargaryen.fragileglass.tileentity.capability.FragilityDataManager.FragileBehaviour.STONE;

/**
 * Responsible for everything to do with block fragility data from fragileglassft_blocklist.cfg.
 */
public class FragilityDataManager {
    private static FragilityDataManager INSTANCE;

    private File configDir;
    private File configFile;

    private HashMap<String, FragilityData> tileEntityData;

    public enum FragileBehaviour {
        //Break if above the break speed
        GLASS,
        //Update after the update delay if above the break speed
        STONE,
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
    }

    public void addCapabilityIfPossible(TileEntity te, AttachCapabilitiesEvent<TileEntity> evt) {
        FragilityData fragData = this.getTileEntityFragilityData(te);
        if(fragData.behaviour == FragileBehaviour.GLASS) {
            ICapabilityProvider iCapProv = new ICapabilityProvider() {
                IFragileCapability inst = new IFragileCapability() {
                    @Override
                    public void onCrash(IBlockState state, TileEntity te, Entity crasher, double speed) {
                        if (speed > fragData.breakSpeed) {
                            te.getWorld().destroyBlock(te.getPos(), false);
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
        else if(fragData.behaviour == FragileBehaviour.STONE) {
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
        FMLLog.bigWarning("Could not load "+DataReference.MODID+"_blocklist.cfg!");
        FMLLog.bigWarning("Default block behaviour will be loaded. No custom data will take effect.");
        e.printStackTrace();
        this.loadDefaultData();
    }

    private void loadDefaultData() {
        this.tileEntityData.clear();
        this.tileEntityData.put(DataReference.MODID + ":tefg", new FragilityData(GLASS, 0.165, 0, new String[]{}));
        this.tileEntityData.put(DataReference.MODID + ":teti", new FragilityData(GLASS, 0.0, 0, new String[]{}));
        this.tileEntityData.put(DataReference.MODID + ":tews", new FragilityData(STONE, 0.0, 10, new String[]{}));
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
                            } else if (values[1].equals("stone")) {
                                behaviour = STONE;
                            } else {
                                behaviour = GLASS;
                                if (!values[1].equals("glass")) {
                                    FMLLog.log.error("[FRAGILITY CONFIG] '" + values[1] + "' should be 'glass', 'stone' or 'mod'. Assuming you mean 'glass'");
                                }
                            }
                            this.tileEntityData.put(values[0],
                                    new FragilityData(behaviour,
                                            Double.parseDouble(values[2]),
                                            Integer.parseInt(values[3]),
                                            Arrays.copyOfRange(values, 4, values.length)));
                        } else {
                            FMLLog.log.error("[FRAGILITY CONFIG] '" + values[0] + "' should have the form modid:tileregistryname - ignoring");
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

    private class FragilityData {
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
            "#Here is where you can configure which blocks are fragile and which are not, and modify basic behaviour.\n",
            "#--Limitations--\n",
            "#There is currently no way to make a block fragile if it didn't have a tile entity to begin with,\n",
            "#e.g. Dirt, Redstone Lamps, Doors, Trapdoors, Packed Ice, ordinary Glass, Prismarine.\n",
            "#It should be possible to make any block fragile if it has a tile entity,\n",
            "#e.g. Beds, Flower Pots, Beacons, Dispensers, Floower Pots, OpenBlocks Tanks.\n",
            "#--How to customise--\n",
            "#To add a comment to the file, start the line with a # symbol.\n",
            "#To make a tile entity fragile, add a new row in this file following this format:\n",
            "#<modid>:<tile entity id> <glass/stone/mod> <min speed> <update delay> <extra values>\n",
            "#* modid and tile entity id are the ResourceLocation string used to register the tile entity with Forge.\n",
            "#  You can find these by searching for \"GameRegistry.registerTileEntity\" in the mod's source code...\n",
            "#  or by asking the developer.\n",
            "#* You must choose one of 'glass', 'stone' or 'mod'; the tile entity will copy the behaviour of the\n",
            "#  corresponding block in Fragile Glass and Thin Ice. For more advanced behaviour the modder will have to\n",
            "#  code the Capability themselves.\n",
            "#  * All blocks' 'crash behaviours' will trigger (but not necessarily break) when the 'breaker' is\n",
            "#    moving fast enough to be able to break things. If the breaker isn't fast enough, the block won't\n",
            "#    break. This 'breaking speed' depends on the breaker.\n",
            "#  * 'glass' means the block will simply break.\n",
            "#  * 'stone' means a block update will trigger, but it won't break unless that is in the update code.\n",
            "#  * 'mod' is for a block that does something more advanced. It is completely up to the mod developer\n",
            "#    what the break behaviour will be, but this mod will still load all the values you write here.\n",
            "#* The first number is a minimum speed (must be decimal). The breaker must be moving above their breaking\n",
            "#  speed, AND above this speed, to trigger the crash behaviour. Speed is measured in blocks per tick,\n",
            "#  which is metres per second divided by 20.\n",
            "#* The second number (must be integer) is only used by 'stone' blocks, and it specifies the delay\n",
            "#  between the collision and their block update. Delays are measured in ticks and there are 20 ticks per second.\n",
            "#* You can add extra values of any format, separated by spaces, for any mod blocks that might require\n",
            "#  them.\n",
            "#--Default values, in case you break something--\n",
            "#All fragile glass blocks:\n",
            "#fragileglassft:tefg glass 0.165 0\n",
            "#Thin ice:\n",
            "#fragileglassft:teti glass 0.0 0\n",
            "#Weak stone:\n",
            "#fragileglassft:tews stone 0.0 10\n",
            "fragileglassft:tefg glass 0.165 0\n",
            "fragileglassft:teti glass 0.0 0\n",
            "fragileglassft:tews stone 0.0 10\n",
    };
}
