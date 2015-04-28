package com.fredtargaryen.fragileglass.block;

import com.fredtargaryen.fragileglass.tileentity.TileEntityGlass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public abstract class AnyFragileGlassBlock extends Block implements ITileEntityProvider
{
    public AnyFragileGlassBlock()
    {
        super(Material.glass);
    }

    @Override
    public TileEntity createNewTileEntity(World par1World, int par2)
    {
        try
        {
            return new TileEntityGlass();
        }
        catch (Exception exception)
        {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Return true from this function if the player with silk touch can harvest this block directly, and not its normal drops.
     * @param player The player doing the harvesting
     * @return True if the block can be directly harvested using silk touch
     */
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
    public boolean isOpaqueCube(){return false;}

    public boolean isFullCube()
    {
        return false;
    }
}
