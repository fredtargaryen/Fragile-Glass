package com.fredtargaryen.fragileglass.worldgen;

import com.fredtargaryen.fragileglass.config.WorldgenConfig;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class IcePatchGenConfig implements IFeatureConfig {
    public int avePatchSize;

    public IcePatchGenConfig() {
        this.avePatchSize = WorldgenConfig.AVG_PATCH_SIZE_ICE.get();
    }
}
