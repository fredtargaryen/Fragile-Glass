package com.fredtargaryen.fragileglass.worldgen;

import com.fredtargaryen.fragileglass.config.WorldgenConfig;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.placement.ChanceConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class FeatureManager {
    public static final IcePatchPlacement ICE_PLACEMENT = new IcePatchPlacement(ChanceConfig.CODEC);
    public static final StonePatchPlacement STONE_PLACEMENT = new StonePatchPlacement(ChanceConfig.CODEC);
    public static final Feature<IcePatchGenConfig> ICE_FEATURE = new IcePatchGen(IcePatchGenConfig.factory);
    public static final Feature<StonePatchGenConfig> STONE_FEATURE = new StonePatchGen(StonePatchGenConfig.factory);


    /**
     * This event fires when a Biome is created from json or when a registered biome is re-created for worldgen.
     * It allows mods to edit a biome (like add a mob spawn) before it gets used for worldgen.
     *
     * In order to maintain the most compatibility possible with other mods' modifications to a biome,
     * the event should be assigned a {@link net.minecraftforge.eventbus.api.EventPriority} as follows:
     *
     * - Additions to any list/map contained in a biome : {@link EventPriority#HIGH}
     * - Removals to any list/map contained in a biome : {@link EventPriority#NORMAL}
     * - Any other modification : {@link EventPriority#LOW}
     *
     * Be aware that another mod could have done an operation beforehand, so an expected value out of a vanilla biome might not
     * always be the same, depending on other mods.
     */
    @SubscribeEvent
    public static void loadBiome(BiomeLoadingEvent ble)
    {
        BiomeGenerationSettingsBuilder generation = ble.getGeneration();
        if(WorldgenConfig.GEN_THIN_ICE.get() && ble.getClimate().precipitation == Biome.RainType.SNOW)
        {
            generation.getFeatures(GenerationStage.Decoration.TOP_LAYER_MODIFICATION).add(() -> ICE_FEATURE.withConfiguration(new IcePatchGenConfig()).withPlacement(ICE_PLACEMENT.configure(new ChanceConfig(WorldgenConfig.GEN_CHANCE_ICE.get()))));
        }
        if(WorldgenConfig.GEN_WEAK_STONE.get()) {
            generation.getFeatures(GenerationStage.Decoration.RAW_GENERATION).add(() -> STONE_FEATURE.withConfiguration(new StonePatchGenConfig()).withPlacement(STONE_PLACEMENT.configure(new ChanceConfig(WorldgenConfig.GEN_CHANCE_STONE.get()))));
        }
    }

    public void registerFeatures(RegistryEvent.Register<Feature<?>> event) {
        ICE_FEATURE.setRegistryName("icepatchgen");
        STONE_FEATURE.setRegistryName("stonepatchgen");
        event.getRegistry().registerAll(ICE_FEATURE, STONE_FEATURE);
    }

    public void registerPlacements(RegistryEvent.Register<Placement<?>> event) {
        ICE_PLACEMENT.setRegistryName("icepatchplacement");
        STONE_PLACEMENT.setRegistryName("stonepatchplacement");
        event.getRegistry().registerAll(ICE_PLACEMENT, STONE_PLACEMENT);
    }
}
