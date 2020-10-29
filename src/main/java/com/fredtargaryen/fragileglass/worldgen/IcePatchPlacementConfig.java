package com.fredtargaryen.fragileglass.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.placement.ChanceConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;

public class IcePatchPlacementConfig implements IPlacementConfig {
    public static final Codec<IcePatchPlacementConfig> FACTORY = Codec.INT.fieldOf("chance").xmap(IcePatchPlacementConfig::new, config -> config.genChance).codec();

    public int genChance;

    public IcePatchPlacementConfig(int genChance) {
        this.genChance = genChance;
    }
}
