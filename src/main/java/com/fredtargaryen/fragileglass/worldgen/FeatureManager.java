package com.fredtargaryen.fragileglass.worldgen;

import com.fredtargaryen.fragileglass.config.WorldgenConfig;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.ForgeRegistries;

import static com.fredtargaryen.fragileglass.FragileGlassBase.ICE_FEATURE;
import static com.fredtargaryen.fragileglass.FragileGlassBase.STONE_FEATURE;

public class FeatureManager {
    public static IcePatchPlacement ICE_PLACEMENT;
    public static StonePatchPlacement STONE_PLACEMENT;

    public void registerFeatures(RegistryEvent.Register<Feature<?>> event) {
        ICE_FEATURE = new IcePatchGen(IcePatchGenConfig::factory);
        ICE_FEATURE.setRegistryName("icepatchgen");
        ICE_PLACEMENT = new IcePatchPlacement(IcePatchPlacementConfig::factory);
        STONE_FEATURE = new StonePatchGen(StonePatchGenConfig::factory);
        STONE_FEATURE.setRegistryName("stonepatchgen");
        STONE_PLACEMENT = new StonePatchPlacement(StonePatchPlacementConfig::factory);
        event.getRegistry().registerAll(ICE_FEATURE, STONE_FEATURE);
    }

    public void registerGenerators() {
        if(WorldgenConfig.GEN_THIN_ICE.get()) {
            //Create the feature, its config, the placement controller and its config
            IcePatchGenConfig ipgc = new IcePatchGenConfig();
            IcePatchPlacementConfig ippc = new IcePatchPlacementConfig();
            //Register these in all Biomes necessary
            for(Biome b : ForgeRegistries.BIOMES) {
                if(b.getDefaultTemperature() < 0.2)
                    b.addFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, ICE_FEATURE.withConfiguration(ipgc).withPlacement(ICE_PLACEMENT.configure(ippc)));
            }
        }
        if(WorldgenConfig.GEN_WEAK_STONE.get()) {
            //Create the feature, its config, the placement controller and its config
            StonePatchGenConfig spgc = new StonePatchGenConfig();
            StonePatchPlacementConfig sppc = new StonePatchPlacementConfig();
            StonePatchGen spg = new StonePatchGen(StonePatchGenConfig::factory);
            StonePatchPlacement spp = new StonePatchPlacement(StonePatchPlacementConfig::factory);
            //Register these in all Biomes necessary
            for(Biome b : ForgeRegistries.BIOMES){
                b.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, STONE_FEATURE.withConfiguration(spgc).withPlacement(STONE_PLACEMENT.configure(sppc)));
            }
        }
    }
}
