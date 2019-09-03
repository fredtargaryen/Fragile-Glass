package com.fredtargaryen.fragileglass.config.behaviour.data;

import com.fredtargaryen.fragileglass.config.behaviour.configloader.ConfigLoader;
import com.fredtargaryen.fragileglass.config.behaviour.datamanager.DataManager;
import net.minecraft.block.BlockState;

import javax.annotation.Nullable;

public abstract class FragilityData {
    private double breakSpeed;

    public FragilityData(double breakSpeed) {
        this.breakSpeed = breakSpeed;
    }

    public abstract DataManager.FragileBehaviour getBehaviour();

    public abstract void parseExtraData(@Nullable BlockState oldState, ConfigLoader cl, String... extraData) throws FragilityDataParseException;

    public double getBreakSpeed() { return this.breakSpeed; }

    public class FragilityDataParseException extends Exception {
        public FragilityDataParseException(String message) {
            super(message);
        }
    }

    /**
     * Check that the number of extra values supplied after the three required values is correct.
     * @param extraData The extra values (so not including block string, behaviour name or break speed)
     * @param length The required length
     * @throws FragilityDataParseException if the length of extraData != the required length
     */
    protected final void lengthCheck(String[] extraData, int length) throws FragilityDataParseException {
        if(extraData.length != length) {
            throw new FragilityDataParseException("This behaviour must have "+length+" extra parameters after the first 3.");
        }
    }
}
