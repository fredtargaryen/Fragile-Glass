package com.fredtargaryen.fragileglass.tileentity.capability;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;

/**
 * Mod devs making use of fragileglassft_tileentities.cfg for fragility can work with extra data supplied by their end
 * users. A config line would look like:
 * modname:modtile mod 0.2 a b c d
 * Where newmod:modtile is the registry name of the TileEntity's type, mod specifies that a mod behaviour is being created,
 * 0.2 is the minimum break speed and a, b, c and d are extra data parsed in the method below.
 *
 * The modder must subscribe to AttachCapabilitiesEvent<TileEntity>, and in the event handler provide an implementation
 * of IFragileCapability, with the TileEntity's behaviour in the body of onCrash. The code is roughly this:
 *
 * @SubscribeEvent
 * public void addModCrashBehaviour(TileEntity te, AttachCapabilitiesEvent<TileEntity> evt) {
 *     ICapabilityProvider iCapProv = new ICapabilityProvider() {
 *         IFragileCapability inst = new IFragileCapability() {
 *             @Override
 *             public void onCrash(BlockState state, TileEntity te, Entity crasher, double speedSq) {
 *                 //Do whatever in here
 *             }
 *         };
 *
 *         @Nonnull
 *         @Override
 *         public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
 *             return cap == FragileGlassBase.FRAGILECAP ? LazyOptional.of(() -> (T) inst) : LazyOptional.empty();
 *         }
 *     };
 *     evt.addCapability(DataReference.FRAGILE_CAP_LOCATION, iCapProv);
 * }
 *
 * Please note that there are no ways to stipulate the format of the extra data. However, the modder can provide users a
 * file called fragileglassft_tileentities_modname.cfg containing recommended config lines for their TileEntities, and
 * instructions in comments. This file can be put into the config folder as-is and will be parsed as usual.
 * Also note that there is no way to parse or validate the values - this must be taken care of in onCrash.
 */
public interface IFragileCapability {
    /**
     * This method is the final decision on whether a crash behaviour will execute. Even if IBreakCapability#isAbleToBreak always
     * returns true, the crash only happens if told to in onCrash.
     * Whenever this method is called, the projected entity bounding box WILL be inside the 1x1x1 space of a block, BUT
     * not necessarily intersecting the block's bounding box. Implementations will need to check for that if needed.
     */
    void onCrash(BlockState state, TileEntity te, Entity crasher, double speedSq, String[] extraData);
}
