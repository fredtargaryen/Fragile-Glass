package com.fredtargaryen.fragileglass.block;

import net.minecraft.block.SoundType;
import net.minecraft.util.BlockRenderLayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;

public class BlockFragileGlass extends AnyFragileBlock
{
	public BlockFragileGlass()
	{
		super(false);
		this.setCreativeTab(CreativeTabs.tabBlock);
        this.setStepSound(SoundType.GLASS);
	}

    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }
}