package com.fredtargaryen.fragileglass.worldgen;

import com.fredtargaryen.fragileglass.FragileGlassBase;
import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.Feature;

import java.util.Random;

public class IcePatchGen extends Feature<IcePatchGenConfig> {
    private static final double TWOPI = 2 * Math.PI;
    private static final double PIFRACTION = Math.PI / 6.0;
    private static final ResourceLocation ICE_TAG = new ResourceLocation("minecraft", "ice");

    public IcePatchGen(Codec<IcePatchGenConfig> factory) { super(factory); }

    /**
     * Generate the feature at the given BlockPos (which was validated by an IcePatchPlacement instance).
     * @param world
     * @param chunkGen
     * @param random
     * @param pos The position of the patch centre. Will be readjusted to fit the patch radius.
     * @param config
     * @return
     */
    @Override
    public boolean generate(ISeedReader world, ChunkGenerator chunkGen, Random random, BlockPos pos, IcePatchGenConfig config) {
        if(FragileGlassBase.ICE_BLOCKS == null)
            FragileGlassBase.ICE_BLOCKS = BlockTags.getCollection().get(ICE_TAG); // Hopefully replaces GetOrCreate
        BlockPos.Mutable nextBlockPos = new BlockPos.Mutable(0, 0, 0);
        Block nextBlock;
        int patchRadius = (int) (((2 * random.nextGaussian()) + config.avePatchSize) / 2);
        BlockPos patchCentre = pos;
        int cornerX = patchCentre.getX();
        int cornerZ = patchCentre.getZ();
        //Move centre of patch so that patches cannot go outside the chunk
        double centreX = Math.max(patchCentre.getX(), cornerX + patchRadius);
        centreX = Math.min(centreX, cornerX + 16 - patchRadius);
        double centreZ = Math.max(patchCentre.getZ(), cornerZ + patchRadius);
        centreZ = Math.min(centreZ, cornerZ + 16 - patchRadius);
        double centreY = world.getHeight(Heightmap.Type.WORLD_SURFACE, (int) centreX, (int) centreZ) - 1;
        patchCentre = new BlockPos(centreX, centreY, centreZ);
        for (int rad = patchRadius; rad > 0; rad--) {
            for (double r = 0; r < TWOPI; r += PIFRACTION) {
                int nextX = (int) (centreX + (rad * Math.cos(r)));
                int nextZ = (int) (centreZ + (rad * Math.sin(r)));
                nextBlockPos.setPos(nextX, centreY, nextZ);
                nextBlock = world.getBlockState(nextBlockPos).getBlock();
                if(FragileGlassBase.ICE_BLOCKS.contains(nextBlock)) {
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
        if(FragileGlassBase.ICE_BLOCKS.contains(world.getBlockState(patchCentre).getBlock())) {
            world.setBlockState(patchCentre, FragileGlassBase.THIN_ICE.getDefaultState(), 18);
        }
        return true;
    }
}
