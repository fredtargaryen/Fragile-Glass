package com.fredtargaryen.fragileglass.worldgen;

import com.fredtargaryen.fragileglass.FragileGlassBase;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

import java.util.Random;

public class StonePatchGen extends Feature<StonePatchGenConfig> {
    private static final double TWOPI = Math.PI * 2;
    private static final double PIFRACTION = Math.PI / 6.0;

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
    public boolean func_212245_a(IWorld world, IChunkGenerator<? extends IChunkGenSettings> chunkGen, Random random, BlockPos pos, StonePatchGenConfig config) {
        BlockPos.MutableBlockPos nextBlockPos = new BlockPos.MutableBlockPos(0, 0, 0);
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
                if(nextBlock == Blocks.STONE) {
                    //Adds a little randomness to the outside of patches, to avoid perfect circles all the time
                    if (rad > patchRadius - 2) {
                        if (random.nextBoolean()) {
                            world.setBlockState(nextBlockPos, FragileGlassBase.WEAK_STONE.getDefaultState(), 18);
                        }
                    } else {
                        world.setBlockState(nextBlockPos, FragileGlassBase.WEAK_STONE.getDefaultState(), 18);
                    }
                }
            }
        }
        world.setBlockState(patchCentre, FragileGlassBase.WEAK_STONE.getDefaultState(), 18);
        return true;
    }
}
