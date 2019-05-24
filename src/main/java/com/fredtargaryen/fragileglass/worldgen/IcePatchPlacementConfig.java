package com.fredtargaryen.fragileglass.worldgen;

import com.fredtargaryen.fragileglass.config.WorldgenConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;

public class IcePatchPlacementConfig implements IPlacementConfig {
    public int genChance;

    public IcePatchPlacementConfig() {
        this.genChance = WorldgenConfig.GEN_CHANCE_ICE.get();
    }
}
