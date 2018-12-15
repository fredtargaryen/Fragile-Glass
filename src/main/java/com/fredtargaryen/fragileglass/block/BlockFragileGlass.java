package com.fredtargaryen.fragileglass.block;

import com.fredtargaryen.fragileglass.FragileGlassBase;
import com.fredtargaryen.fragileglass.tileentity.TileEntityFragileGlass;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockFragileGlass extends AnyFragileBlock
{
	public BlockFragileGlass()
	{
		super(Material.GLASS);
		this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
        this.setSoundType(SoundType.GLASS);
	}

    //////////////////////
    //OVERRIDDEN METHODS//
    //////////////////////
    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face)
    {
        Block near = world.getBlockState(pos.offset(face)).getBlock();
        return near == FragileGlassBase.fragileGlass || near == FragileGlassBase.fragilePane
                || near == FragileGlassBase.stainedFragileGlass || near == FragileGlassBase.stainedFragilePane
                || near == Blocks.GLASS || near == Blocks.GLASS_PANE
                || near == Blocks.STAINED_GLASS || near == Blocks.STAINED_GLASS_PANE;
    }

    @Override
    @Deprecated
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    ///////////////////////////////
    //METHODS FROM BLOCKBREAKABLE//
    ///////////////////////////////
    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    @Override
    @Deprecated
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        try {
            return new TileEntityFragileGlass();
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}