package com.fredtargaryen.fragileglass.entity.capability;

import com.fredtargaryen.fragileglass.DataReference;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Vector3d;

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
            Vector3d motion = e.getMotion();
            return motion.x * motion.x + motion.y * motion.y + motion.z * motion.z;
        }

        @Override
        public boolean isAbleToBreak(Entity e, double speedSq) {
            return speedSq >= DataReference.MINIMUM_ENTITY_SPEED_SQUARED
                    && speedSq <= DataReference.MAXIMUM_ENTITY_SPEED_SQUARED;
        }

        @Override
        public double getMotionX(Entity e) {
            return e.getMotion().x;
        }

        @Override
        public double getMotionY(Entity e) {
            return e.getMotion().y;
        }

        @Override
        public double getMotionZ(Entity e) {
            return e.getMotion().z;
        }

        @Override
        public byte getNoOfBreaks(Entity e) {
            return 1;
        }
    }
}
