/**
 * "Physics" still not perfect
 */

package com.fredtargaryen.fragileglass;

import com.fredtargaryen.fragileglass.block.*;
import com.fredtargaryen.fragileglass.item.ItemBlockStainedFragileGlass;
import com.fredtargaryen.fragileglass.item.ItemBlockStainedFragilePane;
import com.fredtargaryen.fragileglass.proxy.CommonProxy;
import com.fredtargaryen.fragileglass.tileentity.TileEntityFragile;
import com.fredtargaryen.fragileglass.worldgen.PatchGen;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

@Mod(modid = DataReference.MODID, version = DataReference.VERSION, name=DataReference.MODNAME)
public class FragileGlassBase
{
	// The instance of your mod that Forge uses.
    @Mod.Instance(value = "ftfragileglass")
    public static FragileGlassBase instance;

    //Config vars
    public static boolean genThinIce;
    public static int avePatchSize;
    public static int genChance;

    public static final PatchGen patchGen = new PatchGen();

    //Declare all blocks here
    public static Block fragileGlass;
	public static Block fragilePane;
    public static Block stainedFragileGlass;
    public static Block stainedFragilePane;
	public static Block sugarBlock;
    public static Block thinIce;
    public static Block sugarCauldron;
    
    // Says where the client and server 'proxy' code is loaded.
    @SidedProxy(clientSide=DataReference.CLIENTPROXYPATH, serverSide=DataReference.SERVERPROXYPATH)
    public static CommonProxy proxy;
        
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        //CONFIG SETUP
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();
        avePatchSize = config.getInt("avePatchSize", "Worldgen", 5, 4, 10, "Average patch diameter");
        genChance = config.getInt("genChance", "Worldgen", 3, 2, 5, "1 in x chance of patch appearing");
        genThinIce = config.getBoolean("genThinIce", "Worldgen", true, "If true, thin ice patches will generate on frozen bodies of water");
        config.save();

        //BLOCK SETUP
    	fragileGlass = new BlockFragileGlass()
                .setUnlocalizedName("ftfragileglass")
                .setRegistryName("fragile_glass");
    	fragilePane = new BlockFragilePane()
    			.setUnlocalizedName("ftfragilepane")
                .setRegistryName("fragile_pane");
        stainedFragileGlass = new BlockStainedFragileGlass()
                .setUnlocalizedName("ftstainedfragileglass")
                .setRegistryName("fragile_glass_stained");
        stainedFragilePane = new BlockStainedFragilePane()
                .setUnlocalizedName("ftstainedfragilepane")
                .setRegistryName("fragile_pane_stained");
    	sugarBlock = new SugarBlock()
                .setUnlocalizedName("ftsugarblock")
                .setRegistryName("sugar_block");
        thinIce = new BlockThinIce()
                .setUnlocalizedName("ftthinice")
                .setRegistryName("thin_ice");
        sugarCauldron = new BlockSugarCauldron()
                .setUnlocalizedName("ftsugarcauldron")
                .setHardness(5.0F)
                .setResistance(10.0F)
                .setRegistryName("sugar_cauldron");

    	//Register blocks
    	GameRegistry.register(fragileGlass, fragileGlass.getRegistryName());
    	GameRegistry.register(fragilePane, fragilePane.getRegistryName());
        GameRegistry.register(stainedFragileGlass, stainedFragileGlass.getRegistryName());
        GameRegistry.register(stainedFragilePane, stainedFragilePane.getRegistryName());
    	GameRegistry.register(sugarBlock, sugarBlock.getRegistryName());
        GameRegistry.register(thinIce, thinIce.getRegistryName());
        GameRegistry.register(sugarCauldron, sugarCauldron.getRegistryName());

        proxy.doStateMappings();
        OreDictionary.registerOre("blockSugar", new ItemStack(sugarBlock, 1, 1));
    }
        
    @Mod.EventHandler
    public void load(FMLInitializationEvent event)
    {
    	//Recipes
    	GameRegistry.addRecipe(new ItemStack(sugarBlock, 1), "xxx", "xxx", "xxx",
    			'x', Items.sugar);
    	GameRegistry.addShapelessRecipe(new ItemStack(Items.sugar, 9), new ItemStack(sugarBlock));
    	GameRegistry.addShapelessRecipe(new ItemStack(sugarCauldron), Items.sugar, Items.cauldron);
    	GameRegistry.addRecipe(new ItemStack(fragilePane, 16), "xxx", "xxx",
    	        'x', fragileGlass);
        for(int meta = 0; meta < 16; meta++)
        {
            GameRegistry.addRecipe(new ItemStack(stainedFragileGlass, 8, meta), "xxx", "xox", "xxx",
                    'x', new ItemStack(fragileGlass), 'o', new ItemStack(Items.dye, 1, 15 - meta));
            GameRegistry.addRecipe(new ItemStack(stainedFragilePane, 16, meta), "xxx", "xxx",
                    'x', new ItemStack(stainedFragileGlass, 1, meta));
        }

        GameRegistry.registerTileEntity(TileEntityFragile.class, "glassTE");
        if(genThinIce) GameRegistry.registerWorldGenerator(patchGen, 1);

    	proxy.registerRenderers();
        proxy.registerModels();
    }
}