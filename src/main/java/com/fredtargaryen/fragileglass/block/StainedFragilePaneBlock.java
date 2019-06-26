package com.fredtargaryen.fragileglass.block;

import net.minecraft.block.*;
import net.minecraft.item.DyeColor;
import net.minecraft.util.BlockRenderLayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class StainedFragilePaneBlock extends FragilePaneBlock implements IBeaconBeamColorProvider {
    private DyeColor color;

    public StainedFragilePaneBlock(DyeColor color) {
        super();
        this.color = color;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public DyeColor getColor() {
        return this.color;
    }
}
