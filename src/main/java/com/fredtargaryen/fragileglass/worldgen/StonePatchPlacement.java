package com.fredtargaryen.fragileglass.worldgen;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.placement.BasePlacement;

import java.util.Random;

public class StonePatchPlacement extends BasePlacement<StonePatchPlacementConfig> {
    @Override
    public <C extends IFeatureConfig> boolean generate(IWorld world, IChunkGenerator<? extends IChunkGenSettings> chunkGen, Random random, BlockPos blockPos, StonePatchPlacementConfig stonePatchPlacementConfig, Feature<C> feature, C c) {
        int y = 0;
        //Coords of "top left" blocks in chunk
        int cornerX = (blockPos.getX() / 16) * 16;
        int cornerZ = (blockPos.getZ() / 16) * 16;
        //Possible middle block in patch
        Block candidate;
        boolean previousBlockSolid = true;
        //The BlockPos where patch generation will be attempted
        BlockPos patchCentre = new BlockPos(cornerX + random.nextInt(16), 0, cornerZ + random.nextInt(16));
        while(y < 256) {
            if (world.getBlockState(patchCentre).isBlockNormalCube()) {
                if (!previousBlockSolid) {
                    previousBlockSolid = true;
                    candidate = world.getBlockState(patchCentre).getBlock();
                    if (candidate == Blocks.STONE) {
                        feature.func_212245_a(world, chunkGen, random, patchCentre, c);
                        patchCentre = new BlockPos(cornerX + random.nextInt(16), y, cornerZ + random.nextInt(16));
                    }
                }
            } else {
                if (previousBlockSolid) {
                    previousBlockSolid = false;
                }
            }
            patchCentre = patchCentre.up();
            ++y;
        }
        return true;
    }
}
