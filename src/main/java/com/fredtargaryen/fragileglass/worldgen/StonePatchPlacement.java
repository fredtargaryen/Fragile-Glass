package com.fredtargaryen.fragileglass.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.WorldDecoratingHelper;
import net.minecraft.world.gen.placement.ChanceConfig;
import net.minecraft.world.gen.placement.Placement;

import java.util.Random;
import java.util.stream.Stream;

public class StonePatchPlacement extends Placement<ChanceConfig> {
    private int patchCount;

    public StonePatchPlacement(Codec<ChanceConfig> func) {
        super(func);
        this.patchCount = 0;
    }

    @Override
    public Stream<BlockPos> getPositions(WorldDecoratingHelper helper, Random random, ChanceConfig config, BlockPos blockPos) {
        Stream.Builder<BlockPos> streamBuilder = Stream.builder();
        //Coords of "top left" blocks in chunk
        int cornerX = blockPos.getX();
        int cornerZ = blockPos.getZ();
        //Possible middle block in patch
        BlockState candidate;
        boolean previousBlockSolid = true;
        //The BlockPos where patch generation will be attempted
        BlockPos.Mutable patchCentre = new BlockPos.Mutable(cornerX + random.nextInt(16), 0, cornerZ + random.nextInt(16));
        while(patchCentre.getY() < 256f) {
            candidate = helper.func_242894_a(patchCentre);
            Material mat = candidate.getMaterial();
            if (mat.isSolid()) {
                if (!previousBlockSolid) {
                    // We've found a "ceiling" i.e. a solid block above a non-solid block
                    previousBlockSolid = true;
                    if (mat == Material.ROCK) {
                        //It's a rocky ceiling, so make the patch if the RNG's good
                        if(this.patchCount == config.chance)
                        {
                            // 1 in x chance, we skipped x patches, so generate one here. This forces a minimum 1 in x rate of patches appearing
                            this.patchCount = 0;
                            streamBuilder.add(patchCentre.toImmutable());
                        }
                        else
                        {
                            if(random.nextInt(config.chance) == 0)
                            {
                                // Reset the patch count and generate the patch
                                this.patchCount = 0;
                                streamBuilder.add(patchCentre.toImmutable());
                            }
                            else
                            {
                                // Don't generate the patch, but increment the patch count
                                this.patchCount++;
                            }
                        }
                    }
                }
            } else {
                if (previousBlockSolid) {
                    previousBlockSolid = false;
                }
            }
            patchCentre.setY(patchCentre.getY() + 1);
        }
        return streamBuilder.build();
    }
}
