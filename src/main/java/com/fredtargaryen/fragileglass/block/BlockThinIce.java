package com.fredtargaryen.fragileglass.block;

import com.fredtargaryen.fragileglass.FragileGlassBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockIce;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BlockThinIce extends BlockIce
{
    public BlockThinIce()
    {
        super();
        this.lightOpacity = 0;
        this.slipperiness = 0.98F;
        this.setCreativeTab(CreativeTabs.MISC);
        this.setTickRandomly(true);
        this.setSoundType(SoundType.GLASS);
    }

    @Override
    public int quantityDropped(Random par1Random)
    {
        return 0;
    }

    /**
     * Return true from this function if the player with silk touch can harvest this block directly, and not its normal drops.
     * @param player The player doing the harvesting
     * @return True if the block can be directly harvested using silk touch
     */
    @Override
    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player)
    {
        return false;
    }

    /**
     * Ticks the block if it's been scheduled
     */
    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (worldIn.getLightFor(EnumSkyBlock.BLOCK, pos) > 11 - this.getLightOpacity(state, worldIn, pos))
        {
            worldIn.setBlockToAir(pos);
        }
    }

    @Override
    public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face)
    {
        if(face == EnumFacing.DOWN)
        {
            return false;
        }
        else if(face == EnumFacing.UP)
        {
            return world.getBlockState(pos.offset(face)).getBlock() == Blocks.ICE;
        }
        else
        {
            Block near = world.getBlockState(pos.offset(face)).getBlock();
            return near == FragileGlassBase.thinIce || near == Blocks.ICE;
        }
    }

    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.TRANSLUCENT;
    }

    public BlockFaceShape getBlockFaceShape(IBlockAccess access, IBlockState state, BlockPos pos, EnumFacing facing)
    {
        return BlockFaceShape.UNDEFINED;
    }

    @Deprecated
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        if(side == EnumFacing.NORTH || side == EnumFacing.SOUTH || side == EnumFacing.EAST || side == EnumFacing.WEST)
        {
            Block b = blockAccess.getBlockState(pos.offset(side)).getBlock();
            if(b == this || b instanceof BlockIce)
            {
                return false;
            }
        }
        return super.shouldSideBeRendered(blockState, blockAccess, pos, side);
    }
}

