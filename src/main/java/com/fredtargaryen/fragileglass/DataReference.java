package com.fredtargaryen.fragileglass;

// Change version number in: DataReference; build.gradle; mcmod.info
public class DataReference
{
    //MAIN MOD DETAILS
    public static final String MODID = "ftfragileglass";
    public static final String MODNAME = "Fragile Glass";
    public static final String VERSION = "1.7.10-1.2";
    //PROXY PATHS
    public static final String CLIENTPROXYPATH = "com.fredtargaryen.fragileglass.proxy.ClientProxy";
    public static final String SERVERPROXYPATH = "com.fredtargaryen.fragileglass.proxy.ServerProxy";

    public static final double GLASS_DETECTION_RANGE = 0.75;
    //The minimum speed a permitted entity must be travelling to break a block.
    //This should be 5.5 m/s.
    //Divided by 20: 0.275 blocks per tick.
    public static final double MINIMUM_ENTITY_SPEED = 0.275;
}
