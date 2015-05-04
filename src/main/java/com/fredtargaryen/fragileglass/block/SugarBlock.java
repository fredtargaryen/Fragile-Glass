package com.fredtargaryen.fragileglass.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class SugarBlock extends Block
{
	public SugarBlock()
	{
		super(Material.wood);
		setCreativeTab(CreativeTabs.tabBlock);
	}
}
