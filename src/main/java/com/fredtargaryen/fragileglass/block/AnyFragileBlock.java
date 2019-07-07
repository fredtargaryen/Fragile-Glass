package com.fredtargaryen.fragileglass.block;

import ljfa.glassshards.api.GlassType;
import ljfa.glassshards.api.IShatterableGlass;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;

import java.util.Random;

@Optional.Interface(iface="ljfa.glassshards.api.IShatterableGlass", modid="glass_shards")
abstract class AnyFragileBlock extends Block implements ITileEntityProvider, IShatterableGlass
{
    AnyFragileBlock(Material m)
    {
        super(m);
        this.setSoundType(SoundType.GLASS);
    }

    @Override
    public abstract TileEntity createNewTileEntity(World worldIn, int meta);

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

    @Optional.Method(modid="glass_shards")
    @Override
    public abstract GlassType getGlassType(World world, BlockPos pos, IBlockState state);
}
