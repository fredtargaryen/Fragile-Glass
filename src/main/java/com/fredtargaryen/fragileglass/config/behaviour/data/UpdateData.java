package com.fredtargaryen.fragileglass.config.behaviour.data;

import com.fredtargaryen.fragileglass.config.behaviour.configloader.ConfigLoader;
import com.fredtargaryen.fragileglass.config.behaviour.datamanager.DataManager;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class UpdateData extends FragilityData {
    private int updateDelay;

    public UpdateData(double breakSpeed) {
        super(breakSpeed);
    }

    @Override
    public FragilityData.FragileBehaviour getBehaviour() {
        return FragilityData.FragileBehaviour.UPDATE;
    }

    @Override
    public void parseExtraData(@Nullable BlockState state, ConfigLoader cl, String... extraData) throws FragilityDataParseException {
        this.lengthCheck(extraData, 1);
        //Validate updateDelay and silently clamp to >= 0
        try {
            this.updateDelay = Math.max(Integer.parseInt(extraData[0]), 0);
        }
        catch(NumberFormatException nfe) {
            throw new FragilityDataParseException("Update delay (" + extraData[0] + ") must be a number.");
        }
    }

    @Override
    public void onCrash(World world, BlockState state, @Nullable TileEntity te, BlockPos pos, @Nullable Entity crasher, double speedSq) {
        if (speedSq > this.breakSpeedSq) {
            world.getPendingBlockTicks().scheduleTick(pos, world.getBlockState(pos).getBlock(), this.updateDelay);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString() + " ");
        sb.append(this.updateDelay);
        return sb.toString();
    }
}
