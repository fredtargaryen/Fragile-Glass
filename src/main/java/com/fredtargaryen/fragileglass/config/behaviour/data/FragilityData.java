package com.fredtargaryen.fragileglass.config.behaviour.data;

import com.fredtargaryen.fragileglass.config.behaviour.configloader.ConfigLoader;
import com.fredtargaryen.fragileglass.config.behaviour.datamanager.DataManager;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public abstract class FragilityData {
    protected double breakSpeedSq;

    public enum FragileBehaviour {
        //Break if above the break speed
        BREAK,
        //Change to a different BlockState
        CHANGE,
        //Execute a server command
        COMMAND,
        //Deal damage to the crashing entity
        DAMAGE,
        //Change to an FallingBlockEntity of the given BlockState
        FALL,
        //Behaviour depends on implementation of IFragileCapability
        MOD,
        //Update after the update delay if above the break speed
        UPDATE,
    }

    public FragilityData(double breakSpeed) {
        this.breakSpeedSq = breakSpeed * breakSpeed;
    }

    public abstract FragileBehaviour getBehaviour();

    public static FragileBehaviour parseBehaviour(String behaviour) {
        return FragileBehaviour.valueOf(behaviour.toUpperCase());
    }

    public abstract void parseExtraData(@Nullable BlockState oldState, ConfigLoader cl, String... extraData) throws FragilityDataParseException;

    public abstract void onCrash(@Nullable BlockState state, @Nullable TileEntity te, BlockPos pos, Entity crasher, double speedSq);

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

    @Override
    public String toString() {
        return this.getBehaviour().toString() + " " + Math.sqrt(this.breakSpeedSq);
    }
}
