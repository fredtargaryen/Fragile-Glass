package com.fredtargaryen.fragileglass.entity.capability;

import com.fredtargaryen.fragileglass.DataReference;
import com.fredtargaryen.fragileglass.FragileGlassBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.concurrent.Callable;

public class ServerCanBreakFactory implements Callable<IServerCanBreakCapability>
{
    @Override
    public IServerCanBreakCapability call() throws Exception {
        return new ServerCanBreakImpl();
    }

    public class ServerCanBreakImpl implements IServerCanBreakCapability
    {
        private Entity e;

        @SubscribeEvent(priority= EventPriority.HIGHEST)
        public void serverBreakCheck(TickEvent.WorldTickEvent event) {
            if (event.phase == TickEvent.Phase.START) {
                //The entity is controlled from server side (like most if not all non-player entities). The entity can
                //conveniently have their movement checked with motionX, motionY and motionZ.
                double distance = Math.sqrt(this.e.motionX * this.e.motionX + this.e.motionY * this.e.motionY + this.e.motionZ * this.e.motionZ);
                if (distance >= DataReference.MINIMUM_ENTITY_SPEED) {
                    AxisAlignedBB originalAABB = this.e.getEntityBoundingBox();
                    AxisAlignedBB aabb = originalAABB;
                    double xComp = this.e.motionX / distance;
                    double yComp = this.e.motionY / distance;
                    double zComp = this.e.motionZ / distance;
                    while (distance > 1.0) {
                        //The end of the movement vector is more than one block away from the current
                        //entity bounding box, so at the end of the tick it will have passed through
                        //at least one whole block. Offset the entity bounding box by a distance of
                        //1m (the length of a block), and check that it intersects with any fragile
                        //block bounding boxes.
                        aabb = aabb.offset(xComp, yComp, zComp);
                        distance -= 1.0;
                        this.breakNearbyFragileBlocks(aabb);
                    }
                    //The end of the movement vector is now less than one block away from the current
                    //entity bounding box. Offset the entity bounding box right to the end of the
                    //movement vector, and check that it intersects with the block bounding box.
                    aabb = originalAABB.offset(this.e.motionX, this.e.motionY, this.e.motionZ);
                    this.breakNearbyFragileBlocks(aabb);
                }
                if(this.e.isDead)
                {
                    MinecraftForge.EVENT_BUS.unregister(this);
                }
            }
        }

        private void breakNearbyFragileBlocks(AxisAlignedBB aabb)
        {
            BlockPos blockPos;
            for (double x = Math.floor(aabb.minX); x < Math.ceil(aabb.maxX); ++x) {
                for (double y = Math.floor(aabb.minY); y < Math.ceil(aabb.maxY); ++y) {
                    for (double z = Math.floor(aabb.minZ); z < Math.ceil(aabb.maxZ); ++z) {
                        blockPos = new BlockPos(x, y, z);
                        IBlockState state = this.e.world.getBlockState(blockPos);
                        if (state.getBlock().hasTileEntity(state)) {
                            TileEntity te = this.e.world.getTileEntity(blockPos);
                            if (te.hasCapability(FragileGlassBase.FRAGILECAP, null)) {
                                te.getCapability(FragileGlassBase.FRAGILECAP, null).onCrash(te);
                            }
                        }
                    }
                }
            }
        }

        public void addEntityReference(Entity e)
        {
            this.e = e;
        }
    }
}
