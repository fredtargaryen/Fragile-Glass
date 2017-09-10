package com.fredtargaryen.fragileglass.entity.capability;

import net.minecraft.entity.Entity;

public interface IBreakCapability
{
    void init(Entity e);

    void update(Entity e);

    double getSpeedSquared(Entity e);

    boolean isAbleToBreak(Entity e, double speed);

    double getMotionX(Entity e);

    double getMotionY(Entity e);

    double getMotionZ(Entity e);
}
