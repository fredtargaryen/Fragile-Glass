package com.fredtargaryen.fragileglass.config;

/**
 * Replaces the Configuration system of Forge 1.12.2. @Config stuff is not usable in this version of Forge.
 */
public class FGConfig {

//    @Config.Comment(value = "If true, thin ice patches will generate on frozen bodies of water")
    public static boolean genThinIce = true;

//    @Config.Comment(value = "Average patch diameter")
    public static int avePatchSizeIce = 5;

//    @Config.Comment(value = "1 in x chance of patch appearing")
    public static int genChanceIce = 3;

//    @Config.Comment(value = "If true, weak stone patches will generate. Expect falls into lava!")
    public static boolean genWeakStone = false;

//    @Config.Comment(value = "Average patch diameter")
    public static int avePatchSizeStone = 5;

//    @Config.Comment(value = "1 in x chance of patch appearing")
    public static int genChanceStone = 3;
}
