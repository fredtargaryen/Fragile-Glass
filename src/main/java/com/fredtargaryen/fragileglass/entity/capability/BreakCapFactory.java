package com.fredtargaryen.fragileglass.entity.capability;

import com.fredtargaryen.fragileglass.DataReference;
import net.minecraft.entity.Entity;

import java.util.concurrent.Callable;

public class BreakCapFactory implements Callable<IBreakCapability> {
    @Override
    public IBreakCapability call() throws Exception {
        return new BreakImpl();
    }

    public class BreakImpl implements IBreakCapability
    {
        public void init(Entity e) {}

        public void update(Entity e) {}

        @Override
        public double getSpeedSquared(Entity e) {
            return e.motionX * e.motionX + e.motionY * e.motionY + e.motionZ * e.motionZ;
        }

        @Override
        public boolean isAbleToBreak(Entity e, double speed) {
            return speed >= DataReference.MINIMUM_ENTITY_SPEED;
        }

        @Override
        public double getMotionX(Entity e) {
            return e.motionX;
        }

        @Override
        public double getMotionY(Entity e) {
            return e.motionY;
        }

        @Override
        public double getMotionZ(Entity e) {
            return e.motionZ;
        }

        @Override
        public byte getNoOfBreaks(Entity e) {
            return 1;
        }
    }
}
