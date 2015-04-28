package com.fredtargaryen.fragileglass.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import java.util.List;

public class BlockStainedFragileGlass extends BlockFragileGlass
{
    private IIcon[] icons = new IIcon[16];

    @Override
    public int damageDropped (int metadata)
    {
        return metadata;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubBlocks(Item i, CreativeTabs tab, List subItems)
    {
        for (int x = 0; x < 16; x++)
        {
            subItems.add(new ItemStack(this, 1, x));
        }
    }

    /**
     * Gets the block's texture. Args: side, meta
     */
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int p_149691_1_, int meta)
    {
        return this.icons[meta];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister p_149651_1_)
    {
        for (int i = 0; i < 16; ++i)
        {
            this.icons[i] = p_149651_1_.registerIcon("minecraft:glass_" + ItemDye.field_150921_b[i]);
        }
    }

    /**
     * Returns which pass should this block be rendered on. 0 for solids and 1 for alpha
     */
    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderBlockPass()
    {
        return 1;
    }
}
