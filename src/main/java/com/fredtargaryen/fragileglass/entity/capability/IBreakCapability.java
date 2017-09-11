package com.fredtargaryen.fragileglass.entity.capability;

import net.minecraft.entity.Entity;

/**
 * Capability for entities that may be able to break fragile blocks. Entities without this Capability won't be able to.
 * If you have an entity that is controlled purely by server updates, use or extend this capability.
 */
public interface IBreakCapability
{
    /**
     * Set up the capability. This is a good place to save a reference to the Entity e.
     * This method is not called by default.
     */
    void init(Entity e);

    /**
     * Called to update the speed of the entity if necessary. Called before determining whether the entity breaks any
     * blocks.
     */
    void update(Entity e);

    /**
     * Get the squared speed of the entity. Asking for just the speed means requiring a Math.sqrt() operation for each
     * entity each tick, which is expensive.
     */
    double getSpeedSquared(Entity e);

    /**
     * Return whether the entity is able to break fragile blocks at this particular tick.
     * Normally this is based on whether the entity is travelling at at least sprinting speed,
     * but could be conditioned on any other aspect of the entity.
     * @param speed the distance the entity is supposed to travel this tick.
     */
    boolean isAbleToBreak(Entity e, double speed);

    /**
     * @return the x motion of the entity, for the block break code.
     */
    double getMotionX(Entity e);

    /**
     * @return the y motion of the entity, for the block break code.
     */
    double getMotionY(Entity e);

    /**
     * @return the z motion of the entity, for the block break code.
     */
    double getMotionZ(Entity e);
}
