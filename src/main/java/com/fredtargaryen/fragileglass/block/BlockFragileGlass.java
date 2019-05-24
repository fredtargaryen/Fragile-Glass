package com.fredtargaryen.fragileglass.block;

import com.fredtargaryen.fragileglass.FragileGlassBase;
import com.fredtargaryen.fragileglass.tileentity.TileEntityFragileGlass;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockFragileGlass extends AnyFragileBlock
{
	public BlockFragileGlass()
	{
		super(Properties.create(Material.GLASS)
        .sound(SoundType.GLASS));
	}

    //////////////////////
    //OVERRIDDEN METHODS//
    //////////////////////
    @OnlyIn(Dist.CLIENT)
    @Override
    public BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean isSideInvisible(IBlockState thisState, IBlockState neighbourState, EnumFacing face) {
	    Block near = neighbourState.getBlock();
	    boolean nearIsGlass = near == FragileGlassBase.FRAGILE_GLASS
                || near instanceof BlockStainedFragileGlass
                || near == Blocks.GLASS
                || near instanceof BlockStainedGlass;
        return nearIsGlass || super.isSideInvisible(thisState, neighbourState, face);
    }

    @Override
    @Deprecated
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(IBlockState state, IBlockReader world) {
        try {
            return new TileEntityFragileGlass();
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public boolean propagatesSkylightDown(IBlockState p_200123_1_, IBlockReader p_200123_2_, BlockPos p_200123_3_) {
        return true;
    }
}