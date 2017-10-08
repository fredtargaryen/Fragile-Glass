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

import java.util.Random;

public class PatchGenIce extends PatchGen
{
    public PatchGenIce()
    {
        super(FragileGlassBase.genChanceIce, FragileGlassBase.avePatchSizeIce, FragileGlassBase.thinIce);
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
        //145 is the TerraFirmaCraft sea level - guessing other mods don't go any higher than this
        Biome b = world.getBiome(new BlockPos(chunkX, 145, chunkZ));
        if (b.getEnableSnow()) {
            //Coords of "top left" blocks in chunk
            int chunkBlockX = chunkX * 16;
            int chunkBlockZ = chunkZ * 16;
            //The BlockPos where patch generation will be attempted
            BlockPos patchCentre;
            //Possible middle block in patch
            Block candidate;
            //Check 16 candidate blocks in the chunk to see if they are ice blocks
            int candX = chunkBlockX;
            int candZ = chunkBlockZ;
            //Whether to stop trying to generate a patch here. Does not imply any patches were generated.
            boolean done = false;
            while (!done) {
                patchCentre = world.getTopSolidOrLiquidBlock(new BlockPos(candX, 0, candZ)).down();
                candidate = world.getBlockState(patchCentre).getBlock();
                if (this.isBlockValidToTransform(candidate)) {
                    done = this.attemptPatch(random, chunkX, chunkZ, patchCentre, world);
                }
                if(candX > chunkBlockX + 15) {
                    done = true;
                }
                else {
                    if (candZ > chunkBlockZ + 15) {
                        candX += 2;
                        candZ = chunkBlockZ;
                    } else {
                        candZ += 2;
                    }
                }
            }
        }
    }

    protected boolean isBlockValidToTransform(Block b)
    {
        return b instanceof BlockIce || FragileGlassBase.iceBlocks.contains(Item.getItemFromBlock(b));
    }
}
