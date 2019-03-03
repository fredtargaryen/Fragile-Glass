package com.fredtargaryen.fragileglass.block;

import com.fredtargaryen.fragileglass.FragileGlassBase;
import com.fredtargaryen.fragileglass.tileentity.TileEntityFragileGlass;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockFragilePane extends AnyFragileBlock
{
    static final BooleanProperty NORTH = BooleanProperty.create("north");
    static final BooleanProperty EAST = BooleanProperty.create("east");
    static final BooleanProperty SOUTH = BooleanProperty.create("south");
    static final BooleanProperty WEST = BooleanProperty.create("west");
    private static final AxisAlignedBB[] AABB_BY_INDEX = new AxisAlignedBB[] {new AxisAlignedBB(0.4375D, 0.0D, 0.4375D, 0.5625D, 1.0D, 0.5625D), new AxisAlignedBB(0.4375D, 0.0D, 0.4375D, 0.5625D, 1.0D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.4375D, 0.5625D, 1.0D, 0.5625D), new AxisAlignedBB(0.0D, 0.0D, 0.4375D, 0.5625D, 1.0D, 1.0D), new AxisAlignedBB(0.4375D, 0.0D, 0.0D, 0.5625D, 1.0D, 0.5625D), new AxisAlignedBB(0.4375D, 0.0D, 0.0D, 0.5625D, 1.0D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.5625D, 1.0D, 0.5625D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.5625D, 1.0D, 1.0D), new AxisAlignedBB(0.4375D, 0.0D, 0.4375D, 1.0D, 1.0D, 0.5625D), new AxisAlignedBB(0.4375D, 0.0D, 0.4375D, 1.0D, 1.0D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.4375D, 1.0D, 1.0D, 0.5625D), new AxisAlignedBB(0.0D, 0.0D, 0.4375D, 1.0D, 1.0D, 1.0D), new AxisAlignedBB(0.4375D, 0.0D, 0.0D, 1.0D, 1.0D, 0.5625D), new AxisAlignedBB(0.4375D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.5625D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D)};

	public BlockFragilePane() {
		super(Properties.create(Material.GLASS).sound(SoundType.GLASS));
        this.setDefaultState(this.stateContainer.getBaseState().with(NORTH, false).with(EAST, false).with(SOUTH, false).with(WEST, false));
	}

    @OnlyIn(Dist.CLIENT)
    @Override
    public BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    public boolean canPaneConnectToBlock(Block blockIn)
    {
        return blockIn.isFullCube(blockIn.getDefaultState()) ||
                blockIn == this
                || blockIn == Blocks.GLASS
                || blockIn == FragileGlassBase.FRAGILE_GLASS
                || blockIn == FragileGlassBase.FRAGILE_PANE
                || blockIn instanceof BlockStainedGlass
                || blockIn instanceof BlockStainedGlassPane
                || blockIn instanceof BlockStainedFragileGlass
                || blockIn instanceof BlockStainedFragilePane
                || blockIn instanceof BlockPane;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder)
    {
        builder.add(NORTH).add(EAST).add(SOUTH).add(WEST);
    }

    //////////////////////////
    //METHODS FROM BLOCKPANE//
    //////////////////////////
    @Override
    @Deprecated
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    /**
     * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     */
    public IBlockState withRotation(IBlockState state, Rotation rot)
    {
        switch (rot)
        {
            case CLOCKWISE_180:
                return state.with(NORTH, state.get(SOUTH)).with(EAST, state.get(WEST)).with(SOUTH, state.get(NORTH)).with(WEST, state.get(EAST));
            case COUNTERCLOCKWISE_90:
                return state.with(NORTH, state.get(EAST)).with(EAST, state.get(SOUTH)).with(SOUTH, state.get(WEST)).with(WEST, state.get(NORTH));
            case CLOCKWISE_90:
                return state.with(NORTH, state.get(WEST)).with(EAST, state.get(NORTH)).with(SOUTH, state.get(EAST)).with(WEST, state.get(SOUTH));
            default:
                return state;
        }
    }

    /**
     * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     */
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn)
    {
        switch (mirrorIn)
        {
            case LEFT_RIGHT:
                return state.with(NORTH, state.get(SOUTH)).with(SOUTH, state.get(NORTH));
            case FRONT_BACK:
                return state.with(EAST, state.get(WEST)).with(WEST, state.get(EAST));
            default:
                return super.mirror(state, mirrorIn);
        }
    }

    private static int getBoundingBoxIndex(EnumFacing p_185729_0_)
    {
        return 1 << p_185729_0_.getHorizontalIndex();
    }

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockReader source, BlockPos pos)
    {
        state = this.getActualState(state, source, pos);
        return AABB_BY_INDEX[getBoundingBoxIndex(state)];
    }

    private static int getBoundingBoxIndex(IBlockState state)
    {
        int i = 0;

        if (state.get(NORTH))
        {
            i |= getBoundingBoxIndex(EnumFacing.NORTH);
        }

        if (state.get(EAST))
        {
            i |= getBoundingBoxIndex(EnumFacing.EAST);
        }

        if (state.get(SOUTH))
        {
            i |= getBoundingBoxIndex(EnumFacing.SOUTH);
        }

        if (state.get(WEST))
        {
            i |= getBoundingBoxIndex(EnumFacing.WEST);
        }

        return i;
    }

    /**
     * Get the actual Block state of this Block at the given position. This applies properties not visible in the
     * metadata, such as fence connections.
     */
    public IBlockState getActualState(IBlockState state, IBlockReader worldIn, BlockPos pos)
    {
        Block b = worldIn.getBlockState(pos).getBlock();
        return state.with(NORTH, canPaneConnectToBlock(b))
                .with(SOUTH, canPaneConnectToBlock(b))
                .with(WEST, canPaneConnectToBlock(b))
                .with(EAST, canPaneConnectToBlock(b));
    }

    @Override
    public TileEntity createTileEntity(IBlockState state, IBlockReader world) {
        try {
            return new TileEntityFragileGlass();
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
