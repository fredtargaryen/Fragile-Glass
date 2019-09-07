package com.fredtargaryen.fragileglass.config.behaviour.data;

import com.fredtargaryen.fragileglass.config.behaviour.configloader.ConfigLoader;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public class DamageData extends FragilityData {
    private DamageSource damageSource;
    private float damageAmount;

    public DamageData(double breakSpeed) {
        super(breakSpeed);
    }

    @Override
    public FragilityData.FragileBehaviour getBehaviour() {
        return FragilityData.FragileBehaviour.DAMAGE;
    }

    @Override
    public void parseExtraData(@Nullable BlockState state, ConfigLoader cl, String... extraData) throws FragilityData.FragilityDataParseException {
        this.lengthCheck(extraData, 2);
        this.damageSource = this.parseDamageSource(extraData[0]);
        //Validate updateDelay and silently clamp to >= 0
        this.damageAmount = Float.parseFloat(extraData[1]);
    }

    @Override
    public void onCrash(@Nullable BlockState state, @Nullable TileEntity te, BlockPos pos, Entity crasher, double speedSq) {
        crasher.attackEntityFrom(this.damageSource, this.damageAmount);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString() + " ");
        sb.append(this.damageAmount);
        return sb.toString();
    }

    private DamageSource parseDamageSource(String sourceString) throws FragilityDataParseException {
        String lowerString = sourceString.toLowerCase();
        switch (lowerString) {
            case "infire":
                return DamageSource.IN_FIRE;
            case "lightningbolt":
                return DamageSource.LIGHTNING_BOLT;
            case "onfire":
                return DamageSource.ON_FIRE;
            case "lava":
                return DamageSource.LAVA;
            case "hotfloor":
                return DamageSource.HOT_FLOOR;
            case "inwall":
                return DamageSource.IN_WALL;
            case "cramming":
                return DamageSource.CRAMMING;
            case "drown":
                return DamageSource.DROWN;
            case "starve":
                return DamageSource.STARVE;
            case "cactus":
                return DamageSource.CACTUS;
            case "fall":
                return DamageSource.FALL;
            case "flyintowall":
                return DamageSource.FLY_INTO_WALL;
            case "outofworld":
                return DamageSource.OUT_OF_WORLD;
            case "generic":
                return DamageSource.GENERIC;
            case "magic":
                return DamageSource.MAGIC;
            case "wither":
                return DamageSource.WITHER;
            case "anvil":
                return DamageSource.ANVIL;
            case "fallingblock":
                return DamageSource.FALLING_BLOCK;
            case "dragonbreath":
                return DamageSource.DRAGON_BREATH;
            case "fireworks":
                return DamageSource.FIREWORKS;
            case "dryout":
                return DamageSource.DRYOUT;
            case "sweetberrybush":
                return DamageSource.SWEET_BERRY_BUSH;
            default:
                throw new FragilityDataParseException("No damage source found. Please remove any underscores.");
        }
    }
}
