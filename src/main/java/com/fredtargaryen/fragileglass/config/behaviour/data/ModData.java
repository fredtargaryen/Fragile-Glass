package com.fredtargaryen.fragileglass.config.behaviour.data;

import com.fredtargaryen.fragileglass.config.behaviour.configloader.ConfigLoader;
import com.fredtargaryen.fragileglass.config.behaviour.datamanager.DataManager;
import net.minecraft.block.BlockState;

import javax.annotation.Nullable;

public class ModData extends FragilityData {
    private String[] extraData;

    public ModData(double breakSpeed) {
        super(breakSpeed);
    }

    @Override
    public DataManager.FragileBehaviour getBehaviour() {
        return DataManager.FragileBehaviour.MOD;
    }

    @Override
    public void parseExtraData(@Nullable BlockState state, ConfigLoader cl, String[] extraData) throws FragilityDataParseException {
        this.extraData = extraData;
    }
}
