package com.fredtargaryen.fragileglass.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.Random;

public abstract class AnyFragileBlock extends Block implements ITileEntityProvider
{
    public AnyFragileBlock(Material m)
    {
        super(m);
        this.setSoundType(SoundType.GLASS);
    }

    @Override
    public abstract TileEntity createNewTileEntity(World world, int meta);

    @Override
    public int quantityDropped(Random par1Random)
    {
        return 0;
    }

    /**
     * Return true from this function if the player with silk touch can harvest this block directly, and not its normal drops.
     * @param player The player doing the harvesting
     * @return True if the block can be directly harvested using silk touch
     */
    @Override
    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player)
    {
        return false;
    }

    /**
     * Returns an item stack containing a single instance of the current block type. 'i' is the block's subtype/damage
     * and is ignored for blocks which do not support subtypes. Blocks which cannot be harvested should return null.
     */
    @Override
    protected ItemStack getSilkTouchDrop(IBlockState state)
    {
        return null;
    }
}
