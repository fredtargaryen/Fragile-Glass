package com.fredtargaryen.fragileglass.client.renderer.block;

import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;

import static net.minecraft.block.BlockColored.COLOR;

@SideOnly(Side.CLIENT)
public class VanillaOnlyStateMapper extends StateMapperBase
{
    private final IProperty<?> name;
    private final String prefix;
    private final String suffix;

    private VanillaOnlyStateMapper(IProperty<?> name, String prefix, String suffix)
    {
        this.name = name;
        this.prefix = prefix;
        this.suffix = suffix;
    }

    protected ModelResourceLocation getModelResourceLocation(IBlockState state)
    {
        Map<IProperty<?>, Comparable<?>> map = Maps.newLinkedHashMap(state.getProperties());
        String s;

        if (this.name == null)
        {
            s = this.prefix+(Block.REGISTRY.getNameForObject(state.getBlock())).toString()+this.suffix;
        }
        else
        {
            s = "minecraft:"+this.prefix+((IProperty)this.name).getName(map.remove(this.name))+this.suffix;
        }

        return new ModelResourceLocation(s, this.getPropertyString(map));
    }

    @SideOnly(Side.CLIENT)
    public static class Builder
    {
        private IProperty<?> name;
        private String prefix;
        private String suffix;

        public VanillaOnlyStateMapper.Builder withColour()
        {
            this.name = COLOR;
            return this;
        }

        public VanillaOnlyStateMapper.Builder withPrefix(String prefix)
        {
            this.prefix = prefix;
            return this;
        }

        public VanillaOnlyStateMapper.Builder withSuffix(String builderSuffixIn)
        {
            this.suffix = builderSuffixIn;
            return this;
        }

        public VanillaOnlyStateMapper build()
        {
            return new VanillaOnlyStateMapper(this.name,
                    this.prefix == null ? "" : this.prefix,
                    this.suffix == null ? "" : this.suffix);
        }
    }
}
