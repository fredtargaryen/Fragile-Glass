package com.fredtargaryen.fragileglass.world;

import com.fredtargaryen.fragileglass.DataReference;
import com.fredtargaryen.fragileglass.FragileGlassBase;
import com.fredtargaryen.fragileglass.entity.capability.IBreakCapability;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.item.FireworkRocketEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.item.minecart.MinecartEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.util.HashMap;

/**
 * Responsible for everything to do with entity break data from fragileglassft_entities.cfg.
 */
public class EntityDataManager extends DataManager<EntityType, BreakerData> {

    public EntityDataManager() {
        super("entities");
    }

    public void addCapabilityIfPossible(Entity e, AttachCapabilitiesEvent<Entity> evt) {
        BreakerData breakerData = this.data.get(e.getType());
        if (breakerData == null) {
            if (e instanceof LivingEntity
                    || e instanceof ArrowEntity
                    || e instanceof FireballEntity
                    || e instanceof MinecartEntity
                    || e instanceof FireworkRocketEntity
                    || e instanceof BoatEntity
                    || e instanceof TNTEntity
                    || e instanceof FallingBlockEntity) {
                evt.addCapability(DataReference.BREAK_LOCATION, new ICapabilityProvider() {
                    IBreakCapability inst = FragileGlassBase.BREAKCAP.getDefaultInstance();

                    @Override
                    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
                        return capability == FragileGlassBase.BREAKCAP ? LazyOptional.of(() -> (T) inst) : LazyOptional.empty();
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
                        Vec3d motion = e.getMotion();
                        return motion.x * motion.x + motion.y * motion.y + motion.z * motion.z;
                    }

                    @Override
                    public boolean isAbleToBreak(Entity e, double speedSq) {
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

    /**
     * Detect and read all block/tile entity config files. MUST be called when all Blocks and TileEntityTypes have been registered!
     */
    public void loadEntityData() {
        this.loadDataFromConfigDir(new EntityConfigLoader(this, this.data));
    }

    //Doesn't look like I can read from assets so sadly all this is needed for now
    private static final String[] defaultFileData = new String[] {
            "###################################################\n",
            "#FRAGILE GLASS AND THIN ICE CONFIG FILE - ENTITIES#\n",
            "###################################################\n",
            "#THINK VERY CAREFULLY AND BACK UP YOUR WORLDS BEFORE ADDING ENTRIES HERE!\n",
            "#(You probably don't really want to make ZOMBIES ABLE TO BREAK EVERYTHING, for example.)\n",
            "#Here is where you can configure the speeds required for each entity to break a fragile block.\n",
            "#\n#--Limitations--\n",
            "#* This will not work for entities which only appear on the client, such as particles.\n",
            "#* If your entry is not for a valid block, the mod will assume you entered a tile entity. However it\n",
            "#  cannot check if tile entities are valid, so you won't be warned. Check your spellings carefully.\n",
            "#\n#--How to customise--\n",
            "#To add a comment to the file, start the line with a # symbol.\n",
            "#To make an entity able to break fragile blocks, add a new row in this file following this format:\n",
            "#<modid>:<ID> <min speed> <max speed> <extra values>\n",
            "#* 'modid:ID' is the ResourceLocation string used to register the entity with Forge.\n",
            "#  - You can usually find this by looking at the entity in-game with the F3 menu on.\n",
            "#* The first number is a minimum speed (must be decimal). The entity must be moving above this speed\n",
            "#  for a block to potentially break. Speed is measured in blocks per tick, which is metres per second\n",
            "#  divided by 20. The minimum for this value is 0.0, i.e. any movement could break a block.\n",
            "#* The second number is a maximum speed (must be decimal). The entity must be moving below this speed\n",
            "#  for a block to potentially break. Speed is measured in blocks per tick, which is metres per second\n",
            "#  divided by 20. The maximum for this value is 5.893: beyond this is faster than chunks can even load.\n",
            "#* If the max speed is less than the min speed they will be switched around internally.\n",
            "#* You can add extra values of any format, separated by spaces, for any mod entities that might require\n",
            "#  them.\n",
            "#\n#--Tips--\n",
            "#* Certain entities will get a default break speed if not in this file, just so that the file doesn't\n",
            "#  become totally huge. This applies to: mobs and animals; arrows; fireballs; minecarts; firework\n",
            "#  rockets; boats; primed TNT; falling blocks. Writing an entity in here will override the default.\n",
            "#* It is more realistic if the smaller the mob, the larger the break speed.\n",
            "#* Giving a mob a minimum speed of 0 is risky; a single step towards a fragile block will break it.\n",
            "#\n#--Example lines which you might want to uncomment--\n",
            "#Let zombies walk through fragile blocks\n",
            "#minecraft:zombie 0.1 6.0\n",
    };
}
