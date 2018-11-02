package com.fredtargaryen.fragileglass.tileentity;

import com.fredtargaryen.fragileglass.FragileGlassBase;
import com.fredtargaryen.fragileglass.tileentity.capability.FragilityDataManager;
import com.fredtargaryen.fragileglass.tileentity.capability.IBlockMadeFragileCapability;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ITickable;

public class TileEntityBlockMadeFragile extends TileEntityFragile implements ITickable {
    public TileEntityBlockMadeFragile() {
        super();
    }

    @Override
    public void update() {
        IBlockState state = this.world.getBlockState(this.pos);
        Block block = state.getBlock();
        if(block.isAir(state, this.world, this.pos)) {
            this.world.removeTileEntity(this.pos);
        }
        else {
            IBlockMadeFragileCapability ibmfc = this.getCapability(FragileGlassBase.BLOCKMADEFRAGILECAP, null);
            if(ibmfc != null) {
                FragilityDataManager fdm = FragilityDataManager.getInstance();
                FragilityDataManager.FragilityData fragilityData = fdm.getBlockFragilityData(block);
                ibmfc.setFragilityData(fragilityData);
            }
        }
    }
}
