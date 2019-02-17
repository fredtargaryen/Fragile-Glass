package com.fredtargaryen.fragileglass.proxy;

import com.fredtargaryen.fragileglass.DataReference;
import com.fredtargaryen.fragileglass.FragileGlassBase;
import com.fredtargaryen.fragileglass.client.renderer.block.VanillaOnlyStateMapper;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;

public class ClientProxy implements IProxy
{
    @Override
    public void registerModels()
    {
        //Describes how some blocks should look in the inventory
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(FragileGlassBase.weakStone), 0, new ModelResourceLocation(DataReference.MODID+":weakstone"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(FragileGlassBase.sugarBlock), 0, new ModelResourceLocation(DataReference.MODID+":sugarblock"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(FragileGlassBase.sugarCauldron), 0, new ModelResourceLocation(DataReference.MODID+":sugarcauldron", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(FragileGlassBase.fragileGlass), 0, new ModelResourceLocation("glass"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(FragileGlassBase.stainedFragileGlass), 0, new ModelResourceLocation("white_stained_glass"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(FragileGlassBase.stainedFragileGlass), 1, new ModelResourceLocation("orange_stained_glass"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(FragileGlassBase.stainedFragileGlass), 2, new ModelResourceLocation("magenta_stained_glass"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(FragileGlassBase.stainedFragileGlass), 3, new ModelResourceLocation("light_blue_stained_glass"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(FragileGlassBase.stainedFragileGlass), 4, new ModelResourceLocation("yellow_stained_glass"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(FragileGlassBase.stainedFragileGlass), 5, new ModelResourceLocation("lime_stained_glass"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(FragileGlassBase.stainedFragileGlass), 6, new ModelResourceLocation("pink_stained_glass"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(FragileGlassBase.stainedFragileGlass), 7, new ModelResourceLocation("gray_stained_glass"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(FragileGlassBase.stainedFragileGlass), 8, new ModelResourceLocation("silver_stained_glass"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(FragileGlassBase.stainedFragileGlass), 9, new ModelResourceLocation("cyan_stained_glass"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(FragileGlassBase.stainedFragileGlass), 10, new ModelResourceLocation("purple_stained_glass"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(FragileGlassBase.stainedFragileGlass), 11, new ModelResourceLocation("blue_stained_glass"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(FragileGlassBase.stainedFragileGlass), 12, new ModelResourceLocation("brown_stained_glass"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(FragileGlassBase.stainedFragileGlass), 13, new ModelResourceLocation("green_stained_glass"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(FragileGlassBase.stainedFragileGlass), 14, new ModelResourceLocation("red_stained_glass"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(FragileGlassBase.stainedFragileGlass), 15, new ModelResourceLocation("black_stained_glass"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(FragileGlassBase.fragilePane), 0, new ModelResourceLocation("glass_pane", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(FragileGlassBase.stainedFragilePane), 0, new ModelResourceLocation("white_stained_glass_pane", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(FragileGlassBase.stainedFragilePane), 1, new ModelResourceLocation("orange_stained_glass_pane", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(FragileGlassBase.stainedFragilePane), 2, new ModelResourceLocation("magenta_stained_glass_pane", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(FragileGlassBase.stainedFragilePane), 3, new ModelResourceLocation("light_blue_stained_glass_pane", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(FragileGlassBase.stainedFragilePane), 4, new ModelResourceLocation("yellow_stained_glass_pane", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(FragileGlassBase.stainedFragilePane), 5, new ModelResourceLocation("lime_stained_glass_pane", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(FragileGlassBase.stainedFragilePane), 6, new ModelResourceLocation("pink_stained_glass_pane", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(FragileGlassBase.stainedFragilePane), 7, new ModelResourceLocation("gray_stained_glass_pane", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(FragileGlassBase.stainedFragilePane), 8, new ModelResourceLocation("silver_stained_glass_pane", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(FragileGlassBase.stainedFragilePane), 9, new ModelResourceLocation("cyan_stained_glass_pane", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(FragileGlassBase.stainedFragilePane), 10, new ModelResourceLocation("purple_stained_glass_pane", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(FragileGlassBase.stainedFragilePane), 11, new ModelResourceLocation("blue_stained_glass_pane", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(FragileGlassBase.stainedFragilePane), 12, new ModelResourceLocation("brown_stained_glass_pane", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(FragileGlassBase.stainedFragilePane), 13, new ModelResourceLocation("green_stained_glass_pane", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(FragileGlassBase.stainedFragilePane), 14, new ModelResourceLocation("red_stained_glass_pane", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(FragileGlassBase.stainedFragilePane), 15, new ModelResourceLocation("black_stained_glass_pane", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(FragileGlassBase.thinIce), 0, new ModelResourceLocation(DataReference.MODID+":thinice"));
    }

    public void doStateMappings()
    {
        //Creates all the coloured BlockStates for stained blocks
        ModelLoader.setCustomStateMapper(FragileGlassBase.stainedFragileGlass, (new VanillaOnlyStateMapper.Builder()).withColour().withSuffix("_stained_glass").build());
        ModelLoader.setCustomStateMapper(FragileGlassBase.stainedFragilePane, (new VanillaOnlyStateMapper.Builder()).withColour().withSuffix("_stained_glass_pane").build());
    }
}