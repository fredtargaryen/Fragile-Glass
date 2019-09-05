package com.fredtargaryen.fragileglass.entity.capability;

import com.fredtargaryen.fragileglass.DataReference;
import com.fredtargaryen.fragileglass.network.MessageBreakerMovement;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.concurrent.Callable;

public class PlayerBreakFactory implements Callable<IPlayerBreakCapability>
{
    @Override
    public IPlayerBreakCapability call() throws Exception {
        return new PlayerCanBreakImpl();
    }

    private class PlayerCanBreakImpl implements IPlayerBreakCapability {
        protected double prevPosX;
        protected double prevPosY;
        protected double prevPosZ;
        private double[] motionVec;
        private double lastSpeedSq;

        public void init(Entity e) {
            this.prevPosX = e.posX;
            this.prevPosY = e.posY;
            this.prevPosZ = e.posZ;
            this.lastSpeedSq = 0.0;
            this.motionVec = new double[] { 0.0, 0.0, 0.0 };
        }

        @Override
        public void update(Entity e) { }

        @Override
        public double getSpeedSquared(Entity e) {
            return this.lastSpeedSq;
        }

        @Override
        public boolean isAbleToBreak(Entity e, double speedSq) {
            return !e.isSpectator() && speedSq >= DataReference.PLAYER_WALK_SPEED_SQUARED && speedSq <= DataReference.MAXIMUM_ENTITY_SPEED_SQUARED;
        }

        @Override
        public double getMotionX(Entity e) {
            return this.motionVec[0];
        }

        @Override
        public double getMotionY(Entity e) {
            return this.motionVec[1];
        }

        @Override
        public double getMotionZ(Entity e) {
            return this.motionVec[2];
        }

        @Override
        public byte getNoOfBreaks(Entity e) {
            return 3;
        }

        public void onMessage(MessageBreakerMovement mbm) {
            this.motionVec[0] = mbm.motionx;
            this.motionVec[1] = mbm.motiony;
            this.motionVec[2] = mbm.motionz;
            this.lastSpeedSq = mbm.speedSq;
        }
    }
}
