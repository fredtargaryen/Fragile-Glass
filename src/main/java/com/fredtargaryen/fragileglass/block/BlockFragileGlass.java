package com.fredtargaryen.fragileglass.block;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.world.IBlockAccess;

import java.util.Random;

public class BlockFragileGlass extends AnyFragileGlassBlock
{
	public BlockFragileGlass()
	{
		super();
		this.setCreativeTab(CreativeTabs.tabBlock);
	}
	
	public int quantityDropped(Random par1Random)
    {
        return 0;
    }

    /**
     * Returns true if the given side of this block type should be rendered, if the adjacent block is at the given
     * coordinates.  Args: blockAccess, x, y, z, side
     */
    @SideOnly(Side.CLIENT)
    @Override
    public boolean shouldSideBeRendered(IBlockAccess w, BlockPos pos, EnumFacing side)
    {
        return w.getBlockState(pos) instanceof BlockFragileGlass ? false : super.shouldSideBeRendered(w, pos, side);
    }
}