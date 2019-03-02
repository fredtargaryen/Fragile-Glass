package com.fredtargaryen.fragileglass.block;

import net.minecraft.block.BlockFalling;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class SugarBlock extends BlockFalling
{
	public SugarBlock()
	{
		super(Properties.create(Material.SAND).sound(SoundType.SAND));
	}
}
