package com.fredtargaryen.fragileglass.client.renderer.block;

import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class VanillaOnlyStateMapper extends StateMapperBase
{
    private final IProperty<?> name;
    private final String suffix;

    private VanillaOnlyStateMapper(IProperty<?> name, String suffix)
    {
        this.name = name;
        this.suffix = suffix;
    }

    protected ModelResourceLocation getModelResourceLocation(IBlockState state)
    {
        Map<IProperty<?>, Comparable<?>> map = Maps.newLinkedHashMap(state.getProperties());
        String s;

        if (this.name == null)
        {
            s = (Block.REGISTRY.getNameForObject(state.getBlock())).toString();
        }
        else
        {
            s = "minecraft:"+((IProperty)this.name).getName(map.remove(this.name));
        }

        if (this.suffix != null)
        {
            s = s + this.suffix;
        }

        return new ModelResourceLocation(s, this.getPropertyString(map));
    }

    @SideOnly(Side.CLIENT)
    public static class Builder
    {
        private IProperty<?> name;
        private String suffix;

        public VanillaOnlyStateMapper.Builder withName(IProperty<?> builderPropertyIn)
        {
            this.name = builderPropertyIn;
            return this;
        }

        public VanillaOnlyStateMapper.Builder withSuffix(String builderSuffixIn)
        {
            this.suffix = builderSuffixIn;
            return this;
        }

        public VanillaOnlyStateMapper build()
        {
            return new VanillaOnlyStateMapper(this.name, this.suffix);
        }
    }
}
