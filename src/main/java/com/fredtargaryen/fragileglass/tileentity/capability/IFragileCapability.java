package com.fredtargaryen.fragileglass.tileentity.capability;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;

public interface IFragileCapability {
    /**
     * Mod TileEntities making use of the block config file for fragility can work with extra data in whatever format
     * they wish, supplied as a String array. This method is called once just after construction, to let the
     * Capability implementation store or process the data as necessary.
     * TODO Currently unusable.
     */
    void handleExtraData(String[] extraData);

    /**
     * This method is the final decision on whether a block will break. Even if IBreakCapability#isAbleToBreak always
     * returns true, the block only breaks if told to in onCrash.
     * Return true iff the entity which is trying to break the block was blocked from moving past this block.
     * For example, if it was not fast enough to break the block.
     * You can still break the block but return true, for whatever reason.
     * Whenever this method is called, the entity bounding box WILL be inside the 1x1x1 space of a block, BUT
     * not necessarily intersecting the block's bounding box. Implementations will need to check for that.
     */
    void onCrash(IBlockState state, TileEntity te, Entity crasher, double speed);
}
