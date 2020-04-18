package com.fredtargaryen.fragileglass.config.behaviour.data;

import com.fredtargaryen.fragileglass.config.behaviour.configloader.ConfigLoader;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ExplodeData extends FragilityData {
    private float strength;

    public ExplodeData(double breakSpeed) {
        super(breakSpeed);
    }

    @Override
    public FragileBehaviour getBehaviour() {
        return FragileBehaviour.EXPLODE;
    }

    @Override
    public void parseExtraData(@Nullable BlockState state, ConfigLoader cl, String... extraData) throws FragilityDataParseException {
        this.lengthCheck(extraData, 1);
        //Validate updateDelay and silently clamp to 0.0 <= f <= 100.0
        Float f;
        try {
            f = Float.parseFloat(extraData[0]);
        }
        catch(NumberFormatException nfe) {
            throw new FragilityDataParseException(extraData[0] + "must be a decimal between 1 and 100 inclusive.");
        }
        this.strength = Math.max(Math.min(f, 100F), 1F);
    }

    @Override
    public void onCrash(World world, BlockState state, @Nullable TileEntity te, BlockPos pos, @Nullable Entity crasher, double speedSq) {
        if (speedSq > this.breakSpeedSq) {
            world.createExplosion(null, (double) pos.getX(), (double) pos.getY(), (double) pos.getZ(), this.strength, Explosion.Mode.BREAK);
        }
    }

    @Override
    public String toString() {
        return super.toString() + " " + this.strength;
    }
}
