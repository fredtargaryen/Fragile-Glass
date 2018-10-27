package com.fredtargaryen.fragileglass.block;

import com.fredtargaryen.fragileglass.tileentity.TileEntityWeakStone;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockWeakStone extends BlockFalling implements ITileEntityProvider {
    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        try
        {
            return new TileEntityWeakStone();
        }
        catch (Exception exception)
        {
            throw new RuntimeException(exception);
        }
    }

    public BlockWeakStone()
    {
        super();
    }

    /**
     * Called after the block is set in the Chunk data, but before the Tile Entity is set
     */
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
    }

    /**
     * Called when a neighboring block was changed and marks that this state should perform any checks during a neighbor
     * change. Cases may include when redstone power is updated, cactus blocks popping off due to a neighboring solid
     * block, etc.
     */
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
    }

    /**
     * How many world ticks before ticking
     */
    public int tickRate(World worldIn)
    {
        return 20;
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
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        drops.add(new ItemStack(Item.getItemFromBlock(Blocks.GRAVEL), 1, 0));
    }
}
