package com.fredtargaryen.fragileglass.block;

import com.fredtargaryen.fragileglass.FragileGlassBase;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.state.StateContainer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockStainedFragilePane extends BlockFragilePane
{
    public BlockStainedFragilePane()
    {
        super();
        this.setDefaultState(this.stateContainer.getBaseState().with(NORTH, false).with(EAST, false).with(SOUTH, false).with(WEST, false));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder)
    {
        builder.add(NORTH).add(EAST).add(WEST).add(SOUTH);
    }

    @Override
    public void onBlockAdded(IBlockState state, World worldIn, BlockPos pos, IBlockState otherState) {
        if (!worldIn.isRemote) {
            BlockBeacon.updateColorAsync(worldIn, pos);
        }
    }

    @Override
    public void onReplaced(IBlockState state, World worldIn, BlockPos pos, IBlockState newState, boolean isMoving) {
        if (!worldIn.isRemote) {
            BlockBeacon.updateColorAsync(worldIn, pos);
        }
    }

    @Override
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
}
