package com.fredtargaryen.fragileglass.worldgen;

import com.fredtargaryen.fragileglass.config.WorldgenConfig;
import com.mojang.serialization.Codec;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class IcePatchGenConfig implements IFeatureConfig {
    public int avePatchSize;

    public static final Codec<IcePatchGenConfig> factory;

    public static final IcePatchGenConfig config = new IcePatchGenConfig();

    static {
        factory = Codec.unit(() -> config);
    }

    public IcePatchGenConfig() {
        this.avePatchSize = WorldgenConfig.AVG_PATCH_SIZE_ICE.get();
    }
}
