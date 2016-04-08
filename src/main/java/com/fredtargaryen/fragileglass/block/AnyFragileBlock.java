package com.fredtargaryen.fragileglass.block;

import com.fredtargaryen.fragileglass.FragileGlassBase;
import com.fredtargaryen.fragileglass.tileentity.TileEntityFragile;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.Random;

public abstract class AnyFragileBlock extends Block implements ITileEntityProvider
{
    public AnyFragileBlock(boolean i)
    {
        super(Material.glass);
        this.ignoreSimilarity = i;
        this.setStepSound(SoundType.GLASS);
    }

    public boolean ignoreSimilarity;

    @Override
    public TileEntity createNewTileEntity(World par1World, int par2)
    {
        try
        {
            return new TileEntityFragile();
        }
        catch (Exception exception)
        {
            throw new RuntimeException(exception);
        }
    }

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
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    @SideOnly(Side.CLIENT)
    @Override
    public boolean isOpaqueCube(IBlockState state){return false;}

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess worldIn, BlockPos pos, EnumFacing side)
    {
        IBlockState iblockstate = worldIn.getBlockState(pos);
        System.out.println(iblockstate == state);
        Block block = iblockstate.getBlock();

        if (this == FragileGlassBase.fragileGlass || this == FragileGlassBase.stainedFragileGlass)
        {
            if (worldIn.getBlockState(pos.offset(side.getOpposite())) != iblockstate)
            {
                return true;
            }

            if (block == this)
            {
                return false;
            }
        }

        return !(!this.ignoreSimilarity && block == this) && super.shouldSideBeRendered(iblockstate, worldIn, pos, side);
    }

    /**
     * Returns an item stack containing a single instance of the current block type. 'i' is the block's subtype/damage
     * and is ignored for blocks which do not support subtypes. Blocks which cannot be harvested should return null.
     */
    @Override
    protected ItemStack createStackedBlock(IBlockState state)
    {
        return null;
    }
}
