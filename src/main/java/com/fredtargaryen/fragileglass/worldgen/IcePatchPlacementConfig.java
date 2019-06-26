package com.fredtargaryen.fragileglass.worldgen;

import com.fredtargaryen.fragileglass.config.WorldgenConfig;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.gen.placement.IPlacementConfig;

public class IcePatchPlacementConfig implements IPlacementConfig {
    public int genChance;

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> p_214719_1_) {
        return null;
    }

    public static IcePatchPlacementConfig factory(Dynamic data) {
        return new IcePatchPlacementConfig();
    }

    public IcePatchPlacementConfig() {
        this.genChance = WorldgenConfig.GEN_CHANCE_ICE.get();
    }
}
