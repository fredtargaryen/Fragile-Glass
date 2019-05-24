package com.fredtargaryen.fragileglass.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockIce;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumLightType;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.extensions.IForgeBlock;

import java.util.Random;

public class BlockThinIce extends Block implements IForgeBlock
{
    public BlockThinIce()
    {
        super(Properties.create(Material.ICE).slipperiness(0.98F).sound(SoundType.GLASS).needsRandomTick());
    }

    public int getOpacity(IBlockState state, IBlockReader worldIn, BlockPos pos) {
        return Blocks.WATER.getDefaultState().getOpacity(worldIn, pos);
    }

    @Override
    public int getItemsToDropCount(IBlockState state, int fortune, World worldIn, BlockPos pos, Random random) {
        return 0;
    }

    /**
     * Return true from this function if the player with silk touch can harvest this block directly, and not its normal drops.
     * @param player The player doing the harvesting
     * @return True if the block can be directly harvested using silk touch
     */
    @Override
    public boolean canSilkHarvest(IBlockState state, IWorldReader world, BlockPos pos, EntityPlayer player)
    {
        return false;
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void tick(IBlockState state, World worldIn, BlockPos pos, Random random) {
        if (worldIn.getLightFor(EnumLightType.BLOCK, pos) > 11 - state.getOpacity(worldIn, pos)) {
            worldIn.removeBlock(pos);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean isSideInvisible(IBlockState thisState, IBlockState neighbourState, EnumFacing face) {
        Block near = neighbourState.getBlock();
        boolean nearIsIce = near == Blocks.ICE || near == this;
        return nearIsIce || super.isSideInvisible(thisState, neighbourState, face);
    }

    @OnlyIn(Dist.CLIENT)
    public BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.TRANSLUCENT;
    }

    public BlockFaceShape getBlockFaceShape(IBlockReader access, IBlockState state, BlockPos pos, EnumFacing facing)
    {
        return BlockFaceShape.UNDEFINED;
    }

    @Deprecated
    @OnlyIn(Dist.CLIENT)
    public static boolean shouldSideBeRendered(IBlockState blockState, IBlockReader blockAccess, BlockPos pos, EnumFacing side)
    {
        if(side == EnumFacing.NORTH || side == EnumFacing.SOUTH || side == EnumFacing.EAST || side == EnumFacing.WEST)
        {
            Block b = blockAccess.getBlockState(pos.offset(side)).getBlock();
            if(b instanceof BlockThinIce || b instanceof BlockIce)
            {
                return false;
            }
        }
        return BlockIce.shouldSideBeRendered(blockState, blockAccess, pos, side);
    }
}

