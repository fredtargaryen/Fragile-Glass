package com.fredtargaryen.fragileglass.entity.capability;

/**
 * Players get this Capability instead of IBreakCapability, because their motion is never updated server-side.
 * Player motion is set via a MessageBreakerMovement so this method is required.
 */
public interface IPlayerBreakCapability extends IBreakCapability {
    void onMessage(MessageBreakerMovement mbm);
}
