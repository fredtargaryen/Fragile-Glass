package com.fredtargaryen.fragileglass.tileentity;

import net.minecraft.tileentity.TileEntity;

/**
 * Will probably always be empty. Capabilities can't be added to blocks so a TileEntity is needed.
 * Developers are free to add the capability to their own TileEntities instead of using this one.
 * Frustratingly, capabilities are attached in the earliest constructor of TileEntities, before
 * any information about the TileEntity is set. However glass, weak stone and ice have distinct break
 * conditions and procedures. With no way to extract the corresponding block from the TileEntity,
 * a different TileEntity is needed for each type. Suggestions of a better way to do this are very
 * welcome.
 */
public abstract class TileEntityFragile extends TileEntity
{
    public TileEntityFragile()
    {
        super();
    }
}