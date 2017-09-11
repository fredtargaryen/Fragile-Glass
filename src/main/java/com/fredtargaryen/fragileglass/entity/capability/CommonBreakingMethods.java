package com.fredtargaryen.fragileglass.entity.capability;

import com.fredtargaryen.fragileglass.DataReference;
import com.fredtargaryen.fragileglass.FragileGlassBase;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.Iterator;
import java.util.List;

public class CommonBreakingMethods
{
    public static void breakBlocksInWay(Entity e, double xToUse, double yToUse, double zToUse, double distance)
    {
        AxisAlignedBB originalAABB = e.getEntityBoundingBox();
        AxisAlignedBB aabb = originalAABB;
        double xComp = xToUse / distance;
        double yComp = yToUse / distance;
        double zComp = zToUse / distance;
        while (distance > 1.0)
        {
            //The end of the movement vector is more than one block away from the current
            //entity bounding box, so at the end of the tick it will have passed through
            //at least one whole block. Offset the entity bounding box by a distance of
            //1m (the length of a block), and check that it intersects with any fragile
            //block bounding boxes.
            aabb = aabb.offset(xComp, yComp, zComp);
            distance -= 1.0;
            if (breakNearbyFragileBlocks(e, aabb)) return;
        }
        //The end of the movement vector is now less than one block away from the current
        //entity bounding box. Offset the entity bounding box right to the end of the
        //movement vector, and check that it intersects with the block bounding box.
        aabb = originalAABB.offset(xToUse, yToUse, zToUse);
        breakNearbyFragileBlocks(e, aabb);
    }

    /**
     * @param e
     * @param aabb
     * @return Whether the crasher was blocked from passing through
     */
    private static boolean breakNearbyFragileBlocks(Entity e, AxisAlignedBB aabb)
    {
        BlockPos blockPos;
        Block block;
        for (double x = Math.floor(aabb.minX); x < Math.ceil(aabb.maxX); ++x)
        {
            for (double y = Math.floor(aabb.minY); y < Math.ceil(aabb.maxY); ++y)
            {
                for (double z = Math.floor(aabb.minZ); z < Math.ceil(aabb.maxZ); ++z)
                {
                    blockPos = new BlockPos(x, y, z);
                    IBlockState state = e.world.getBlockState(blockPos);
                    block = state.getBlock();
                    //Chances are the block will be an air block (pass through no question) so best check this first
                    if (block != Blocks.AIR)
                    {
                        if (block.hasTileEntity(state))
                        {
                            TileEntity te = e.world.getTileEntity(blockPos);
                            if (te.hasCapability(FragileGlassBase.FRAGILECAP, null))
                            {
                                te.getCapability(FragileGlassBase.FRAGILECAP, null).onCrash(te);
                            }
                            else
                            {
                                if (block.isNormalCube(state, e.world, blockPos))
                                {
                                    return true;
                                }
                            }
                        }
                        else
                        {
                            if (block.isNormalCube(state, e.world, blockPos))
                            {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean isValidMoveSpeedSquared(double blocksPerTick)
    {
        return blocksPerTick <= DataReference.MAXIMUM_ENTITY_SPEED_SQUARED;
    }
}
