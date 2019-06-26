package com.fredtargaryen.fragileglass.worldgen;

import com.fredtargaryen.fragileglass.config.WorldgenConfig;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;

import java.util.Iterator;

public class FeatureManager {
    public void registerGenerators() {
        if(WorldgenConfig.GEN_THIN_ICE.get()) {
            //Create the feature, its config, the placement controller and its config
            IcePatchGenConfig ipgc = new IcePatchGenConfig();
            IcePatchPlacementConfig ippc = new IcePatchPlacementConfig();
            IcePatchGen ipg = new IcePatchGen(IcePatchGenConfig::factory);
            IcePatchPlacement ipp = new IcePatchPlacement(IcePatchPlacementConfig::factory);
            //Register these in all Biomes necessary
            Iterator<Biome> i = Biome.BIOMES.iterator();
            while(i.hasNext()) {
                Biome b = i.next();
                b.addFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, Biome.createDecoratedFeature(ipg, ipgc, ipp, ippc));
            }
        }
        if(WorldgenConfig.GEN_WEAK_STONE.get()) {
            //Create the feature, its config, the placement controller and its config
            StonePatchGenConfig spgc = new StonePatchGenConfig();
            StonePatchPlacementConfig sppc = new StonePatchPlacementConfig();
            StonePatchGen spg = new StonePatchGen(StonePatchGenConfig::factory);
            StonePatchPlacement spp = new StonePatchPlacement(StonePatchPlacementConfig::factory);
            //Register these in all Biomes necessary
            Iterator<Biome> i = Biome.BIOMES.iterator();
            while(i.hasNext()) {
                Biome b = i.next();
                b.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Biome.createDecoratedFeature(spg, spgc, spp, sppc));
            }
        }
    }
}
