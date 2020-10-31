package com.fredtargaryen.fragileglass.worldgen;

import com.fredtargaryen.fragileglass.FragileGlassBase;
import com.mojang.serialization.Codec;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.WorldDecoratingHelper;
import net.minecraft.world.gen.placement.ChanceConfig;
import net.minecraft.world.gen.placement.Placement;

import java.util.Random;
import java.util.stream.Stream;


public class IcePatchPlacement extends Placement<ChanceConfig> {
    private static final ResourceLocation ICE_TAG = new ResourceLocation("minecraft", "ice");

    private int patchCount;

    public IcePatchPlacement(Codec<ChanceConfig> codec) {
        super(codec);
        this.patchCount = 0;
    }

    @Override
    public Stream<BlockPos> getPositions(WorldDecoratingHelper helper, Random random, ChanceConfig config, BlockPos blockPos) {
        if(FragileGlassBase.ICE_BLOCKS == null)
            FragileGlassBase.ICE_BLOCKS = BlockTags.getCollection().get(ICE_TAG); // Hopefully replaces GetOrCreate
        BlockPos pos = this.searchForTransformableBlock(helper, random, blockPos, (byte) 16);
        if(pos != null)
        {
            // Found a good block. Now check the RNG
            if(this.patchCount == config.chance)
            {
                // 1 in x chance, we skipped x patches, so generate one here. This forces a minimum 1 in x rate of patches appearing
                this.patchCount = 0;
                return Stream.of(pos);
            }
            else
            {
                if(random.nextInt(config.chance) == 0)
                {
                    // Reset the patch count and generate the patch
                    this.patchCount = 0;
                    return Stream.of(pos);
                }
                else
                {
                    // Don't generate the patch, but increment the patch count
                    this.patchCount++;
                }
            }
        }
        // Didn't find a good block in the chunk, or did, but the RNG failed
        return Stream.empty();
    }

    /**
     * Search the middle 3x3 blocks of the chunk for a transformable block. Any more and you risk going outside the chunk later and having problems
     * @param helper access to the heightmap, for getting the right y position
     * @param random
     * @param cornerPos The BlockPos of the corner of the chunk with the minimum coordinates
     * @param attempts the number of attempts to pick a random surface block and check if it's icy
     * @return
     */
    private BlockPos searchForTransformableBlock(WorldDecoratingHelper helper, Random random, BlockPos cornerPos, byte attempts) {
        int cornerX = cornerPos.getX();
        int cornerZ = cornerPos.getZ();
        for(byte b = 0; b < attempts; b++) {
            int surfaceX = cornerX + random.nextInt(16);
            int surfaceZ = cornerZ + random.nextInt(16);
            int surfaceY = helper.func_242893_a(Heightmap.Type.WORLD_SURFACE, cornerX, cornerZ) - 1; // Hopefully replaces getHeight or whatever it was called
            BlockPos checkPos = new BlockPos(surfaceX, surfaceY, surfaceZ);
            if (FragileGlassBase.ICE_BLOCKS.contains(helper.func_242894_a(checkPos).getBlock())) {
                return checkPos;
            }
        }
        return null;
    }
}
