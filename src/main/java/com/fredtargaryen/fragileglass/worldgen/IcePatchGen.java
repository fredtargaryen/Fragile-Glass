package com.fredtargaryen.fragileglass.worldgen;

import com.fredtargaryen.fragileglass.FragileGlassBase;
import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

import java.util.Random;
import java.util.stream.Stream;

public class IcePatchGen extends Feature<IcePatchGenConfig> {
    private static final double TWOPI = 2 * Math.PI;
    private static final double PIFRACTION = Math.PI / 6.0;
    private static final ResourceLocation ICE_TAG = new ResourceLocation("minecraft", "ice");

    public IcePatchGen(Codec<IcePatchGenConfig> factory) { super(factory); }

    /**
     * Generate the feature at the given BlockPos (which was validated by an IPatchPlacement instance).
     * @param world
     * @param chunkGen
     * @param random
     * @param pos
     * @param config
     * @return
     */
    @Override
    public boolean func_241855_a(ISeedReader world, ChunkGenerator chunkGen, Random random, BlockPos pos, IcePatchGenConfig config) {
//        Biome b = iChunkGenerator.getBiomeProvider().getNoiseBiome(surfacePos.getX(), surfacePos.getY(), surfacePos.getZ());
//        //Biome b = world.getBiome(surfacePos);
//        if (b.getTemperature(surfacePos) < 0.015F) {
//            //Coords of "top left" blocks in chunk
//            int chunkBlockX = (surfacePos.getX() / 16) * 16;
//            int chunkBlockZ = (surfacePos.getZ() / 16) * 16;
//            //The BlockPos where patch generation will be done
//            BlockPos patchCentre;
//            //Possible middle block in patch
//            BlockState candidate;
//            //Check 16 candidate blocks in the chunk to see if they are ice blocks
//            int candX = chunkBlockX;
//            int candZ = chunkBlockZ;
//            //Whether to stop trying to generate a patch here. Does not imply any patches were generated.
//            boolean done = false;
//            while (!done) {
//                //Loop down to first non-air block
//                BlockPos pos = new BlockPos(candX, 256, candZ);
//                boolean stop = false;
//                while(!stop) {
//                    pos = pos.down();
//                    if(!world.getBlockState(pos).isAir(world, pos) || pos.getY() == -1) {
//                        stop = true;
//                    }
//                }
//                if(pos.getY() >= 0) {
//                    patchCentre = pos;
//                    candidate = world.getBlockState(patchCentre);
//                    if (this.isBlockValidToTransform(candidate.getBlock())) {
//                        return Stream.of(patchCentre);
//                    }
//                    if (candX > chunkBlockX + 15) {
//                        done = true;
//                    } else {
//                        if (candZ > chunkBlockZ + 15) {
//                            candX += 2;
//                            candZ = chunkBlockZ;
//                        } else {
//                            candZ += 2;
//                        }
//                    }
//                }
//            }
//        }
        if(FragileGlassBase.ICE_BLOCKS == null)
            FragileGlassBase.ICE_BLOCKS = BlockTags.getCollection().func_241834_b(ICE_TAG); // Hopefully replaces GetOrCreate
        BlockPos.Mutable nextBlockPos = new BlockPos.Mutable(0, 0, 0);
        Block nextBlock;
        int patchRadius = (int) (((2 * random.nextGaussian()) + config.avePatchSize) / 2);
        BlockPos patchCentre = pos;
        int cornerX = (patchCentre.getX() / 16) * 16;
        int cornerZ = (patchCentre.getZ() / 16) * 16;
        //Move centre of patch so that patches cannot go outside the chunk
        double centreX = Math.max(patchCentre.getX(), cornerX + patchRadius);
        centreX = Math.min(centreX, cornerX + 16 - patchRadius);
        double centreY = patchCentre.getY();
        double centreZ = Math.max(patchCentre.getZ(), cornerZ + patchRadius);
        centreZ = Math.min(centreZ, cornerZ + 16 - patchRadius);
        patchCentre = new BlockPos(centreX, centreY, centreZ);
        for (int rad = patchRadius; rad > 0; rad--) {
            for (double r = 0; r < TWOPI; r += PIFRACTION) {
                int nextX = (int) (centreX + (rad * Math.cos(r)));
                int nextZ = (int) (centreZ + (rad * Math.sin(r)));
                nextBlockPos.setPos(nextX, centreY, nextZ);
                nextBlock = world.getBlockState(nextBlockPos).getBlock();
                if(FragileGlassBase.ICE_BLOCKS.func_230236_b_().contains(nextBlock)) {
                    //Adds a little randomness to the outside of patches, to avoid perfect circles all the time
                    if (rad > patchRadius - 2) {
                        if (random.nextBoolean()) {
                            world.setBlockState(nextBlockPos, FragileGlassBase.THIN_ICE.getDefaultState(), 18);
                        }
                    } else {
                        world.setBlockState(nextBlockPos, FragileGlassBase.THIN_ICE.getDefaultState(), 18);
                    }
                }
            }
        }
        world.setBlockState(patchCentre, FragileGlassBase.THIN_ICE.getDefaultState(), 18);
        return true;
    }
}
