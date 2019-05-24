package com.fredtargaryen.fragileglass.worldgen;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.placement.BasePlacement;

import java.util.Random;

public class IcePatchPlacement extends BasePlacement<IcePatchPlacementConfig> {
    @Override
    public <C extends IFeatureConfig> boolean generate(IWorld world, IChunkGenerator<? extends IChunkGenSettings> iChunkGenerator, Random random, BlockPos blockPos, IcePatchPlacementConfig icePatchPlacementConfig, Feature<C> feature, C c) {
        if(random.nextInt(icePatchPlacementConfig.genChance) == 0) {
            BlockPos surfacePos = world.getHeight(Heightmap.Type.WORLD_SURFACE, blockPos).down();
            Biome b = world.getBiome(surfacePos);
            if (b.getTemperature(surfacePos) < 0.015F) {
                //Coords of "top left" blocks in chunk
                int chunkBlockX = surfacePos.getX() * 16;
                int chunkBlockZ = surfacePos.getZ() * 16;
                //The BlockPos where patch generation will be done
                BlockPos patchCentre;
                //Possible middle block in patch
                Block candidate;
                //Check 16 candidate blocks in the chunk to see if they are ice blocks
                int candX = chunkBlockX;
                int candZ = chunkBlockZ;
                //Whether to stop trying to generate a patch here. Does not imply any patches were generated.
//                boolean done = false;
//                while (!done) {
//                    //Loop down to first non-air block
//                    BlockPos pos = new BlockPos(candX, 256, candZ);
//                    boolean stop = false;
//                    while(!stop) {
//                        pos = pos.down();
//                        if(!world.getBlockState(pos).isAir(world, pos) || pos.getY() == -1) {
//                            stop = true;
//                        }
//                    }
//                    if(pos.getY() >= 0) {
//                        patchCentre = pos;
//                        candidate = world.getBlockState(patchCentre).getBlock();
//                        if (this.isBlockValidToTransform(candidate)) {
//                            done = this.attemptPatch(random, chunkX, chunkZ, patchCentre, world);
//                        }
//                        if (candX > chunkBlockX + 15) {
//                            done = true;
//                        } else {
//                            if (candZ > chunkBlockZ + 15) {
//                                candX += 2;
//                                candZ = chunkBlockZ;
//                            } else {
//                                candZ += 2;
//                            }
//                        }
//                    }
//                }
                return feature.func_212245_a(world, iChunkGenerator, random, surfacePos, c);
            }
        }
        return false;
    }
}
