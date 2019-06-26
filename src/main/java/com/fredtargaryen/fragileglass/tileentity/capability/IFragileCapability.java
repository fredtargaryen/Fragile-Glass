package com.fredtargaryen.fragileglass.tileentity.capability;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;

/**
 * Mod devs making use of the block config file for fragility can work with extra data supplied by their end users,
 * supplied as a String array in the file. Recommended usage in AttachCapabilitiesEvent<TileEntity> handler:
 * FragilityDataManager fdm = FragilityDataManager.getInstance();
 * FragilityData data = fdm.getTileEntityFragilityData(te);
 * if(data != null) {
 *     ICapabilityProvider icp = <construct your capability, with the FragilityData getters to access the values>
 *     event.addCapability(<capability location>, icp);
 * }
 */
public interface IFragileCapability {
    /**
     * This method is the final decision on whether a block will break. Even if IBreakCapability#isAbleToBreak always
     * returns true, the block only breaks if told to in onCrash.
     * Return true iff the entity which is trying to break the block was blocked from moving past this block.
     * For example, if it was not fast enough to break the block.
     * You can still break the block but return true, for whatever reason.
     * Whenever this method is called, the projected entity bounding box WILL be inside the 1x1x1 space of a block, BUT
     * not necessarily intersecting the block's bounding box. Implementations will need to check for that.
     */
    void onCrash(BlockState state, TileEntity te, Entity crasher, double speed);
}
