package com.fredtargaryen.fragileglass.world;

import com.fredtargaryen.fragileglass.entity.capability.CommonBreakingMethods;
import com.fredtargaryen.fragileglass.entity.capability.IBreakCapability;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Iterator;

import static com.fredtargaryen.fragileglass.FragileGlassBase.BREAKCAP;

public class BreakSystem
{
    private World world;
    public void init(World world)
    {
        this.world = world;
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void end(World world)
    {
        if(this.world == world) {
            MinecraftForge.EVENT_BUS.unregister(this);
        }
    }

    @SubscribeEvent(priority= EventPriority.HIGHEST)
    public void breakCheck(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.START)
        {
            Iterator<Entity> i = event.world.getLoadedEntityList().iterator();
            while(i.hasNext())
            {
                Entity e = i.next();
                if(!e.isDead) {
                    if (e.hasCapability(BREAKCAP, null)) {
                        IBreakCapability ibc = e.getCapability(BREAKCAP, null);
                        ibc.update(e);
                        double speedSq = ibc.getSpeedSquared(e);
                        if (CommonBreakingMethods.isValidMoveSpeedSquared(speedSq)) {
                            double speed = Math.sqrt(speedSq);
                            if (ibc.isAbleToBreak(e, speed)) {
                                CommonBreakingMethods.breakBlocksInWay(e, ibc.getMotionX(e), ibc.getMotionY(e), ibc.getMotionZ(e), speed);
                            }
                        }
                    }
                }
            }
        }
    }
}
