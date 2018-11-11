package com.fredtargaryen.fragileglass.world;

import net.minecraft.block.state.IBlockState;

public class FragilityData {
    private FragilityDataManager.FragileBehaviour behaviour;
    private double breakSpeed;
    private int updateDelay;
    private IBlockState newBlockState;
    private String[] extraData;

    public FragilityData(FragilityDataManager.FragileBehaviour behaviour, double breakSpeed, int updateDelay, IBlockState newBlockState, String[] extraData) {
        this.behaviour = behaviour;
        this.breakSpeed = breakSpeed;
        this.updateDelay = updateDelay;
        this.newBlockState = newBlockState;
        this.extraData = extraData;
    }

    public FragilityDataManager.FragileBehaviour getBehaviour() { return this.behaviour; }

    public double getBreakSpeed() { return this.breakSpeed; }

    public int getUpdateDelay() { return this.updateDelay; }

    public IBlockState getNewBlockState() { return this.newBlockState; }

    public String[] getExtraData() { return this.extraData; }
}
