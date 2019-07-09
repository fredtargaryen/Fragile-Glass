package com.fredtargaryen.fragileglass.tileentity;

import com.fredtargaryen.fragileglass.FragileGlassBase;
import net.minecraft.tileentity.TileEntity;

/**
 * Will probably always be empty. A TileEntity can be used to provide developer-defined crash behaviour - here, a tile
 * entity is needed to create the delayed crumble effect).
 * Every TileEntityType of a registered TileEntity has a ResourceLocation, and the name of the ResourceLocation is used
 * in fragileglassft_tileentities.cfg for fragile behaviour configuration.
 */
public class TileEntityWeakStone extends TileEntity {
    public TileEntityWeakStone() { super(FragileGlassBase.TEWS_TYPE); }
}
