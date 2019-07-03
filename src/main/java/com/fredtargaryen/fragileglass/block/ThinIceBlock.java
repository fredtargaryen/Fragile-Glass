package com.fredtargaryen.fragileglass.block;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.extensions.IForgeBlock;

import java.util.Random;

public class ThinIceBlock extends Block implements IForgeBlock {
    private static final VoxelShape SHAPE = Block.makeCuboidShape(14.0, 14.0, 14.0, 16.0, 16.0, 16.0);

    public ThinIceBlock() {
        super(Properties.create(Material.ICE).slipperiness(0.98F).sound(SoundType.GLASS).tickRandomly());
    }

    public int getOpacity(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return Blocks.WATER.getDefaultState().getOpacity(worldIn, pos);
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void tick(BlockState state, World worldIn, BlockPos pos, Random random) {
        if (worldIn.getLightFor(LightType.BLOCK, pos) > 11 - state.getOpacity(worldIn, pos)) {
            worldIn.removeBlock(pos, false);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean isSideInvisible(BlockState thisState, BlockState neighbourState, Direction face) {
        Block near = neighbourState.getBlock();
        boolean nearIsIce = near == Blocks.ICE || near == this;
        return nearIsIce || super.isSideInvisible(thisState, neighbourState, face);
    }

    @OnlyIn(Dist.CLIENT)
    public BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.TRANSLUCENT;
    }


    @Deprecated
    @OnlyIn(Dist.CLIENT)
    public static boolean shouldSideBeRendered(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side)
    {
        if(side == Direction.NORTH || side == Direction.SOUTH || side == Direction.EAST || side == Direction.WEST)
        {
            Block b = blockAccess.getBlockState(pos.offset(side)).getBlock();
            if(b instanceof ThinIceBlock || b instanceof IceBlock)
            {
                return false;
            }
        }
        return IceBlock.shouldSideBeRendered(blockState, blockAccess, pos, side);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) { return SHAPE; }
}
