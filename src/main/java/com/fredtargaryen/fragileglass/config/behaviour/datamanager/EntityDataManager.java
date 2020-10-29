package com.fredtargaryen.fragileglass.config.behaviour.datamanager;

import com.fredtargaryen.fragileglass.DataReference;
import com.fredtargaryen.fragileglass.FragileGlassBase;
import com.fredtargaryen.fragileglass.config.behaviour.configloader.ConfigLoader;
import com.fredtargaryen.fragileglass.config.behaviour.configloader.EntityConfigLoader;
import com.fredtargaryen.fragileglass.config.behaviour.data.BreakerData;
import com.fredtargaryen.fragileglass.config.behaviour.data.FragilityData;
import com.fredtargaryen.fragileglass.entity.capability.IBreakCapability;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.item.minecart.MinecartEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Responsible for everything to do with entity break data from fragileglassft_entities.cfg.
 */
public class EntityDataManager extends DataManager<EntityType, BreakerData> {
    /**
     * Processes config lines from files, code or commands
     */
    private ConfigLoader entityConfigLoader;

    public EntityDataManager() {
        super("entities");
        this.entityConfigLoader = new EntityConfigLoader(this, this.data);
    }

    public void addCapabilityIfPossible(Entity e, AttachCapabilitiesEvent<Entity> evt) {
        //A player would reasonably expect many existing entities to be able to break fragile blocks, but it is
        //very unlikely that anyone would go to the trouble of writing out all the config lines for every entity.
        //The following code does some type checks and creates BreakerDatas if the entity probably would break a block.
        BreakerData breakerData = this.data.get(e.getType());
        if (breakerData == null) {
            //A breakerdata for this entitytype has not been created yet
            if (e instanceof LivingEntity
                    || e instanceof ArrowEntity
                    || e instanceof FireballEntity
                    || e instanceof MinecartEntity
                    || e instanceof FireworkRocketEntity
                    || e instanceof BoatEntity
                    || e instanceof TNTEntity
                    || e instanceof FallingBlockEntity) {
                breakerData = new BreakerData(DataReference.MINIMUM_ENTITY_SPEED_SQUARED, DataReference.MAXIMUM_ENTITY_SPEED_SQUARED, new String[]{});
                this.data.put(e.getType(), breakerData);
            }
        }
        if(breakerData != null){
            //The entity was predefined (via configs or commands) as being able to break a block,
            //or a BreakerData was automatically created above.
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
                        Vector3d motion = e.getMotion();
                        return motion.x * motion.x + motion.y * motion.y + motion.z * motion.z;
                    }

                    @Override
                    public boolean isAbleToBreak(Entity e, double speedSq) {
                        BreakerData breakerData = EntityDataManager.this.data.get(e.getType());
                        if(breakerData == null) return false;
                        return speedSq >= breakerData.getMinSpeedSquared()
                                && speedSq <= breakerData.getMaxSpeedSquared();
                    }

                    @Override
                    public double getMotionX(Entity e) {
                        return e.getMotion().x;
                    }

                    @Override
                    public double getMotionY(Entity e) {
                        return e.getMotion().y;
                    }

                    @Override
                    public double getMotionZ(Entity e) {
                        return e.getMotion().z;
                    }

                    @Override
                    public byte getNoOfBreaks(Entity e) {
                        return 1;
                    }
                };

                @Nullable
                @Override
                public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
                    return capability == FragileGlassBase.BREAKCAP ? LazyOptional.of(() -> (T) inst) : LazyOptional.empty();
                }
            };
            evt.addCapability(DataReference.BREAK_LOCATION, iCapProv);
        }
    }

    @Override
    protected String[] getDefaultConfigFileText() { return defaultFileData; }

    @Override
    public boolean loadData() {
        return this.loadDataFromConfigDir(this.entityConfigLoader);
    }

    @Override
    public void parseConfigLine(String configLine) throws ConfigLoader.ConfigLoadException {
        this.entityConfigLoader.parseArbitraryString(configLine);
    }

    @Override
    public void removeBehaviour(EntityType key, @Nullable FragilityData.FragileBehaviour behaviour) {
        this.data.remove(key);
    }

    @Override
    public String stringifyBehaviour(EntityType key, @Nullable FragilityData.FragileBehaviour behaviour) {
        return key.getRegistryName() + " " + this.data.get(key).toString();
    }

    //Doesn't look like I can read from assets so sadly all this is needed for now
    private static final String[] defaultFileData = new String[] {
            "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n",
            "@FRAGILE GLASS AND THIN ICE CONFIG FILE - ENTITIES@\n",
            "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n",
            "@THINK VERY CAREFULLY AND BACK UP YOUR WORLDS BEFORE ADDING ENTRIES HERE!\n",
            "@(You probably don't really want to make ZOMBIES ABLE TO BREAK EVERYTHING, for example.)\n",
            "@Here is where you can configure the speeds required for each entity to break a fragile block.\n",

            "\n@--Limitations--\n",
            "@* This will not work for entities which only appear on the client, such as particles.\n",
            "@* If your entry is not for a valid block, the mod will assume you entered a tile entity. However it\n",
            "@  cannot check if tile entities are valid, so you won't be warned. Check your spellings carefully.\n",

            "\n@--How to customise--\n",
            "@To add a comment to the file, start the line with a @ symbol.\n",
            "@To make an entity able to break fragile blocks, add a new row in this file following this format:\n",
            "@modid:ID minSpeed maxSpeed extraValues\n",
            "@* 'modid:ID' is the ResourceLocation string used to register the entity with Forge.\n",
            "@  - You can usually find this by looking at the entity in-game with the F3 menu on.\n",
            "@* minSpeed is a minimum speed (must be decimal). The entity must be moving above this speed\n",
            "@  for a block to potentially break. Speed is measured in blocks per tick, which is metres per second\n",
            "@  divided by 20. The minimum for this value is 0.0, i.e. any movement could break a block.\n",
            "@* maxSpeed is a maximum speed (must be decimal). The entity must be moving below this speed\n",
            "@  for a block to potentially break. Speed is measured in blocks per tick, which is metres per second\n",
            "@  divided by 20. The maximum for this value is 5.893: beyond this is faster than chunks can even load.\n",
            "@  - If the max speed is less than the min speed they will be switched around internally.\n",
            "@* You can add extra values of any format, separated by spaces, for any mod entities that might require\n",
            "@  them.\n",

            "\n@--Tips--\n",
            "@* Certain entities will get a default break speed if not in this file, just so that the file doesn't\n",
            "@  become totally huge. This applies to: mobs and animals; arrows; fireballs; minecarts; firework\n",
            "@  rockets; boats; primed TNT; falling blocks. Writing an entity in here will override the default.\n",
            "@* It is more realistic if the smaller the mob, the larger the break speed.\n",
            "@* Giving a mob a minimum speed of 0 is risky; a single step towards a fragile block will break it.\n",

            "\n@--Example lines which you might want to uncomment--\n",
            "@Lets raiders break fragile blocks\n",
            "@#minecraft:raiders 0.05 6.0\n\n",
    };
}
