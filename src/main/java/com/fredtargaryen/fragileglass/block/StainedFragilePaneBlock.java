package com.fredtargaryen.fragileglass.block;

import net.minecraft.block.IBeaconBeamColorProvider;
import net.minecraft.item.DyeColor;

public class StainedFragilePaneBlock extends FragilePaneBlock implements IBeaconBeamColorProvider {
    private DyeColor color;

    public StainedFragilePaneBlock(DyeColor color) {
        super();
        this.color = color;
    }

    @Override
    public DyeColor getColor() {
        return this.color;
    }
}
