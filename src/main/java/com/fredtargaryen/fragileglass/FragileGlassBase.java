/**
 * TODO
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
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
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
                .setUnlocalizedName("ftsugarblock")
                .setStepSound(Block.soundTypeSand);
        thinIce = new BlockThinIce()
                .setUnlocalizedName("ftthinice")
                .setStepSound(Block.soundTypeGlass);
        sugarCauldron = new BlockSugarCauldron()
                .setUnlocalizedName("ftsugarcauldron")
                .setHardness(5.0F)
                .setResistance(10.0F)
                .setStepSound(Block.soundTypeMetal);

    	//Register blocks
    	GameRegistry.registerBlock(fragileGlass, fragileGlass.getUnlocalizedName().substring(5));
    	GameRegistry.registerBlock(fragilePane, fragilePane.getUnlocalizedName().substring(5));
        GameRegistry.registerBlock(stainedFragileGlass, ItemBlockStainedFragileGlass.class, stainedFragileGlass.getUnlocalizedName().substring(5));
        GameRegistry.registerBlock(stainedFragilePane, ItemBlockStainedFragilePane.class, stainedFragilePane.getUnlocalizedName().substring(5));
    	GameRegistry.registerBlock(sugarBlock, sugarBlock.getUnlocalizedName().substring(5));
        GameRegistry.registerBlock(thinIce, thinIce.getUnlocalizedName().substring(5));
        GameRegistry.registerBlock(sugarCauldron, sugarCauldron.getUnlocalizedName().substring(5));

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