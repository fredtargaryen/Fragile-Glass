package com.fredtargaryen.fragileglass.tileentity.capability;

import net.minecraft.tileentity.TileEntity;

import java.util.concurrent.Callable;

public class FragileCapFactory implements Callable<IFragileCapability>
{
    @Override
    public IFragileCapability call() throws Exception {
        return new FragileImpl();
    }

    public class FragileImpl implements IFragileCapability
    {
        public void onCrash(TileEntity te)
        {
            te.getWorld().destroyBlock(te.getPos(), false);
        }
    }
}