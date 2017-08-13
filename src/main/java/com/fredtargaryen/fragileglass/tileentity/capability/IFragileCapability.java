package com.fredtargaryen.fragileglass.tileentity.capability;

import net.minecraft.tileentity.TileEntity;

public interface IFragileCapability
{
    void onCrash(TileEntity te);
}
