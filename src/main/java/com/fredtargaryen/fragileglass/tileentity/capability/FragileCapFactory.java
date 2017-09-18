package com.fredtargaryen.fragileglass.tileentity.capability;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
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
        public boolean onCrash(IBlockState state, TileEntity te, Entity crasher, double speed)
        {
            te.getWorld().destroyBlock(te.getPos(), false);
            return false;
        }
    }
}