package com.fredtargaryen.fragileglass.block;

import com.fredtargaryen.fragileglass.FragileGlassBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBeacon;
import net.minecraft.block.BlockPane;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockStainedFragilePane extends BlockFragilePane
{
    private static final PropertyEnum COLOR = PropertyEnum.create("color", EnumDyeColor.class);

    public BlockStainedFragilePane()
    {
        super();
        this.setDefaultState(this.blockState.getBaseState().withProperty(NORTH, false).withProperty(EAST, false).withProperty(SOUTH, false).withProperty(WEST, false).withProperty(COLOR, EnumDyeColor.WHITE));
    }

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    @SideOnly(Side.CLIENT)
    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list)
    {
        for (int i = 0; i < EnumDyeColor.values().length; ++i)
        {
            list.add(new ItemStack(this, 1, i));
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.TRANSLUCENT;
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    @Override
    public int getMetaFromState(IBlockState state)
    {
        return ((EnumDyeColor)state.getValue(COLOR)).getMetadata();
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, NORTH, EAST, WEST, SOUTH, COLOR);
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        if (!worldIn.isRemote)
        {
            BlockBeacon.updateColorAsync(worldIn, pos);
        }
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        if (!worldIn.isRemote)
        {
            BlockBeacon.updateColorAsync(worldIn, pos);
        }
    }

    @Override
    public boolean canPaneConnectToBlock(Block blockIn)
    {
        return blockIn.isFullBlock(blockIn.getDefaultState()) ||
                blockIn == this
                || blockIn == Blocks.GLASS
                || blockIn == FragileGlassBase.fragileGlass
                || blockIn == FragileGlassBase.fragilePane
                || blockIn == Blocks.STAINED_GLASS
                || blockIn == FragileGlassBase.stainedFragileGlass
                || blockIn == Blocks.STAINED_GLASS_PANE
                || blockIn instanceof BlockPane;
    }

    //////////////////////////////////
    //METHODS FROM BLOCKSTAINEDGLASS//
    //////////////////////////////////
    /**
     * Convert the given metadata into a BlockState for this Block
     */
    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(COLOR, EnumDyeColor.byMetadata(meta));
    }
}
