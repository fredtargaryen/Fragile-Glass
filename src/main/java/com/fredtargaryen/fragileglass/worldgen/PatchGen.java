package com.fredtargaryen.fragileglass.worldgen;

import com.fredtargaryen.fragileglass.FragileGlassBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockIce;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

public class PatchGen implements IWorldGenerator
{
    //If it goes a long time without genning a patch, forces a bonus patch to gen
    private int timeSinceLastPatch;
    private final int timeToWaitBeforeBonusPatch;

    public PatchGen () {
        this.timeSinceLastPatch = 0;
        this.timeToWaitBeforeBonusPatch = FragileGlassBase.genChance + 1;
    }

    /**
     * Generate some world
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
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        //145 is the TerraFirmaCraft sea level - guessing other mods don't go any higher than this
        Biome b = world.getBiome(new BlockPos(chunkX, 145, chunkZ));
        if (b.getEnableSnow())
        {
            if(random.nextInt(FragileGlassBase.genChance) == 0 && this.genPatch(random, chunkX, chunkZ, world))
            {
                this.timeSinceLastPatch = 0;
            }
            else if(this.timeSinceLastPatch >= this.timeToWaitBeforeBonusPatch && this.genPatch(random, chunkX, chunkZ, world))
            {
                this.timeSinceLastPatch = 0;
            }
            else
            {
                this.timeSinceLastPatch++;
            }
        }
    }

    private boolean genPatch(Random random, int chunkX, int chunkZ, World world)
    {
        //Coords of "top left" blocks in chunk
        int chunkBlockX = chunkX * 16;
        int chunkBlockZ = chunkZ * 16;
        //Coords of "top left" blocks in an adjacent chunk
        int nextChunkBlockX = chunkBlockX + 16;
        int nextChunkBlockZ = chunkBlockZ + 16;
        //The y coordinate where patch generation will be attempted
        int patchY;
        //The BlockPos where patch generation will be attempted
        BlockPos patchCentre;
        //Possible middle block in patch
        Block candidate;
        BlockPos.MutableBlockPos nextBlockPos = new BlockPos.MutableBlockPos(0, 0, 0);
        Block nextBlock;

        //Check 16 candidate blocks in the chunk to see if they are ice blocks
        for(int candX = chunkBlockX; candX < nextChunkBlockX; candX += 8)
        {
            for(int candZ = chunkBlockZ; candZ < nextChunkBlockZ; candZ += 8)
            {
                patchCentre = world.getTopSolidOrLiquidBlock(new BlockPos(candX, 0, candZ)).down();
                patchY = patchCentre.getY();
                candidate = world.getBlockState(patchCentre).getBlock();
                if(candidate instanceof BlockIce || FragileGlassBase.iceBlocks.contains(Item.getItemFromBlock(candidate)))
                {
                    int patchRadius = (int) (((2 * random.nextGaussian()) + FragileGlassBase.avePatchSize) / 2);
                    for (int rad = patchRadius; rad > 0; rad--)
                    {
                        for (double d = 0; d < 360; d += 10)
                        {
                            double r = Math.toRadians(d);
                            int nextX = (int) (candX + (rad * Math.cos(r)));
                            int nextZ = (int) (candZ + (rad * Math.sin(r)));
                            nextBlockPos.setPos(nextX, patchY, nextZ);
                            nextBlock = world.getBlockState(nextBlockPos).getBlock();
                            if (nextBlock instanceof BlockIce || FragileGlassBase.iceBlocks.contains(Item.getItemFromBlock(nextBlock)))
                            {
                                //Adds a little randomness to the outside of patches, to avoid perfect circles all the time
                                if (rad > patchRadius - 2)
                                {
                                    if (random.nextBoolean())
                                    {
                                        world.setBlockState(nextBlockPos, FragileGlassBase.thinIce.getDefaultState());
                                    }
                                }
                                else
                                {
                                    world.setBlockState(nextBlockPos, FragileGlassBase.thinIce.getDefaultState());
                                }
                            }
                        }
                    }
                    world.setBlockState(patchCentre, FragileGlassBase.thinIce.getDefaultState());
                    //For testing
                    //System.out.println("Generated patch at ("+candX+", "+patchY+", "+candZ+").");
                    return true;
                }
            }
        }
        return false;
    }
}
