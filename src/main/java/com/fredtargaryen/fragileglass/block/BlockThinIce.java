package com.fredtargaryen.fragileglass.block;

import com.fredtargaryen.fragileglass.FragileGlassBase;
import com.fredtargaryen.fragileglass.tileentity.TileEntityThinIce;
import net.minecraft.block.Block;
import net.minecraft.block.BlockIce;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
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
        this.slipperiness = 0.98F;
        this.setCreativeTab(CreativeTabs.MISC);
        this.setTickRandomly(true);
    }

    public TileEntity createNewTileEntity(World w, int i)
    {
        try {
            return new TileEntityThinIce();
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
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

    @Deprecated
    @SideOnly(Side.CLIENT)
    @Override
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

    @Deprecated
    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }
}

