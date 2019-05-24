package com.fredtargaryen.fragileglass.worldgen;

import net.minecraft.block.Block;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Random;

public abstract class PatchGen<C extends IFeatureConfig> extends Feature<IFeatureConfig> {
    //If it goes a long time without genning a patch, forces a bonus patch to gen
    protected int timeSinceLastPatch;
    protected final int timeToWaitBeforeBonusPatch;
    private int genChance;
    protected int avePatchSize;
    private Block blockToSet;
    private static final double piFraction = Math.PI / 6;
    private static final double twoPi = Math.PI * 2;

    public PatchGen (ForgeConfigSpec.IntValue genChance, ForgeConfigSpec.IntValue avePatchSize, Block blockToSet) {
        this.genChance = genChance.get();
        this.avePatchSize = avePatchSize.get();
        this.blockToSet =  blockToSet;
        this.timeSinceLastPatch = 0;
        this.timeToWaitBeforeBonusPatch = this.genChance + 1;
    }

    protected abstract boolean isBlockValidToTransform(Block block);
}
