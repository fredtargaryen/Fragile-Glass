package com.fredtargaryen.fragileglass.worldgen;

import com.fredtargaryen.fragileglass.config.WorldgenConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;

public class StonePatchPlacementConfig implements IPlacementConfig {
    public int genChance;

    public StonePatchPlacementConfig() {
        this.genChance = WorldgenConfig.GEN_CHANCE_STONE.get();
    }
}
