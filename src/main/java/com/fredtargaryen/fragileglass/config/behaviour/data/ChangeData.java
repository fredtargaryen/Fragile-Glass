package com.fredtargaryen.fragileglass.config.behaviour.data;

import com.fredtargaryen.fragileglass.config.behaviour.configloader.ConfigLoader;
import com.fredtargaryen.fragileglass.config.behaviour.datamanager.DataManager;
import net.minecraft.block.BlockState;

import javax.annotation.Nullable;

public class ChangeData extends FragilityData {
    private BlockState newBlockState;

    public ChangeData(double breakSpeed) {
        super(breakSpeed);
    }

    @Override
    public DataManager.FragileBehaviour getBehaviour() {
        return DataManager.FragileBehaviour.CHANGE;
    }

    @Override
    public void parseExtraData(@Nullable BlockState oldState, ConfigLoader cl, String[] extraData) throws FragilityDataParseException {
        this.lengthCheck(extraData, 1);
        try {
            //Validate newState
            if(oldState == null){
                this.newBlockState = cl.getSingleBlockStateFromString(extraData[1]);
            }
            else {
                this.newBlockState = cl.getNewStateFromOldAndString(oldState, extraData[0]);
            }
        }
        catch(ConfigLoader.ConfigLoadException cle) {
            throw new FragilityDataParseException(cle.shortMessage);
        }
    }

    public BlockState getNewBlockState() { return this.newBlockState; }
}
