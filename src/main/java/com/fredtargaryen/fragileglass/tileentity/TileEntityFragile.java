package com.fredtargaryen.fragileglass.tileentity;

import com.fredtargaryen.fragileglass.DataReference;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.*;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ITickable;

import java.util.Iterator;
import java.util.List;

public class TileEntityFragile extends TileEntity implements ITickable
{
    public TileEntityFragile(){super();}

    @Override
    public void update()
    {
        if(!this.world.isRemote)
        {
            BlockPos pos = new BlockPos(this.pos);
            //Get all entities near enough to break it if fast enough
            IBlockState myBlockState = this.world.getBlockState(pos);
            AxisAlignedBB normAABB = myBlockState.getBlock()
                    .getCollisionBoundingBox(myBlockState, this.world, pos)
                    .offset(pos.getX(), pos.getY(), pos.getZ());
            AxisAlignedBB checkAABB = normAABB.expand(DataReference.GLASS_DETECTION_RANGE, DataReference.GLASS_DETECTION_RANGE, DataReference.GLASS_DETECTION_RANGE);
            List<Entity> entities = this.world.getEntitiesWithinAABBExcludingEntity(null, checkAABB);
            Iterator<Entity> remover = entities.iterator();
            while(remover.hasNext())
            {
                Entity nextEnt = remover.next();
                if(!(nextEnt instanceof EntityLivingBase
                        || nextEnt instanceof EntityArrow
                        || nextEnt instanceof EntityFireball
                        || nextEnt instanceof EntityMinecart
                        || nextEnt instanceof EntityFallingBlock
                        || nextEnt instanceof EntityFireworkRocket
                        || nextEnt instanceof EntityBoat
                        || nextEnt instanceof EntityTNTPrimed))
                {
                    remover.remove();
                }
            }
            if (entities.size() > 0)
            {
                //Check if any of the possible entities are fast enough
                boolean stop = false;
                remover = entities.iterator();
                while(!stop && remover.hasNext())
                {
                    Entity nextEnt = remover.next();
                    if(nextEnt instanceof EntityFallingBlock)
                    {
                        this.world.destroyBlock(pos, false);
                        stop = true;
                    }
                    else
                    {
                        if (Math.abs(nextEnt.motionX) > DataReference.MINIMUM_ENTITY_SPEED ||
                                Math.abs(nextEnt.motionY) > DataReference.MINIMUM_ENTITY_SPEED ||
                                Math.abs(nextEnt.motionZ) > DataReference.MINIMUM_ENTITY_SPEED)
                        {
                            //breaks it
                            this.world.destroyBlock(pos, false);
                            stop = true;
                        }
                    }
                }
            }
        }
    }
}