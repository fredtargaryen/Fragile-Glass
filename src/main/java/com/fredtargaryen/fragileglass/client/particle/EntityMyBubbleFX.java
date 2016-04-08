package com.fredtargaryen.fragileglass.client.particle;

import net.minecraft.client.particle.EntityBubbleFX;
import net.minecraft.world.World;

//Normal bubbles will only work in water >:(
public class EntityMyBubbleFX extends EntityBubbleFX
{
    public EntityMyBubbleFX(World p_i1198_1_, double p_i1198_2_, double p_i1198_4_, double p_i1198_6_, double p_i1198_8_, double p_i1198_10_, double p_i1198_12_)
    {
        super(p_i1198_1_, p_i1198_2_, p_i1198_4_, p_i1198_6_, p_i1198_8_, p_i1198_10_, p_i1198_12_);
        this.xSpeed = 0.0D;
        this.zSpeed = 0.0D;
    }
    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.moveEntity(this.xSpeed, this.ySpeed, this.zSpeed);
        this.ySpeed *= 0.8500000238418579D;

        if (this.particleMaxAge-- <= 0)
        {
            this.setExpired();
        }
    }
}
