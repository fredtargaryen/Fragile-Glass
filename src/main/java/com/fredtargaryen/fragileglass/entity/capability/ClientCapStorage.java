package com.fredtargaryen.fragileglass.entity.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class ClientCapStorage implements Capability.IStorage<IClientCanBreakCapability> {
    @Nullable
    @Override
    public NBTBase writeNBT(Capability<IClientCanBreakCapability> capability, IClientCanBreakCapability instance, EnumFacing side) {
        return null;
    }

    @Override
    public void readNBT(Capability<IClientCanBreakCapability> capability, IClientCanBreakCapability instance, EnumFacing side, NBTBase nbt) {

    }
}
