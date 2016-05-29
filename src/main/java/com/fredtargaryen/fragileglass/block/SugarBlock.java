package com.fredtargaryen.fragileglass.block;

import net.minecraft.block.BlockFalling;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class SugarBlock extends BlockFalling
{
	public SugarBlock()
	{
		super(Material.SAND);
		this.setCreativeTab(CreativeTabs.FOOD);
		this.setSoundType(SoundType.SAND);
	}
}
