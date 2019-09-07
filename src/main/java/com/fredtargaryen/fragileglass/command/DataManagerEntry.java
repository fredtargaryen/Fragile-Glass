package com.fredtargaryen.fragileglass.command;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.tileentity.TileEntityType;

import java.util.Collection;
import java.util.List;

public class DataManagerEntry {
    private List<BlockState> blockStateSet;
    private Collection<EntityType<?>> entityTypeSet;
    private TileEntityType tileEntityType;

    public List<BlockState> getBlockStateSet() { return blockStateSet; }

    public Collection<EntityType<?>> getEntityTypeSet() { return entityTypeSet; }

    public TileEntityType getTileEntityType() { return tileEntityType; }

    public void setBlockStateSet(List<BlockState> list) {
        this.blockStateSet = list;
    }

    public void setEntityTypeSet(Collection<EntityType<?>> ets) {
        this.entityTypeSet = ets;
    }

    public void setTileEntityType(TileEntityType tet) {
        this.tileEntityType = tet;
    }
}
