package com.fredtargaryen.fragileglass.client.particle;

import net.minecraft.client.particle.ParticleSplash;
import net.minecraft.world.World;

public class ParticleMySplash extends ParticleSplash
{
    public ParticleMySplash(World p_i1230_1_, double p_i1230_2_, double p_i1230_4_, double p_i1230_6_)
    {
        super(p_i1230_1_, p_i1230_2_, p_i1230_4_, p_i1230_6_, 0.0D, 0.0D, 0.0D);
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.motionY -= (double)this.particleGravity;
        this.move(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.9800000190734863D;
        this.motionY *= 0.9800000190734863D;
        this.motionZ *= 0.9800000190734863D;

        if (this.maxAge-- <= 0)
        {
            this.setExpired();
        }
    }
}
