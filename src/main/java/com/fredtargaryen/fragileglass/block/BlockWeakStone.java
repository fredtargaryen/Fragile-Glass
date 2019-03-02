package com.fredtargaryen.fragileglass.block;

import com.fredtargaryen.fragileglass.tileentity.TileEntityWeakStone;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockWeakStone extends BlockFalling {
    public BlockWeakStone()
    {
        super(Properties.create(Material.ROCK));
    }

    @Override
    public TileEntity createTileEntity(IBlockState state, IBlockReader world) {
        try {
            return new TileEntityWeakStone();
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onBlockAdded(IBlockState state, World worldIn, BlockPos pos, IBlockState otherState) {}

    /**
     * Called when a neighboring block was changed and marks that this state should perform any checks during a neighbor
     * change. Cases may include when redstone power is updated, cactus blocks popping off due to a neighboring solid
     * block, etc.
     */
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
    }

    public void onEndFalling(World worldIn, BlockPos pos, IBlockState p_176502_3_, IBlockState p_176502_4_)
    {
        worldIn.setBlockState(pos, Blocks.GRAVEL.getDefaultState());
    }

    /**
     * This gets a complete list of items dropped from this block.
     *
     * @param drops add all items this block drops to this drops list
     * @param world The current world
     * @param pos Block position in world
     * @param state Current state
     * @param fortune Breakers fortune level
     */
    public void getDrops(IBlockState state, NonNullList<ItemStack> drops, World world, BlockPos pos, int fortune)
    {
        drops.add(new ItemStack(Item.getItemFromBlock(Blocks.GRAVEL), 1, new NBTTagCompound()));
    }
}
