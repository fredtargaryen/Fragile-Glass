package com.fredtargaryen.fragileglass.block;

import com.fredtargaryen.fragileglass.tileentity.TileEntityFragileGlass;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import java.util.Random;

public class BlockFragilePane extends BlockGlassPane
{
	public BlockFragilePane() {
		super(Properties.create(Material.GLASS).sound(SoundType.GLASS));
        this.setDefaultState(this.stateContainer.getBaseState().with(NORTH, false).with(EAST, false).with(SOUTH, false).with(WEST, false).with(WATERLOGGED, false));
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

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder)
    {
        builder.add(NORTH).add(EAST).add(SOUTH).add(WEST).add(WATERLOGGED);
    }

    ////////////////////////////////
    //METHODS FROM ANYFRAGILEBLOCK//
    ////////////////////////////////
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
    public boolean canSilkHarvest(IBlockState state, IWorldReader world, BlockPos pos, EntityPlayer player) {
        return false;
    }
}
