package com.fredtargaryen.fragileglass.tileentity.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class BlockMadeFragileCapStorage implements Capability.IStorage<IBlockMadeFragileCapability> {
    @Nullable
    @Override
    public NBTBase writeNBT(Capability<IBlockMadeFragileCapability> capability, IBlockMadeFragileCapability instance, EnumFacing side) {
        return null;
    }

    @Override
    public void readNBT(Capability<IBlockMadeFragileCapability> capability, IBlockMadeFragileCapability instance, EnumFacing side, NBTBase nbt) {

    }
}