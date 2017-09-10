package com.fredtargaryen.fragileglass.entity.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class PlayerBreakStorage implements Capability.IStorage<IPlayerBreakCapability> {
    @Nullable
    @Override
    public NBTBase writeNBT(Capability<IPlayerBreakCapability> capability, IPlayerBreakCapability instance, EnumFacing side) {
        return null;
    }

    @Override
    public void readNBT(Capability<IPlayerBreakCapability> capability, IPlayerBreakCapability instance, EnumFacing side, NBTBase nbt) {

    }
}
