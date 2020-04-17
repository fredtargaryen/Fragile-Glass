package com.fredtargaryen.fragileglass.config.behaviour.data;

import com.fredtargaryen.fragileglass.config.behaviour.configloader.ConfigLoader;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public class WaitData extends FragilityData {
    private int ticks;

    public WaitData(double breakSpeed) { super(breakSpeed); }

    @Override
    public FragilityData.FragileBehaviour getBehaviour() {
        return FragilityData.FragileBehaviour.WAIT;
    }

    @Override
    public void parseExtraData(@Nullable BlockState state, ConfigLoader cl, String[] extraData) throws FragilityDataParseException {
        this.lengthCheck(extraData, 1);
        //Validate ticks and silently set to at least 1
        int ticks;
        try {
            ticks = Integer.parseInt(extraData[0]);
        }
        catch(NumberFormatException nfe) {
            throw new FragilityDataParseException(extraData[0] + "must be an integer larger than 0.");
        }
        this.ticks = Math.max(ticks, 1);
    }

    @Override
    public void onCrash(@Nullable BlockState state, @Nullable TileEntity te, BlockPos pos, Entity crasher, double speedSq) {
        //Does nothing. This is a special-case behaviour that is not used in the usual way.
    }

    public int getTicks() { return this.ticks; }
}
