package com.fredtargaryen.fragileglass.worldgen;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

public abstract class PatchGen implements IWorldGenerator
{
    //If it goes a long time without genning a patch, forces a bonus patch to gen
    protected int timeSinceLastPatch;
    protected final int timeToWaitBeforeBonusPatch;
    private int genChance;
    protected int avePatchSize;
    private Block blockToSet;
    private static final double piFraction = Math.PI / 6;
    private static final double twoPi = Math.PI * 2;

    public PatchGen (int genChance, int avePatchSize, Block blockToSet) {
        this.genChance = genChance;
        this.avePatchSize = avePatchSize;
        this.blockToSet =  blockToSet;
        this.timeSinceLastPatch = 0;
        this.timeToWaitBeforeBonusPatch = genChance + 1;
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
    @Override
    public abstract void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider);

    protected boolean attemptPatch(Random random, int chunkX, int chunkZ, BlockPos patchCentre, World world)
    {
        if(random.nextInt(this.genChance) == 0 && this.genPatch(random, patchCentre, world))
        {
            this.timeSinceLastPatch = 0;
            return true;
        }
        else if(this.timeSinceLastPatch >= this.timeToWaitBeforeBonusPatch && this.genPatch(random, patchCentre, world))
        {
            this.timeSinceLastPatch = 0;
            return true;
        }
        else
        {
            this.timeSinceLastPatch++;
            return false;
        }
    }

    protected boolean genPatch(Random random, BlockPos patchCentre, World world)
    {
        BlockPos.MutableBlockPos nextBlockPos = new BlockPos.MutableBlockPos(0, 0, 0);
        Block nextBlock;
        int patchRadius = (int) (((2 * random.nextGaussian()) + this.avePatchSize) / 2);
        for (int rad = patchRadius; rad > 0; rad--)
        {
            int nextY = patchCentre.getY();
            for (double r = 0; r < twoPi; r += piFraction)
            {
                int nextX = (int) (patchCentre.getX() + (rad * Math.cos(r)));
                int nextZ = (int) (patchCentre.getZ() + (rad * Math.sin(r)));
                nextBlockPos.setPos(nextX, nextY, nextZ);
                nextBlock = world.getBlockState(nextBlockPos).getBlock();
                if(this.isBlockValidToTransform(nextBlock))
                {
                    //Adds a little randomness to the outside of patches, to avoid perfect circles all the time
                    if (rad > patchRadius - 2) {
                        if (random.nextBoolean()) {
                            world.setBlockState(nextBlockPos, this.blockToSet.getDefaultState());
                        }
                    } else {
                        world.setBlockState(nextBlockPos, this.blockToSet.getDefaultState());
                    }
                }
            }
        }
        world.setBlockState(patchCentre, this.blockToSet.getDefaultState());
        System.out.println("Patch at "+patchCentre);
        return true;
    }

    protected abstract boolean isBlockValidToTransform(Block block);
}
