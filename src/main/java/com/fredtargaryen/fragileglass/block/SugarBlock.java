package com.fredtargaryen.fragileglass.block;

import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class SugarBlock extends BlockFalling
{
	public SugarBlock()
	{
		super(Material.sand);
		this.setCreativeTab(CreativeTabs.tabMisc);
	}
}
