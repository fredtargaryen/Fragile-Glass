package com.fredtargaryen.fragileglass.worldgen;

import com.fredtargaryen.fragileglass.config.WorldgenConfig;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class StonePatchGenConfig implements IFeatureConfig {
    public int avePatchSize;

    public StonePatchGenConfig() {
        this.avePatchSize = WorldgenConfig.AVG_PATCH_SIZE_STONE.get();
    }
}
