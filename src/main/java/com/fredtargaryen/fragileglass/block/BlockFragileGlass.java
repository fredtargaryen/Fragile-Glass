package com.fredtargaryen.fragileglass.block;

import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;

public class BlockFragileGlass extends AnyFragileBlock
{
	public BlockFragileGlass()
	{
		super(false);
		this.setCreativeTab(CreativeTabs.tabBlock);
	}

    @SideOnly(Side.CLIENT)
    @Override
    public EnumWorldBlockLayer getBlockLayer()
    {
        return EnumWorldBlockLayer.CUTOUT_MIPPED;
    }
}