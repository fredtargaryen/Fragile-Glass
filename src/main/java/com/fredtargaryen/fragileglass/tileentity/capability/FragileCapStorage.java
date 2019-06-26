package com.fredtargaryen.fragileglass.tileentity.capability;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class FragileCapStorage implements Capability.IStorage<IFragileCapability> {
    @Nullable
    @Override
    public INBT writeNBT(Capability<IFragileCapability> capability, IFragileCapability instance, Direction side) {
        return null;
    }

    @Override
    public void readNBT(Capability<IFragileCapability> capability, IFragileCapability instance, Direction side, INBT nbt) {

    }
}