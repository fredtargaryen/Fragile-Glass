package com.fredtargaryen.fragileglass;

import net.minecraft.util.ResourceLocation;

// Change version number in: DataReference; build.gradle; mcmod.info
public class DataReference
{
    //MAIN MOD DETAILS
    public static final String MODID = "fragileglassft";
    public static final String MODNAME = "Fragile Glass and Thin Ice";
    public static final String VERSION = "1.10.0";
    //PROXY PATHS
    public static final String CLIENTPROXYPATH = "com.fredtargaryen.fragileglass.proxy.ClientProxy";
    public static final String SERVERPROXYPATH = "com.fredtargaryen.fragileglass.proxy.ServerProxy";

    //The minimum speed a permitted entity must be travelling to break a fragile glass block.
    //This should be sprinting, which is over 5.5 m/s.
    //Divided by 20: 0.275 blocks per tick.
    public static final double MINIMUM_ENTITY_SPEED_SQUARED = 0.275 * 0.275;

    /**
     * On the client, for some reason walk speed is recorded as roughly 0.136.
     */
    public static final double PLAYER_WALK_SPEED = 0.135;

    /**
     * On the client, for some reason sprint speed is recorded as roughly 0.1655.
     */
    public static final double PLAYER_SPRINT_SPEED = 0.165;

    //Arbitrary high speed.
    //A potion of Speed "increases walking speed by 20% × level" (Minecraft Wiki)
    //At potion levels above 100 the player is moving faster than chunks can load so no point allowing higher speeds
    //Maximum speed = 5.612 m/s [sprint speed average] + (5.612 * 0.2 [20%] * 100 [potion level]) = 117.852 m/s
    //117.852 / 20 = 5.8926 blocks per tick.
    //Squared, to avoid a Math.sqrt() every tick: 34.7227348
    public static final double MAXIMUM_ENTITY_SPEED_SQUARED = 34.7227348;

    public static final ResourceLocation BREAK_LOCATION = new ResourceLocation(DataReference.MODID, "IBreakCapability");
    public static final ResourceLocation PLAYER_BREAK_LOCATION = new ResourceLocation(DataReference.MODID, "IBreakCapability2");

    public static final ResourceLocation FRAGILE_CAP_LOCATION = new ResourceLocation(DataReference.MODID, "IFragileCapability");
}
