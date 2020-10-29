package com.fredtargaryen.fragileglass.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class WorldgenConfig {
    public static ForgeConfigSpec.BooleanValue GEN_THIN_ICE;

    public static ForgeConfigSpec.IntValue AVG_PATCH_SIZE_ICE;

    public static ForgeConfigSpec.IntValue GEN_CHANCE_ICE;

    public static ForgeConfigSpec.BooleanValue GEN_WEAK_STONE;

    public static ForgeConfigSpec.IntValue AVG_PATCH_SIZE_STONE;

    public static ForgeConfigSpec.IntValue GEN_CHANCE_STONE;

    public static ForgeConfigSpec.BooleanValue SHOW_SUCCESS_MESSAGE;

    public static void init(ForgeConfigSpec.Builder serverBuilder) {
        serverBuilder.comment("Status messages");
        SHOW_SUCCESS_MESSAGE = serverBuilder.comment("If true, enable successful load message on login")
                .define("settings.showstatus", true);
        serverBuilder.comment("Customise generation of thin ice and weak stone");
        GEN_THIN_ICE = serverBuilder.comment("If true, thin ice patches will generate on frozen bodies of water")
                .define("thinice.enable", true);
        AVG_PATCH_SIZE_ICE = serverBuilder.comment("Average patch diameter")
                .defineInRange("thinice.avgPatchSize", 5, 1, 12);
        GEN_CHANCE_ICE = serverBuilder.comment("1 in x chance of patch appearing")
                .defineInRange("thinice.genChance", 3, 1, 5);
        GEN_WEAK_STONE = serverBuilder.comment("If true, weak stone patches will generate on cave ceilings. Expect falls into lava!")
                .define("weakstone.enable", false);
        AVG_PATCH_SIZE_STONE = serverBuilder.comment("Average patch diameter")
                .defineInRange("weakstone.avgPatchSize", 5, 1, 12);
        GEN_CHANCE_STONE = serverBuilder.comment("1 in x chance of patch appearing")
                .defineInRange("weakstone.genChance", 3, 1, 5);
    }
}
