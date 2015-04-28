/**TO DO
 * Add the jsons
 * Decide re. items
 * Glass should break before ents hit it, so they don't lose speed - can't do much about this
 */

package com.fredtargaryen.fragileglass;

import com.fredtargaryen.fragileglass.block.*;
import com.fredtargaryen.fragileglass.proxy.CommonProxy;
import com.fredtargaryen.fragileglass.tileentity.TileEntityGlass;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = DataReference.MODID, version = DataReference.VERSION, name=DataReference.MODNAME)
public class FragileGlassBase
{
	// The instance of your mod that Forge uses.
    @Mod.Instance(value = "ftfragileglass")
    public static FragileGlassBase instance;
    
    //Declare all blocks here
    public static Block fragileGlass;
	public static Block fragilePane;
    public static Block stainedFragileGlass;
    public static Block stainedFragilePane;
	public static Block sugarBlock;
    
    // Says where the client and server 'proxy' code is loaded.
    @SidedProxy(clientSide=DataReference.CLIENTPROXYPATH, serverSide=DataReference.SERVERPROXYPATH)
    public static CommonProxy proxy;
        
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    	fragileGlass = new BlockFragileGlass()
                .setUnlocalizedName("ftfragileglass")
    			.setStepSound(Block.soundTypeGlass);
    	fragilePane = new BlockFragilePane()
    			.setUnlocalizedName("ftfragilepane")
    			.setStepSound(Block.soundTypeGlass);
        stainedFragileGlass = new BlockStainedFragileGlass()
                .setUnlocalizedName("ftstainedfragileglass")
                .setStepSound(Block.soundTypeGlass);
        stainedFragilePane = new BlockStainedFragilePane()
                .setUnlocalizedName("ftstainedfragilepane")
                .setStepSound(Block.soundTypeGlass);
    	sugarBlock = new SugarBlock()
                .setStepSound(Block.soundTypeGravel);

    	//Register blocks
    	GameRegistry.registerBlock(fragileGlass, fragileGlass.getUnlocalizedName().substring(5));
    	GameRegistry.registerBlock(fragilePane, fragilePane.getUnlocalizedName().substring(5));
        GameRegistry.registerBlock(stainedFragileGlass, stainedFragileGlass.getUnlocalizedName().substring(5));
        GameRegistry.registerBlock(stainedFragilePane, stainedFragilePane.getUnlocalizedName().substring(5));
    	GameRegistry.registerBlock(sugarBlock, sugarBlock.getUnlocalizedName().substring(5));
    }
        
    @Mod.EventHandler
    public void load(FMLInitializationEvent event)
    {
    	//Recipes
    	GameRegistry.addRecipe(new ItemStack(sugarBlock, 1), "xxx", "xxx", "xxx",
    			'x', Items.sugar);
    	GameRegistry.addShapelessRecipe(new ItemStack(Items.sugar, 9), new ItemStack(sugarBlock));
    	GameRegistry.addSmelting(sugarBlock, new ItemStack(fragileGlass, 64), 0.1F);
    	GameRegistry.addRecipe(new ItemStack(fragilePane, 16), "xxx", "xxx",
    	        'x', fragileGlass);
        for(int meta = 0; meta < 16; meta++)
        {
            GameRegistry.addRecipe(new ItemStack(stainedFragileGlass, 8, meta), "xxx", "xox", "xxx",
                    'x', new ItemStack(fragileGlass), 'o', new ItemStack(Items.dye, 1, meta));
            GameRegistry.addRecipe(new ItemStack(stainedFragilePane, 16, meta), "xxx", "xxx",
                    'x', new ItemStack(stainedFragileGlass, 1, meta));
        }

        GameRegistry.registerTileEntity(TileEntityGlass.class, "glassTE");

    	proxy.registerRenderers();
    }
        
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    	// Stub Method
    }
}