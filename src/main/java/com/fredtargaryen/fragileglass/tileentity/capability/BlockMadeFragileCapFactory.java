package com.fredtargaryen.fragileglass.tileentity.capability;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.concurrent.Callable;

public class BlockMadeFragileCapFactory implements Callable<IBlockMadeFragileCapability>
{
    @Override
    public IBlockMadeFragileCapability call() throws Exception {
        return new BlockMadeFragileImpl();
    }

    public class BlockMadeFragileImpl implements IBlockMadeFragileCapability {
        private FragilityDataManager.FragilityData fragData;

        @Override
        public void setFragilityData(FragilityDataManager.FragilityData fragData) {
            this.fragData = fragData;
        }

        @Override
        public void onCrash(IBlockState state, TileEntity te, Entity crasher, double speed) {
            FragilityDataManager.FragileBehaviour behaviour = this.fragData.getBehaviour();
            if(behaviour == FragilityDataManager.FragileBehaviour.GLASS) {
                if (speed > this.fragData.getBreakSpeed()) {
                    te.getWorld().destroyBlock(te.getPos(), false);
                }
            }
            else if(behaviour == FragilityDataManager.FragileBehaviour.STONE) {
                if (speed > this.fragData.getBreakSpeed()) {
                    World w = te.getWorld();
                    BlockPos tilePos = te.getPos();
                    w.scheduleUpdate(tilePos, w.getBlockState(tilePos).getBlock(), this.fragData.getUpdateDelay());
                }
            }
        }
    }
}