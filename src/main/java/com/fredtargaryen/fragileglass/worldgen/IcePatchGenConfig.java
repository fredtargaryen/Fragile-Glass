package com.fredtargaryen.fragileglass.worldgen;

import com.fredtargaryen.fragileglass.config.WorldgenConfig;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class IcePatchGenConfig implements IFeatureConfig {
    public int avePatchSize;

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> p_214634_1_) {
        return null;
    }

    public static IcePatchGenConfig factory(Dynamic data) {
        return new IcePatchGenConfig();
    }

    public IcePatchGenConfig() {
        this.avePatchSize = WorldgenConfig.AVG_PATCH_SIZE_ICE.get();
    }
}
