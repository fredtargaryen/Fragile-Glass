package com.fredtargaryen.fragileglass.worldgen;

import com.fredtargaryen.fragileglass.FragileGlassBase;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;

import java.util.Random;

public class PatchGenStone extends PatchGen
{
    public PatchGenStone()
    {
        super(FragileGlassBase.genChanceStone, FragileGlassBase.avePatchSizeStone, FragileGlassBase.weakStone);
    }

    /**
     * Finds suitable places in the chunk to call attemptPatch.
     *
     * @param random the chunk specific {@link Random}.
     * @param chunkX the chunk X coordinate of this chunk.
     * @param chunkZ the chunk Z coordinate of this chunk.
     * @param world : additionalData[0] The minecraft {@link World} we're generating for.
     * @param chunkGenerator : additionalData[1] The {@link IChunkProvider} that is generating.
     * @param chunkProvider : additionalData[2] {@link IChunkProvider} that is requesting the world generation.
     *
     */
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        int y = 0;
        //Coords of "top left" blocks in chunk
        int chunkBlockX = chunkX * 16;
        int chunkBlockZ = chunkZ * 16;
        //Possible middle block in patch
        Block candidate;
        boolean previousBlockSolid = true;
        //The BlockPos where patch generation will be attempted
        BlockPos patchCentre = new BlockPos(chunkBlockX + random.nextInt(16), 0, chunkBlockZ + random.nextInt(16));
        while(y < 256) {
            if (world.getBlockState(patchCentre).isBlockNormalCube()) {
                if (!previousBlockSolid) {
                    previousBlockSolid = true;
                    candidate = world.getBlockState(patchCentre).getBlock();
                    if (this.isBlockValidToTransform(candidate)) {
                        this.attemptPatch(random, chunkX, chunkZ, patchCentre, world);
                        patchCentre = new BlockPos(chunkBlockX + random.nextInt(16), y, chunkBlockZ + random.nextInt(16));
                    }
                }
            }
            else
            {
                if (previousBlockSolid)
                {
                    previousBlockSolid = false;
                }
            }
            patchCentre = patchCentre.up();
            ++y;
        }
    }

    @Override
    protected boolean isBlockValidToTransform(Block block) {
        return block.getMaterial(block.getDefaultState()) == Material.ROCK;
    }
}
