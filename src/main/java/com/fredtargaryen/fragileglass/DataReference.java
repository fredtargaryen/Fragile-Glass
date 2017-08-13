package com.fredtargaryen.fragileglass;

import net.minecraft.util.ResourceLocation;

// Change version number in: DataReference; build.gradle; mcmod.info
//Snowy seeds to test thin ice:
//Vanilla:
public class DataReference
{
    //MAIN MOD DETAILS
    public static final String MODID = "ftfragileglass";
    public static final String MODNAME = "Fragile Glass and Thin Ice";
    public static final String VERSION = "1.8.1";
    //PROXY PATHS
    public static final String CLIENTPROXYPATH = "com.fredtargaryen.fragileglass.proxy.ClientProxy";
    public static final String SERVERPROXYPATH = "com.fredtargaryen.fragileglass.proxy.ServerProxy";

    public static final double GLASS_DETECTION_RADIUS = 10.0;
    //The minimum speed a permitted entity must be travelling to break a block.
    //This should be 5.5 m/s.
    //Divided by 20: 0.275 blocks per tick.
    public static final double MINIMUM_ENTITY_SPEED = 0.05;

    public static final ResourceLocation CLIENT_CAN_BREAK_LOCATION = new ResourceLocation(DataReference.MODID, "IClientCanBreakCapability");
    public static final ResourceLocation SERVER_CAN_BREAK_LOCATION = new ResourceLocation(DataReference.MODID, "IServerCanBreakCapability");

    public static final ResourceLocation FRAGILE_CAP_LOCATION = new ResourceLocation(DataReference.MODID, "IFragileCapability");
}
