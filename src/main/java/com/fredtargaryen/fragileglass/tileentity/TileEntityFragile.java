package com.fredtargaryen.fragileglass.tileentity;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Will probably always be empty. A TileEntity can be used to work around the need to write a config line for each block
 * (as in the case of TileEntityFragileGlass, where all 34 blocks and their states are unified under one TileEntity
 * line), or to provide special behaviour (as in TileEntityWeakStone; a tile entity is needed to create the delayed
 * crumble effect).
 * Every TileEntityType of a registered TileEntity has a ResourceLocation, and the name of the ResourceLocation is used
 * in fragileglassft_blocks.cfg for fragile behaviour configuration.
 */
public abstract class TileEntityFragile extends TileEntity {
    public TileEntityFragile(TileEntityType tet)
    {
        super(tet);
    }
}