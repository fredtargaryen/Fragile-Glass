package com.fredtargaryen.fragileglass.entity.capability;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class PlayerBreakStorage implements Capability.IStorage<IPlayerBreakCapability> {
    @Nullable
    @Override
    public INBT writeNBT(Capability<IPlayerBreakCapability> capability, IPlayerBreakCapability instance, Direction side) {
        return null;
    }

    @Override
    public void readNBT(Capability<IPlayerBreakCapability> capability, IPlayerBreakCapability instance, Direction side, INBT nbt) {

    }
}
