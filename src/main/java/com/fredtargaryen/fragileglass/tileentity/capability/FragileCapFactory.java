package com.fredtargaryen.fragileglass.tileentity.capability;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.concurrent.Callable;

public class FragileCapFactory implements Callable<IFragileCapability>
{
    @Override
    public IFragileCapability call() throws Exception {
        return new FragileImpl();
    }

    public class FragileImpl implements IFragileCapability {
        //Breaks the block if the entity is currently able to break blocks. Enough for most blocks.
        //Effectively the default behaviour of Thin Ice. Use a speed check if you want the block to be tougher.
        public void onCrash(BlockState state, TileEntity te, Entity crasher, double speed) {
            World w = te.getWorld();
            if(w != null) w.destroyBlock(te.getPos(), true);
        }
    }
}