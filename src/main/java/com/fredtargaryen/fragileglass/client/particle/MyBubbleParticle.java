package com.fredtargaryen.fragileglass.client.particle;

import net.minecraft.client.particle.*;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.IParticleData;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

//Normal bubbles will only work in water >:(
public class MyBubbleParticle extends SpriteTexturedParticle {
    private MyBubbleParticle(World p_i1198_1_, double p_i1198_2_, double p_i1198_4_, double p_i1198_6_) {
        super(p_i1198_1_, p_i1198_2_, p_i1198_4_, p_i1198_6_, 0.0D, 0.0D, 0.0D);
        this.motionX = 0.0D;
        this.motionZ = 0.0D;
    }
    /**
     * Called to update the entity's position/logic.
     */
    public void tick() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.move(this.motionX, this.motionY, this.motionZ);
        this.motionY *= 0.8500000238418579D;

        if (this.maxAge-- <= 0)
        {
            this.setExpired();
        }
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<IParticleData> {
        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle makeParticle(IParticleData type, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new MyBubbleParticle(worldIn, x, y, z);
        }
    }
}
