package com.fredtargaryen.fragileglass.block;

import net.minecraft.block.IBeaconBeamColorProvider;
import net.minecraft.item.DyeColor;

public class StainedFragileGlassBlock extends FragileGlassBlock implements IBeaconBeamColorProvider {
    private DyeColor color;

    public StainedFragileGlassBlock(DyeColor color) {
        super();
        this.color = color;
    }

    @Override
    public DyeColor getColor() {
        return this.color;
    }
}
