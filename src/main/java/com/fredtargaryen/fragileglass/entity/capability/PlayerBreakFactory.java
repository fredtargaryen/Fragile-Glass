package com.fredtargaryen.fragileglass.entity.capability;

import net.minecraft.entity.Entity;
import java.util.concurrent.Callable;

public class PlayerBreakFactory implements Callable<IPlayerBreakCapability>
{
    @Override
    public IPlayerBreakCapability call() throws Exception {
        return new PlayerCanBreakImpl();
    }

    private class PlayerCanBreakImpl implements IPlayerBreakCapability
    {
        protected double prevPosX;
        protected double prevPosY;
        protected double prevPosZ;
        private double modeDistance;
        private byte distancePointer;
        private double[] latestDistances;
        private double[] motionVec;

        public void init(Entity e)
        {
            this.prevPosX = e.posX;
            this.prevPosY = e.posY;
            this.prevPosZ = e.posZ;
            this.modeDistance = 0.0;
            this.latestDistances = new double[] {0.0, 0.0, 0.0};
            this.distancePointer = 0;
            this.motionVec = new double[] { 0.0, 0.0, 0.0 };
        }

        @Override
        public void update(Entity e) {
            this.motionVec[0] = e.posX - this.prevPosX;
            this.prevPosX = e.posX;
            this.motionVec[1] = e.posY - this.prevPosY;
            this.prevPosY = e.posY;
            this.motionVec[2] = e.posZ - this.prevPosZ;
            this.prevPosZ = e.posZ;
        }

        @Override
        public double getSpeedSquared(Entity e) {
            return this.getNormalSpeed(this.motionVec[0] * this.motionVec[0] + this.motionVec[1] * this.motionVec[1] + this.motionVec[2] * this.motionVec[2]);
        }

        @Override
        public boolean isAbleToBreak(Entity e, double speed) {
            return speed >= 0.275;
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

        /**
         * --PROBLEM--
         * When the player walks forward in a straight line, for example, they travel an average of 0.215 blocks per
         * tick. However the values produced can deviate wildly from this value, reaching as high as 1.5 and possibly
         * more. 0.28 is the value for sprinting, so in a normal walk the player can apparently reach a sprinting speed
         * in certain ticks, and break glass that they should not be able to break.
         *
         * --DESCRIPTION--
         * getNormalDistance keeps track of a "mode" speed that the player is probably travelling at in reality.
         * Distance values that are deemed to be out of the ordinary are discarded, with the mode returned instead.
         * Each new distance value is added to latestDistances[0], or the next location if it is similar to the previous
         * distance value. If 3 similar distance values occur in succession, the average of the 3 values becomes the new
         * mode.
         *
         * --PURPOSE--
         * This method should remove or at least greatly reduce instances of blocks breaking despite the player not
         * actually moving quickly enough. The drawback is that a change in speed will not be reflected for
         * (latestDistances.length + 1) ticks, so if the player begins sprinting during that time they will be unable to
         * break a block. 4 ticks is 0.2 seconds so hopefully this will not be too noticeable.
         *
         * --TROUBLESHOOTING--
         * If modeDistance is not able to reach roughly the player's average movement speed, latestDistances may be too
         * large. Reducing the length of latestDistances means less values are required in succession to overwrite the
         * mode. The caveat here is that outlier values have a slightly higher chance of overwriting the mode, but from
         * the data seen at the time of writing, outlier values occurring in succession are not similar enough to take
         * up more than one location in latestDistances at a time, and the mode can go back to normal quickly.
         *
         * If the player goes from a low average speed to a high one, e.g. from walking to sprinting, but the glass
         * doesn't break because the mode distance is still low, the threshold for mode similarity (the first 0.01)
         * may be too high, or the threshold for previous distance similarity (the second 0.01) may be too low.
         */
        protected double getNormalSpeed(double dist)
        {
            if(Math.abs(dist - this.modeDistance) < 0.01)
            {
                //Distance is similar to the mode already
                this.distancePointer = 0;
                this.latestDistances[this.distancePointer] = dist;
            }
            else if(Math.abs(dist - this.latestDistances[this.distancePointer]) < 0.01)
            {
                //Distance is similar to the previous distance value.
                if(this.distancePointer < this.latestDistances.length - 1)
                {
                    //Add to array. A value similar to this one is more likely to become the new mode.
                    this.latestDistances[++this.distancePointer] = dist;
                }
                else
                {
                    //Array is now full of similar speeds; the mean of these becomes the new mode.
                    this.modeDistance = (this.latestDistances[0] + this.latestDistances[1] + this.latestDistances[2]) / 3.0;
                    this.distancePointer = 0;
                }
            }
            else
            {
                //Distance is brand new but restart the array in case it could be the next mode
                this.distancePointer = 0;
                this.latestDistances[this.distancePointer] = dist;
            }
            return this.modeDistance;
        }
    }
}
