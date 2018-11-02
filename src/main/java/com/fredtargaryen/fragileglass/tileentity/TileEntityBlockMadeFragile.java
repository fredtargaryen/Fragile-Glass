package com.fredtargaryen.fragileglass.tileentity;

import net.minecraft.init.Blocks;
import net.minecraft.util.ITickable;

public class TileEntityBlockMadeFragile extends TileEntityFragile implements ITickable {
    public TileEntityBlockMadeFragile() {
        super();
    }

    @Override
    public void update() {
        if(this.world.getBlockState(this.pos).getBlock() == Blocks.AIR) {
            this.world.removeTileEntity(this.pos);
        }
    }
}
