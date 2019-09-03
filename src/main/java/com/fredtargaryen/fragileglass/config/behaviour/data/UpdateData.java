package com.fredtargaryen.fragileglass.config.behaviour.data;

import com.fredtargaryen.fragileglass.config.behaviour.configloader.ConfigLoader;
import com.fredtargaryen.fragileglass.config.behaviour.datamanager.DataManager;
import net.minecraft.block.BlockState;

import javax.annotation.Nullable;

public class UpdateData extends FragilityData {
    private int updateDelay;

    public UpdateData(double breakSpeed) {
        super(breakSpeed);
    }

    @Override
    public DataManager.FragileBehaviour getBehaviour() {
        return DataManager.FragileBehaviour.UPDATE;
    }

    @Override
    public void parseExtraData(@Nullable BlockState state, ConfigLoader cl, String... extraData) throws FragilityDataParseException {
        this.lengthCheck(extraData, 1);
        //Validate updateDelay and silently clamp to >= 0
        this.updateDelay = Math.max(Integer.parseInt(extraData[0]), 0);
    }

    public int getUpdateDelay() { return this.updateDelay; }
}
