package com.fredtargaryen.fragileglass.block;

import com.fredtargaryen.fragileglass.FragileGlassBase;
import com.fredtargaryen.fragileglass.tileentity.TileEntityFragile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockIce;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BlockThinIce extends AnyFragileBlock
{
    private static final AxisAlignedBB thinBox = new AxisAlignedBB(0.0F, 0.875F, 0.0F, 1.0F, 1.0F, 1.0F);

    public BlockThinIce()
    {
        super(Material.ICE);
        this.lightOpacity = 0;
    }

    public TileEntity createNewTileEntity(World w, int i)
    {
        return new TileEntityFragile();
    }

    /**
     * Ticks the block if it's been scheduled
     */
    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (worldIn.getLightFor(EnumSkyBlock.BLOCK, pos) > 11 - this.getLightOpacity(state, worldIn, pos))
        {
            if (worldIn.provider.doesWaterVaporize())
            {
                worldIn.setBlockToAir(pos);
            }
        }
    }

    /**
     * Returns true if the given side of this block should be rendered, if the adjacent block is at the given
     * coordinates.
     */
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side)
    {
        if(side == EnumFacing.DOWN)
        {
            return true;
        }
        else if(side == EnumFacing.UP)
        {
            Block blockAtPos = worldIn.getBlockState(pos).getBlock();
            return !(blockAtPos == Blocks.ICE || blockAtPos == Blocks.FROSTED_ICE || blockAtPos == Blocks.PACKED_ICE);
        }
        else
        {
            Block blockAtPos = worldIn.getBlockState(pos).getBlock();
            return !(blockAtPos == Blocks.ICE || blockAtPos == FragileGlassBase.thinIce || blockAtPos == Blocks.FROSTED_ICE
            || blockAtPos == Blocks.PACKED_ICE);
        }
    }

    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side)
    {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.TRANSLUCENT;
    }
}

