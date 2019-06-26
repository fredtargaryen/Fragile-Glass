package com.fredtargaryen.fragileglass.block;

import com.fredtargaryen.fragileglass.FragileGlassBase;
import com.fredtargaryen.fragileglass.tileentity.TileEntityFragileGlass;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FragileGlassBlock extends AnyFragileBlock
{
	public FragileGlassBlock()
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
    public boolean isSideInvisible(BlockState thisState, BlockState neighbourState, Direction face) {
	    Block near = neighbourState.getBlock();
	    boolean nearIsGlass = near == FragileGlassBase.FRAGILE_GLASS
                || near instanceof StainedFragileGlassBlock
                || near == Blocks.GLASS
                || near instanceof StainedGlassBlock;
        return nearIsGlass || super.isSideInvisible(thisState, neighbourState, face);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        try {
            return new TileEntityFragileGlass();
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public boolean propagatesSkylightDown(BlockState p_200123_1_, IBlockReader p_200123_2_, BlockPos p_200123_3_) {
        return true;
    }
}