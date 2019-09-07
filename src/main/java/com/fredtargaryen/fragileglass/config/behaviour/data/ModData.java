package com.fredtargaryen.fragileglass.config.behaviour.data;

import com.fredtargaryen.fragileglass.FragileGlassBase;
import com.fredtargaryen.fragileglass.config.behaviour.configloader.ConfigLoader;
import com.fredtargaryen.fragileglass.config.behaviour.datamanager.DataManager;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public class ModData extends FragilityData {
    private String[] extraData;

    public ModData(double breakSpeed) {
        super(breakSpeed);
    }

    @Override
    public FragilityData.FragileBehaviour getBehaviour() {
        return FragilityData.FragileBehaviour.MOD;
    }

    @Override
    public void parseExtraData(@Nullable BlockState state, ConfigLoader cl, String[] extraData) throws FragilityDataParseException {
        this.extraData = extraData;
    }

    @Override
    public void onCrash(@Nullable BlockState state, @Nullable TileEntity te, BlockPos pos, Entity crasher, double speedSq) {
        te.getCapability(FragileGlassBase.FRAGILECAP).ifPresent(cap -> cap.onCrash(state, te, crasher, speedSq, extraData));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString() + " ");
        for(String s : this.extraData) {
            sb.append(" ");
            sb.append(s);
        }
        return sb.toString();
    }
}
