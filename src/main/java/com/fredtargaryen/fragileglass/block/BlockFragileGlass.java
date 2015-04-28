package com.fredtargaryen.fragileglass.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
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

    @Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister i)
	{
        this.blockIcon = i.registerIcon("minecraft:glass");
	}
    
	@SideOnly(Side.CLIENT)
    @Override
    public int getRenderBlockPass()
    {
        return 0;
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
    public boolean shouldSideBeRendered(IBlockAccess w, int x, int y, int z, int side)
    {
        return w.getBlock(x, y, z) instanceof BlockFragileGlass ? false : super.shouldSideBeRendered(w, x, y, z, side);
    }
}