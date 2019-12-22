package com.fredtargaryen.fragileglass.worldgen;

import com.mojang.datafixers.Dynamic;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.placement.Placement;

import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;

public class IcePatchPlacement extends Placement<IcePatchPlacementConfig> {
    public IcePatchPlacement(Function<Dynamic<?>, ? extends IcePatchPlacementConfig> func) { super(func); }

    @Override
    public Stream<BlockPos> getPositions(IWorld world, ChunkGenerator<? extends GenerationSettings> iChunkGenerator, Random random, IcePatchPlacementConfig icePatchPlacementConfig, BlockPos blockPos) {
        if(random.nextInt(icePatchPlacementConfig.genChance) == 0) {
            BlockPos surfacePos = world.getHeight(Heightmap.Type.WORLD_SURFACE, blockPos).down();
            Biome b = iChunkGenerator.getBiomeProvider().func_225526_b_(surfacePos.getX(), surfacePos.getY(), surfacePos.getZ());
            //Biome b = world.getBiome(surfacePos);
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
                return Stream.of(surfacePos);
            }
        }
        return Stream.empty();
    }
}
