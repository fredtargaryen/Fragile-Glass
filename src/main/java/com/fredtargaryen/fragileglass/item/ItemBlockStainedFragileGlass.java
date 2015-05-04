package com.fredtargaryen.fragileglass.item;

import net.minecraft.block.Block;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockStainedFragileGlass extends ItemBlock
{
    public ItemBlockStainedFragileGlass(Block b)
    {
        super(b);
    }

    /**
     * Returns the unlocalized name of this item. This version accepts an ItemStack so different stacks can have
     * different names based on their damage or NBT.
     */
    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        return super.getUnlocalizedName() + "." + EnumDyeColor.byMetadata(stack.getMetadata()).getUnlocalizedName();
    }

    @Override
    public int getMetadata(int damage)
    {
        return damage;
    }
}
