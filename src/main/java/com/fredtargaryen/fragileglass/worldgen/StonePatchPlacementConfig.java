package com.fredtargaryen.fragileglass.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.placement.IPlacementConfig;

public class StonePatchPlacementConfig implements IPlacementConfig {
    public static final Codec<StonePatchPlacementConfig> FACTORY = Codec.INT.fieldOf("chance").xmap(StonePatchPlacementConfig::new, config -> config.genChance).codec();

    public int genChance;

    public StonePatchPlacementConfig(int genChance) {
        this.genChance = genChance;
    }
}
