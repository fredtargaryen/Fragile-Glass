package com.fredtargaryen.fragileglass.entity.capability;

import net.minecraft.entity.Entity;

public interface IServerCanBreakCapability
{
    //Needs to be called after attaching the capability, so that the event can get data from the entity that has it
    void addEntityReference(Entity e);
}
