package com.fredtargaryen.fragileglass.entity.capability;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class BreakCapStorage implements Capability.IStorage<IBreakCapability> {
    @Nullable
    @Override
    public INBT writeNBT(Capability<IBreakCapability> capability, IBreakCapability instance, Direction side) {
        return null;
    }

    @Override
    public void readNBT(Capability<IBreakCapability> capability, IBreakCapability instance, Direction side, INBT nbt) {

    }
}
