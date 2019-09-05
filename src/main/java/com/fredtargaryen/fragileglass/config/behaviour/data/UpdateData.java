package com.fredtargaryen.fragileglass.config.behaviour.data;

import com.fredtargaryen.fragileglass.config.behaviour.configloader.ConfigLoader;
import com.fredtargaryen.fragileglass.config.behaviour.datamanager.DataManager;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

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
        this.updateDelay = Math.max(Integer.parseInt(extraData[0]), 0);
    }

    @Override
    public void onCrash(@Nullable BlockState state, @Nullable TileEntity te, BlockPos pos, Entity crasher, double speedSq) {
        if (speedSq > this.breakSpeedSq) {
            crasher.world.getPendingBlockTicks().scheduleTick(pos, crasher.world.getBlockState(pos).getBlock(), this.updateDelay);
        }
    }
}
