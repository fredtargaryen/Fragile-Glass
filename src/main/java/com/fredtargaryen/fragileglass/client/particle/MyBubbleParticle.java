package com.fredtargaryen.fragileglass.client.particle;

import com.fredtargaryen.fragileglass.FragileGlassBase;
import jdk.nashorn.internal.ir.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.particle.*;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.IParticleData;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

//Normal bubbles will only work in water >:(
public class MyBubbleParticle extends SpriteTexturedParticle {
    private BlockPos cauldronPos;

    private MyBubbleParticle(World w, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, IAnimatedSprite spriteSet) {
        super(w, x, y, z, 0.0D, 0.0D, 0.0D);
        this.motionX = 0.0D;
        this.motionY = ySpeed;
        this.motionZ = 0.0D;
        this.maxAge = 50;
        this.setSprite(spriteSet.get(0, 50));
        this.cauldronPos = new BlockPos(this.posX, this.posY, this.posZ);
    }
    /**
     * Called to update the entity's position/logic.
     */
    public void tick() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.move(this.motionX, this.motionY, this.motionZ);
        //Calculate whether to expire
        BlockState state = this.world.getBlockState(this.cauldronPos);
        if (this.maxAge-- <= 0 ||
                state.getBlock() != FragileGlassBase.SUGAR_CAULDRON ||
                state.get(BlockStateProperties.AGE_0_7) == 6)
        {
            this.setExpired();
        }
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle makeParticle(BasicParticleType type, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new MyBubbleParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }
}
