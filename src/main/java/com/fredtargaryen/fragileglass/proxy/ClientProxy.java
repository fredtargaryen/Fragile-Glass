package com.fredtargaryen.fragileglass.proxy;

import com.fredtargaryen.fragileglass.FragileGlassBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ClientProxy extends CommonProxy
{
    @Override
    public void registerRenderers()
    {
    }

    @Override
    public void registerTickHandlers()
    {
    }

    @Override
    public void registerModels()
    {
        //Describes how some blocks should look in the inventory
        ItemModelMesher m = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
        m.register(Item.getItemFromBlock(FragileGlassBase.fragileGlass), 0, new ModelResourceLocation("glass"));
        m.register(Item.getItemFromBlock(FragileGlassBase.fragilePane), 0, new ModelResourceLocation("item/glass_pane"));
    }
}