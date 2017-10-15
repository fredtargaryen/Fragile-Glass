package com.fredtargaryen.fragileglass.tileentity.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class FragileCapStorage implements Capability.IStorage<IFragileCapability> {
    @Nullable
    @Override
    public NBTBase writeNBT(Capability<IFragileCapability> capability, IFragileCapability instance, EnumFacing side) {
        return null;
    }

    @Override
    public void readNBT(Capability<IFragileCapability> capability, IFragileCapability instance, EnumFacing side, NBTBase nbt) {

    }
}