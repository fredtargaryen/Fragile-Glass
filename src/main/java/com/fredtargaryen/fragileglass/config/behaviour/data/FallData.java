package com.fredtargaryen.fragileglass.config.behaviour.data;

import com.fredtargaryen.fragileglass.config.behaviour.configloader.ConfigLoader;
import com.fredtargaryen.fragileglass.config.behaviour.datamanager.DataManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class FallData extends FragilityData {
    public FallData(double breakSpeed) {
        super(breakSpeed);
    }

    @Override
    public FragilityData.FragileBehaviour getBehaviour() {
        return FragilityData.FragileBehaviour.FALL;
    }

    @Override
    public void parseExtraData(@Nullable BlockState state, ConfigLoader cl, String[] extraData) throws FragilityDataParseException {
        this.lengthCheck(extraData, 0);
    }

    @Override
    public void onCrash(World world, BlockState state, @Nullable TileEntity te, BlockPos pos, @Nullable Entity crasher, double speedSq) {
        if (speedSq > this.breakSpeedSq) {
            if (FallingBlock.canFallThrough(world.getBlockState(pos.down()))) {
                FallingBlockEntity fallingBlock = new FallingBlockEntity(world, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, world.getBlockState(pos));
                if(te != null) {
                    fallingBlock.tileEntityData = te.write(new CompoundNBT());
                }
                world.addEntity(fallingBlock);
            }
        }
    }
}
