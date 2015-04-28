package com.fredtargaryen.fragileglass.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemStainedFragilePane extends ItemBlockWithMetadata
{
    public ItemStainedFragilePane(Block b1)
    {
        super(b1, b1);
    }

    @Override
    public String getUnlocalizedName(ItemStack itemstack) {
        return this.getUnlocalizedName() + "." + ItemDye.field_150921_b[itemstack.getItemDamage()];
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIconFromDamage(int damage)
    {
        return this.field_150939_a.getIcon(2, damage);
    }
}
