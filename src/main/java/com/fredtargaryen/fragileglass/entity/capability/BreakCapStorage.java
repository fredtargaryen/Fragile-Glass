package com.fredtargaryen.fragileglass.entity.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class BreakCapStorage implements Capability.IStorage<IBreakCapability>
{
    @Nullable
    @Override
    public NBTBase writeNBT(Capability<IBreakCapability> capability, IBreakCapability instance, EnumFacing side) {
        return null;
    }

    @Override
    public void readNBT(Capability<IBreakCapability> capability, IBreakCapability instance, EnumFacing side, NBTBase nbt) {

    }
}