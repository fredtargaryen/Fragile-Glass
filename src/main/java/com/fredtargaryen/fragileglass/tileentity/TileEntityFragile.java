package com.fredtargaryen.fragileglass.tileentity;

import com.fredtargaryen.fragileglass.FragileGlassBase;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;

public class TileEntityFragile extends TileEntity
{
    public TileEntityFragile()
    {
        super();
    }

    /**
     * Proposing a new physics system. This might be a terrible idea given that event handlers are
     * supposed to be small and this is fired for every single entity....
     * 1.   The entity is extracted from the EntityEvent and checked to see if it is capable of
     *      breaking fragile glass.
     * 2.   The entity's x, y and z motion are calculated. For most entities these are the same as
     *      motionX, motionY and motionZ. For some entities including players, these have to be
     *      calculated differently; see the code under PLAYERBREAKCAP.
     * 3.   A check to make sure the capable entity is travelling at least MINIMUM_ENTITY_SPEED
     *      blocks per tick. If not, the glass won't break.
     * 4.   [motionX, motionY, motionZ] make up a 3D vector representing the amount by which the
     *      entity will move this tick. If this vector intersects the fragile block's bounding
     *      box, then the entity will pass through the block this tick so the block should break.
     *      This avoids the problem of the previous system (see step P).
     * 5.   It is not enough to only look at the vector, as in general the vector will only pass
     *      through one block in a fragile glass wall (not enough for larger entities to get
     *      through). Instead the bounding box of the entity has to be "stretched" along the vector
     *      so that all blocks it intersects with will break, always providing a large enough gap.
     * 6.   If the entity is moving diagonally this creates a shape which is not a cube, so cannot
     *      be represented using AxisAlignedBB. Instead, AxisAlignedBB#offset(x, y, z) will be
     *      used to effectively move the entity bounding box along the movement vector, checking for
     *      intersections with the block bounding box along the way. If the two bounding boxes
     *      intersect at any point, the block is destroyed. The implementation of this "algorithm"
     *      is explained in inline comments below.
     * P.   This problem is most clear when a player falls onto a fragile glass ceiling. Rather than
     *      smoothly crashing through the ceiling and being damaged when they hit the floor, the
     *      player instead hits the glass ceiling (cancelling their downward movement), gets damaged,
     *      then crashes through to the floor. This problem makes shooting a fragile glass wall
     *      disappointing as well, because the arrow hits the wall (losing all its speed), then
     *      breaks the wall, then falls down as the block is no longer there.
     */

//    @SubscribeEvent(priority = EventPriority.HIGHEST)
//    public void clientBreakCheck(TickEvent.ClientTickEvent event)
//    {
//        if(event.phase == TickEvent.Phase.START)
//        {
//            Iterator<Entity> i = this.entities.iterator();
//            boolean shouldBreak = false;
//            while (!shouldBreak && i.hasNext()) {
//                shouldBreak = this.shouldBreakNow(i.next());
//            }
//            if (shouldBreak) {
//                MessageBreakerMovement mbm = new MessageBreakerMovement();
//                mbm.blockx = this.pos.getX();
//                mbm.blocky = this.pos.getY();
//                mbm.blockz = this.pos.getZ();
//                PacketHandler.INSTANCE.sendToServer(mbm);
//            }
//        }
//    }

    private boolean shouldBreakNow(Entity entity)
    {
        boolean validEntity = false;
        double mx = 0.0;
        double my = 0.0;
        double mz = 0.0;
        if(this.world.isRemote) {
            if (entity.hasCapability(FragileGlassBase.CLIENTBREAKCAP, null))
            {
                //The entity is controlled from keyboard input on client side (it is probably the player). The player (and
                //possibly other mobs controlled this way) cannot have their movement checked with motionX, motionY and
                //motionZ - on the server, for players, motionX and motionZ are 0 and the reliability of motionY is not
                //clear.
                mx = entity.motionX;
                my = entity.motionY;
                mz = entity.motionZ;
                validEntity = true;
            }
        }
        else if (entity.hasCapability(FragileGlassBase.SERVERBREAKCAP, null))
        {
            mx = entity.motionX;
            my = entity.motionY;
            mz = entity.motionZ;
            validEntity = true;
        }
        if(validEntity)
        {

        }
        return false;
    }
}