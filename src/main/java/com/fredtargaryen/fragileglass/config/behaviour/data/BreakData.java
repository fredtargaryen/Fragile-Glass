package com.fredtargaryen.fragileglass.config.behaviour.data;

import com.fredtargaryen.fragileglass.config.behaviour.configloader.ConfigLoader;
import com.fredtargaryen.fragileglass.config.behaviour.datamanager.DataManager;
import net.minecraft.block.BlockState;

import javax.annotation.Nullable;

public class BreakData extends FragilityData {
    public BreakData(double breakSpeed) {
        super(breakSpeed);
    }

    @Override
    public DataManager.FragileBehaviour getBehaviour() {
        return DataManager.FragileBehaviour.BREAK;
    }

    @Override
    public void parseExtraData(@Nullable BlockState oldState, ConfigLoader cl, String... extraData) throws FragilityDataParseException {
        this.lengthCheck(extraData, 0);
    }
}
