package com.fredtargaryen.fragileglass.worldgen;

import com.fredtargaryen.fragileglass.config.WorldgenConfig;
import com.mojang.serialization.Codec;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class StonePatchGenConfig implements IFeatureConfig {
    public int avePatchSize;

    public static final Codec<StonePatchGenConfig> factory;

    public static final StonePatchGenConfig config = new StonePatchGenConfig();

    static {
        factory = Codec.unit(() -> config);
    }

    public StonePatchGenConfig() {
        this.avePatchSize = WorldgenConfig.AVG_PATCH_SIZE_STONE.get();
    }
}
