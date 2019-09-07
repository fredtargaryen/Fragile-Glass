package com.fredtargaryen.fragileglass.config.behaviour.data;

import com.fredtargaryen.fragileglass.config.behaviour.configloader.ConfigLoader;
import com.fredtargaryen.fragileglass.config.behaviour.configloader.KeyParser;
import com.fredtargaryen.fragileglass.config.behaviour.datamanager.DataManager;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public class ChangeData extends FragilityData {
    private BlockState newBlockState;

    public ChangeData(double breakSpeed) {
        super(breakSpeed);
    }

    @Override
    public FragilityData.FragileBehaviour getBehaviour() {
        return FragilityData.FragileBehaviour.CHANGE;
    }

    @Override
    public void parseExtraData(@Nullable BlockState oldState, ConfigLoader cl, String[] extraData) throws FragilityDataParseException {
        this.lengthCheck(extraData, 1);
        try {
            //Validate newState
            if(oldState == null){
                this.newBlockState = cl.getSingleBlockStateFromString(extraData[0]);
            }
            else {
                this.newBlockState = cl.getNewStateFromOldAndString(oldState, extraData[0]);
            }
        }
        catch(ConfigLoader.ConfigLoadException cle) {
            throw new FragilityDataParseException(cle.shortMessage);
        }
    }

    @Override
    public void onCrash(@Nullable BlockState state, @Nullable TileEntity te, BlockPos pos, Entity crasher, double speedSq) {
        if (speedSq > this.breakSpeedSq) {
            crasher.world.setBlockState(pos, this.newBlockState);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString() + " ");
        sb.append(KeyParser.cleanBlockStateString(this.newBlockState.toString()));
        return sb.toString();
    }
}
