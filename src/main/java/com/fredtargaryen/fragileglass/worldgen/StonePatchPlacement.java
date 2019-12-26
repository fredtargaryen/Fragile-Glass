package com.fredtargaryen.fragileglass.worldgen;

import com.mojang.datafixers.Dynamic;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.placement.Placement;

import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;

public class StonePatchPlacement extends Placement<StonePatchPlacementConfig> {
    public StonePatchPlacement(Function<Dynamic<?>, ? extends StonePatchPlacementConfig> func) {
        super(func);
    }

    @Override
    public Stream<BlockPos> getPositions(IWorld world, ChunkGenerator<? extends GenerationSettings> chunkGen, Random random, StonePatchPlacementConfig stonePatchPlacementConfig, BlockPos blockPos) {
        int y = 0;
        //Coords of "top left" blocks in chunk
        int cornerX = (blockPos.getX() / 16) * 16;
        int cornerZ = (blockPos.getZ() / 16) * 16;
        //Possible middle block in patch
        BlockState candidate;
        boolean previousBlockSolid = true;
        //The BlockPos where patch generation will be attempted
        BlockPos patchCentre = new BlockPos(cornerX + random.nextInt(16), 0, cornerZ + random.nextInt(16));
        while(y < 256) {
            if (world.getBlockState(patchCentre).getMaterial().isSolid()) {
                if (!previousBlockSolid) {
                    previousBlockSolid = true;
                    candidate = world.getBlockState(patchCentre);
                    if (candidate.getMaterial() == Material.ROCK) {
                        return Stream.of(patchCentre);
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
        return Stream.empty();
    }
}
