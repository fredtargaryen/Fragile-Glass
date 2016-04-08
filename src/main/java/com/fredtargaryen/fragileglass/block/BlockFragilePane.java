package com.fredtargaryen.fragileglass.block;

import com.fredtargaryen.fragileglass.FragileGlassBase;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.BlockRenderLayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPane;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;

public class BlockFragilePane extends AnyFragileBlock
{
    public static final PropertyBool NORTH = PropertyBool.create("north");
    public static final PropertyBool EAST = PropertyBool.create("east");
    public static final PropertyBool SOUTH = PropertyBool.create("south");
    public static final PropertyBool WEST = PropertyBool.create("west");
    protected static final AxisAlignedBB[] boxes = new AxisAlignedBB[] {new AxisAlignedBB(0.4375D, 0.0D, 0.4375D, 0.5625D, 1.0D, 0.5625D), new AxisAlignedBB(0.4375D, 0.0D, 0.4375D, 0.5625D, 1.0D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.4375D, 0.5625D, 1.0D, 0.5625D), new AxisAlignedBB(0.0D, 0.0D, 0.4375D, 0.5625D, 1.0D, 1.0D), new AxisAlignedBB(0.4375D, 0.0D, 0.0D, 0.5625D, 1.0D, 0.5625D), new AxisAlignedBB(0.4375D, 0.0D, 0.0D, 0.5625D, 1.0D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.5625D, 1.0D, 0.5625D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.5625D, 1.0D, 1.0D), new AxisAlignedBB(0.4375D, 0.0D, 0.4375D, 1.0D, 1.0D, 0.5625D), new AxisAlignedBB(0.4375D, 0.0D, 0.4375D, 1.0D, 1.0D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.4375D, 1.0D, 1.0D, 0.5625D), new AxisAlignedBB(0.0D, 0.0D, 0.4375D, 1.0D, 1.0D, 1.0D), new AxisAlignedBB(0.4375D, 0.0D, 0.0D, 1.0D, 1.0D, 0.5625D), new AxisAlignedBB(0.4375D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.5625D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D)};

	public BlockFragilePane()
	{
		super(false);
        this.setDefaultState(this.blockState.getBaseState().withProperty(NORTH, Boolean.valueOf(false)).withProperty(EAST, Boolean.valueOf(false)).withProperty(SOUTH, Boolean.valueOf(false)).withProperty(WEST, Boolean.valueOf(false)));
		this.setCreativeTab(CreativeTabs.tabDecorations);
	}

    /**
     * Get the actual Block state of this Block at the given position. This applies properties not visible in the
     * metadata, such as fence connections.
     */
    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        return state.withProperty(NORTH, canPaneConnectTo(worldIn, pos, EnumFacing.NORTH))
                .withProperty(SOUTH, canPaneConnectTo(worldIn, pos, EnumFacing.SOUTH))
                .withProperty(WEST, canPaneConnectTo(worldIn, pos, EnumFacing.WEST))
                .withProperty(EAST, canPaneConnectTo(worldIn, pos, EnumFacing.EAST));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    /**
     * Add all collision boxes of this Block to the list that intersect with the given mask.
     */
    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB p_185477_4_, List<AxisAlignedBB> p_185477_5_, Entity p_185477_6_)
    {
        state = this.getActualState(state, worldIn, pos);
        addCollisionBoxToList(pos, p_185477_4_, p_185477_5_, boxes[0]);

        if (((Boolean)state.getValue(NORTH)).booleanValue())
        {
            addCollisionBoxToList(pos, p_185477_4_, p_185477_5_, boxes[getBoundingBoxIndex(EnumFacing.NORTH)]);
        }

        if (((Boolean)state.getValue(SOUTH)).booleanValue())
        {
            addCollisionBoxToList(pos, p_185477_4_, p_185477_5_, boxes[getBoundingBoxIndex(EnumFacing.SOUTH)]);
        }

        if (((Boolean)state.getValue(EAST)).booleanValue())
        {
            addCollisionBoxToList(pos, p_185477_4_, p_185477_5_, boxes[getBoundingBoxIndex(EnumFacing.EAST)]);
        }

        if (((Boolean)state.getValue(WEST)).booleanValue())
        {
            addCollisionBoxToList(pos, p_185477_4_, p_185477_5_, boxes[getBoundingBoxIndex(EnumFacing.WEST)]);
        }
    }

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        state = this.getActualState(state, source, pos);
        return boxes[getBoundingBoxIndex(state)];
    }

    public boolean canPaneConnectToBlock(Block blockIn)
    {
        return blockIn.isFullBlock(blockIn.getDefaultState())
                || blockIn == this
                || blockIn == Blocks.glass
                || blockIn == FragileGlassBase.fragileGlass
                || blockIn == Blocks.stained_glass
                || blockIn == FragileGlassBase.stainedFragileGlass
                || blockIn == Blocks.stained_glass_pane
                || blockIn == FragileGlassBase.stainedFragilePane
                || blockIn instanceof BlockPane;
    }

    public boolean canPaneConnectTo(IBlockAccess world, BlockPos pos, EnumFacing dir)
    {
        BlockPos off = pos.offset(dir);
        IBlockState state = world.getBlockState(off);
        return canPaneConnectToBlock(state.getBlock()) || state.isSideSolid(world, off, dir.getOpposite());
    }
    /**
     * Convert the BlockState into the correct metadata value
     */
    @Override
    public int getMetaFromState(IBlockState state)
    {
        return 0;
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {NORTH, EAST, WEST, SOUTH});
    }

    private static int getBoundingBoxIndex(EnumFacing p_185729_0_)
    {
        return 1 << p_185729_0_.getHorizontalIndex();
    }

    private static int getBoundingBoxIndex(IBlockState p_185728_0_)
    {
        int i = 0;

        if (((Boolean)p_185728_0_.getValue(NORTH)).booleanValue())
        {
            i |= getBoundingBoxIndex(EnumFacing.NORTH);
        }

        if (((Boolean)p_185728_0_.getValue(EAST)).booleanValue())
        {
            i |= getBoundingBoxIndex(EnumFacing.EAST);
        }

        if (((Boolean)p_185728_0_.getValue(SOUTH)).booleanValue())
        {
            i |= getBoundingBoxIndex(EnumFacing.SOUTH);
        }

        if (((Boolean)p_185728_0_.getValue(WEST)).booleanValue())
        {
            i |= getBoundingBoxIndex(EnumFacing.WEST);
        }

        return i;
    }
}
