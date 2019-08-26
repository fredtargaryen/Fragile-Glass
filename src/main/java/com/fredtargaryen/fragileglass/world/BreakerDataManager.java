package com.fredtargaryen.fragileglass.world;

import com.fredtargaryen.fragileglass.DataReference;
import com.fredtargaryen.fragileglass.FragileGlassBase;
import com.fredtargaryen.fragileglass.entity.capability.IBreakCapability;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.*;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.util.HashMap;

/**
 * Responsible for everything to do with entity break data from fragileglassft_entities.cfg.
 */
public class BreakerDataManager {
    private static BreakerDataManager INSTANCE;

    private File configDir;
    private File configFile;

    private HashMap<EntityEntry, BreakerData> entityData;

    public static BreakerDataManager getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new BreakerDataManager();
        }
        return INSTANCE;
    }

    public BreakerDataManager() {
        this.entityData = new HashMap<>();
    }

    public void addCapabilityIfPossible(Entity e, AttachCapabilitiesEvent<Entity> evt) {
        BreakerData breakerData = this.getEntityBreakerData(e);
        if (breakerData == null) {
            if (e instanceof EntityLivingBase
                    || e instanceof EntityArrow
                    || e instanceof EntityFireball
                    || e instanceof EntityMinecart
                    || e instanceof EntityFireworkRocket
                    || e instanceof EntityBoat
                    || e instanceof EntityTNTPrimed
                    || e instanceof EntityFallingBlock) {
                evt.addCapability(DataReference.BREAK_LOCATION, new ICapabilityProvider() {
                    IBreakCapability inst = FragileGlassBase.BREAKCAP.getDefaultInstance();

                    @Override
                    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
                        return capability == FragileGlassBase.BREAKCAP;
                    }

                    @Override
                    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
                        return capability == FragileGlassBase.BREAKCAP ? FragileGlassBase.BREAKCAP.<T>cast(inst) : null;
                    }
                });
            }
        } else {
            ICapabilityProvider iCapProv = new ICapabilityProvider() {
                IBreakCapability inst = new IBreakCapability() {
                    @Override
                    public void init(Entity e) {

                    }

                    @Override
                    public void update(Entity e) {

                    }

                    @Override
                    public double getSpeedSquared(Entity e) {
                        return e.motionX * e.motionX + e.motionY * e.motionY + e.motionZ * e.motionZ;
                    }

                    @Override
                    public boolean isAbleToBreak(Entity e, double speedSq) {
                        return speedSq >= breakerData.getMinSpeedSquared()
                                && speedSq <= breakerData.getMaxSpeedSquared();
                    }

                    @Override
                    public double getMotionX(Entity e) {
                        return e.motionX;
                    }

                    @Override
                    public double getMotionY(Entity e) {
                        return e.motionY;
                    }

                    @Override
                    public double getMotionZ(Entity e) {
                        return e.motionZ;
                    }

                    @Override
                    public byte getNoOfBreaks(Entity e) {
                        return 1;
                    }
                };

                @Override
                public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
                    return capability == FragileGlassBase.BREAKCAP;
                }

                @Nullable
                @Override
                public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
                    return capability == FragileGlassBase.BREAKCAP ? FragileGlassBase.BREAKCAP.<T>cast(inst) : null;
                }
            };
            evt.addCapability(DataReference.BREAK_LOCATION, iCapProv);
        }
    }

    public void clearData() {
        this.entityData.clear();
    }

    public BreakerData getEntityBreakerData(Entity e) {
        EntityEntry entry = EntityRegistry.getEntry(e.getClass());
        if(this.entityData.containsKey(entry)) {
            return this.entityData.get(entry);
        }
        return null;
    }

    public EntityEntry getEntityEntry(String s) {
        ResourceLocation rl = new ResourceLocation(s);
        if(ForgeRegistries.ENTITIES.containsKey(rl)) {
            return ForgeRegistries.ENTITIES.getValue(rl);
        }
        return null;
    }

    private void handleConfigFileException(Exception e) {
        FMLLog.bigWarning("Could not load "+DataReference.MODID+"_entities.cfg! " +
                "Default entity crash behaviour will be loaded. No custom data will take effect.");
        e.printStackTrace();
        this.loadDefaultData();
    }

    public boolean hasEntityBreakerData() {
        return !this.entityData.isEmpty();
    }

    private void loadDefaultData() {
        this.entityData.clear();
    }

    /**
     * Set up to read fragileglassft_entities.cfg. MUST be called in postInit, when all Entities have been created!
     */
    public void loadEntityData() {
        try {
            BreakerConfigLoader bcl = new BreakerConfigLoader(this, this.entityData);
            File[] fileList = this.configDir.listFiles();
            if(fileList != null) {
                String fileName = this.configFile.getName();
                System.out.println("Found file "+fileName+"; now loading");
                BufferedReader br = new BufferedReader(new FileReader(this.configFile));
                bcl.loadFile(br, this.configDir, fileName);
                br.close();
                for (File file : fileList) {
                    fileName = file.getName();
                    String[] fileNameParts = fileName.split("_");
                    if(fileNameParts.length == 3){
                        System.out.println("Found file "+fileName+"; now loading");
                        if(fileNameParts[0].equals(DataReference.MODID) && fileNameParts[1].equals("entities")) {
                            br = new BufferedReader(new FileReader(file));
                            bcl.loadFile(br, this.configDir, fileName);
                            br.close();
                        }
                    }
                }
            }
        }
        catch(IOException ioe) {
            this.handleConfigFileException(new Exception());
        }
    }

    public void setupDirsAndFiles(File configDir) {
        this.configDir = configDir;
        this.configFile = new File(this.configDir, DataReference.MODID + "_entities.cfg");
        if(!this.configFile.exists()) {
            try {
                //Config file is not in config folder! Write from defaultFileData (see bottom of file)
                FMLLog.log.warn("[BREAKER CONFIG] No config file found! Writing a new one.");
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

    //Doesn't look like I can read from assets so sadly all this is needed for now
    private static final String[] defaultFileData = new String[] {
            "###################################################\n",
            "#FRAGILE GLASS AND THIN ICE CONFIG FILE - ENTITIES#\n",
            "###################################################\n",
            "#THINK VERY CAREFULLY AND BACK UP YOUR WORLDS BEFORE ADDING ENTRIES HERE!\n",
            "#(You probably don't really want to make ZOMBIES ABLE TO BREAK EVERYTHING, for example.)\n",
            "#Here is where you can configure the speeds required for each entity to break a fragile block.\n",
            "\n#--Limitations--\n",
            "#* This will not work for entities which only appear on the client, such as particles.\n",
            "#* If your entry is not for a valid block, the mod will assume you entered a tile entity. However it\n",
            "#  cannot check if tile entities are valid, so you won't be warned. Check your spellings carefully.\n",
            "\n#--How to customise--\n",
            "#To add a comment to the file, start the line with a # symbol.\n",
            "#To make an entity able to break fragile blocks, add a new row in this file following this format:\n",
            "#<modid>:<ID> <min speed> <max speed> <extra values>\n",
            "#* 'modid:ID' is the ResourceLocation string used to register the entity with Forge.\n",
            "#  - 'modid' can be found by looking in the 'modid' entry of the mod's mcmod.info file.\n",
            "#    For vanilla Minecraft this is just 'minecraft'.\n",
            "#* The first number is a minimum speed (must be decimal). The entity must be moving above this speed\n",
            "#  for a block to potentially break. Speed is measured in blocks per tick, which is metres per second\n",
            "#  divided by 20. The minimum for this value is 0.0, i.e. any movement could break a block.\n",
            "#* The second number is a maximum speed (must be decimal). The entity must be moving below this speed\n",
            "#  for a block to potentially break. Speed is measured in blocks per tick, which is metres per second\n",
            "#  divided by 20. The maximum for this value is 5.893: beyond this is faster than chunks can even load.\n",
            "#* If the max speed is less than the min speed they will be switched around internally.\n",
            "#* You can add extra values of any format, separated by spaces, for any mod entities that might require\n",
            "#  them.\n",
            "\n#--Tips--\n",
            "#* Certain entities will get a default break speed if not in this file, just so that the file doesn't\n",
            "#  become totally huge. This applies to: mobs and animals; arrows; fireballs; minecarts; firework\n",
            "#  rockets; boats; primed TNT; falling blocks. Writing an entity in here will override the default.\n",
            "#* It is more realistic if the smaller the mob, the larger the break speed.\n",
            "#* Giving a mob a minimum speed of 0 is risky; a single step towards a fragile block will break it.\n",
            "\n#--Example lines which you might want to uncomment--\n",
            "#Let zombies walk through fragile blocks\n",
            "#minecraft:zombie 0.1 6.0\n",
    };
}
