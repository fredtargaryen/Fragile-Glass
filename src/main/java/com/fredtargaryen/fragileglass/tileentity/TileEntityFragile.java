package com.fredtargaryen.fragileglass.tileentity;

import net.minecraft.tileentity.TileEntity;

/**
 * Will probably always be empty. Capabilities can't be added to blocks so a TileEntity is needed.
 * Every Class of a registered TileEntity has a ResourceLocation, and the name of the ResourceLocation is used
 * in fragileglassft_blocklist.cfg for fragile behaviour configuration.
 */
public abstract class TileEntityFragile extends TileEntity {
    public TileEntityFragile()
    {
        super();
    }
}