package com.fredtargaryen.fragileglass.block;

import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockStainedFragilePane extends BlockFragilePane
{
    @OnlyIn(Dist.CLIENT)
    @Override
    public BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public void onBlockAdded(IBlockState state, World worldIn, BlockPos pos, IBlockState otherState) {
        if (!worldIn.isRemote) {
            BlockBeacon.updateColorAsync(worldIn, pos);
        }
    }

    @Override
    public void onReplaced(IBlockState state, World worldIn, BlockPos pos, IBlockState newState, boolean isMoving) {
        if (!worldIn.isRemote) {
            BlockBeacon.updateColorAsync(worldIn, pos);
        }
    }
}
