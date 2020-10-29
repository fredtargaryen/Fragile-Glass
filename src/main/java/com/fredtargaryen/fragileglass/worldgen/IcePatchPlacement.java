package com.fredtargaryen.fragileglass.worldgen;

import com.fredtargaryen.fragileglass.FragileGlassBase;
import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
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

    public IcePatchPlacement(Codec<ChanceConfig> codec) { super(codec); }

    @Override
    public Stream<BlockPos> func_241857_a(WorldDecoratingHelper helper, Random random, ChanceConfig config, BlockPos blockPos) {
        if(random.nextInt(config.chance) == 0) {
            if(FragileGlassBase.ICE_BLOCKS == null)
                FragileGlassBase.ICE_BLOCKS = BlockTags.getCollection().func_241834_b(ICE_TAG); // Hopefully replaces GetOrCreate
            int surfaceY = helper.func_242893_a(Heightmap.Type.WORLD_SURFACE, blockPos.getX(), blockPos.getZ()) - 1; // Hopefully replaces getHeight or whatever it was called
            return Stream.of(new BlockPos(blockPos.getX(), surfaceY, blockPos.getZ()));
        }
        return Stream.empty();
    }

    private boolean isBlockValidToTransform(Block block) {
        return block == Blocks.WATER || FragileGlassBase.ICE_BLOCKS.func_230235_a_(block); // Hopefully replaces contains
    }
}
