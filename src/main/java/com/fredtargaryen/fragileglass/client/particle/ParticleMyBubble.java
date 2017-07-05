package com.fredtargaryen.fragileglass.client.particle;

import net.minecraft.client.particle.ParticleBubble;
import net.minecraft.world.World;

//Normal bubbles will only work in water >:(
public class ParticleMyBubble extends ParticleBubble
{
    public ParticleMyBubble(World p_i1198_1_, double p_i1198_2_, double p_i1198_4_, double p_i1198_6_)
    {
        super(p_i1198_1_, p_i1198_2_, p_i1198_4_, p_i1198_6_, 0.0D, 0.0D, 0.0D);
        this.motionX = 0.0D;
        this.motionZ = 0.0D;
    }
    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.move(this.motionX, this.motionY, this.motionZ);
        this.motionY *= 0.8500000238418579D;

        if (this.particleMaxAge-- <= 0)
        {
            this.setExpired();
        }
    }
}
