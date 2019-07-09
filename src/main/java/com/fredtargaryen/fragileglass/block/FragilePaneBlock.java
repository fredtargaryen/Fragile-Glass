package com.fredtargaryen.fragileglass.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PaneBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.state.StateContainer;

public class FragilePaneBlock extends PaneBlock {
	public FragilePaneBlock() {
		super(Block.Properties.create(Material.GLASS).sound(SoundType.GLASS));
        this.setDefaultState(this.getStateContainer().getBaseState().with(NORTH, false).with(EAST, false).with(SOUTH, false).with(WEST, false).with(WATERLOGGED, false));
	}

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(NORTH).add(EAST).add(SOUTH).add(WEST).add(WATERLOGGED);
    }
}
