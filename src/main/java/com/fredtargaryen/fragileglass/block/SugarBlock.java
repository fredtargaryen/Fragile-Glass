package com.fredtargaryen.fragileglass.block;

import net.minecraft.block.FallingBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class SugarBlock extends FallingBlock
{
	public SugarBlock()
	{
		super(Properties.create(Material.SAND).sound(SoundType.SAND));
	}
}
