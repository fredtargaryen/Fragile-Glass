/**
 * Fix physics for players
 * On client, players send packets of their new position to the server. The server then adjusts the position based on the
 * packets and its own logic, but motion values are not set server-side. The server does not update players by itself.
 *
 * Method tried                                                             Result
 * Get motion from client and send break packets                            Players are stopped then fall
 * Remove collision boxes from list in GetCollisionBoxesEvent, then         Players are not stopped, but the list in
 * break blocks in PlayerTickEvent                                          GetCollisionBoxesEvent only
 *                                                                          includes some ground blocks, there is
 *                                                                          still shaking due to "ex-blocks", and non-
 *                                                                          downward movement is bad
 */
package com.fredtargaryen.fragileglass;

import com.fredtargaryen.fragileglass.block.*;
import com.fredtargaryen.fragileglass.entity.capability.*;
import com.fredtargaryen.fragileglass.item.ItemBlockStainedFragileGlass;
import com.fredtargaryen.fragileglass.item.ItemBlockStainedFragilePane;
import com.fredtargaryen.fragileglass.network.PacketHandler;
import com.fredtargaryen.fragileglass.proxy.CommonProxy;
import com.fredtargaryen.fragileglass.tileentity.TileEntityFragile;
import com.fredtargaryen.fragileglass.tileentity.capability.FragileCapFactory;
import com.fredtargaryen.fragileglass.tileentity.capability.FragileCapStorage;
import com.fredtargaryen.fragileglass.tileentity.capability.IFragileCapability;
import com.fredtargaryen.fragileglass.world.BreakSystem;
import com.fredtargaryen.fragileglass.worldgen.PatchGen;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Mod(modid = DataReference.MODID, version = DataReference.VERSION, name=DataReference.MODNAME)
@Mod.EventBusSubscriber
public class FragileGlassBase
{
	// The instance of your mod that Forge uses.
    @Mod.Instance(DataReference.MODID)
    public static FragileGlassBase instance;

    public static ArrayList<Item> iceBlocks;

    //Config vars
    private static boolean genThinIce;
    public static int avePatchSize;
    public static int genChance;

    private static final PatchGen patchGen = new PatchGen();

    public static BreakSystem breakSystem;

    //Declare all blocks here
    public static Block fragileGlass;
	public static Block fragilePane;
    public static Block stainedFragileGlass;
    public static Block stainedFragilePane;
	public static Block sugarBlock;
    public static Block thinIce;
    public static Block sugarCauldron;

    //Declare all items here
    private static Item iFragileGlass;
    private static Item iFragilePane;
    private static Item iStainedFragileGlass;
    private static Item iStainedFragilePane;
    private static Item iSugarBlock;
    private static Item iThinIce;
    private static Item iSugarCauldron;

    // Says where the client and server 'proxy' code is loaded.
    @SidedProxy(clientSide=DataReference.CLIENTPROXYPATH, serverSide=DataReference.SERVERPROXYPATH)
    private static CommonProxy proxy;
        
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        PacketHandler.init();

        //Capability
        CapabilityManager.INSTANCE.register(IBreakCapability.class, new BreakCapStorage(), new BreakCapFactory());
        CapabilityManager.INSTANCE.register(IPlayerBreakCapability.class, new PlayerBreakStorage(), new PlayerBreakFactory());
        CapabilityManager.INSTANCE.register(IFragileCapability.class, new FragileCapStorage(), new FragileCapFactory());
        MinecraftForge.EVENT_BUS.register(this);

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
                .setRegistryName("ftfragileglass");
    	fragilePane = new BlockFragilePane()
    			.setUnlocalizedName("ftfragilepane")
                .setRegistryName("ftfragilepane");
        stainedFragileGlass = new BlockStainedFragileGlass()
                .setUnlocalizedName("ftstainedfragileglass")
                .setRegistryName("ftstainedfragileglass");
        stainedFragilePane = new BlockStainedFragilePane()
                .setUnlocalizedName("ftstainedfragilepane")
                .setRegistryName("ftstainedfragilepane");
    	sugarBlock = new SugarBlock()
                .setUnlocalizedName("ftsugarblock")
                .setRegistryName("ftsugarblock");
        thinIce = new BlockThinIce()
                .setUnlocalizedName("ftthinice")
                .setRegistryName("ftthinice");
        sugarCauldron = new BlockSugarCauldron()
                .setUnlocalizedName("ftsugarcauldron")
                .setHardness(5.0F)
                .setResistance(10.0F)
                .setRegistryName("ftsugarcauldron");

        //ITEM SETUP
        iFragileGlass = new ItemBlock(fragileGlass)
                .setRegistryName("ftfragileglass");
        iFragilePane = new ItemBlock(fragilePane)
                .setRegistryName("ftfragilepane");
        iStainedFragileGlass = new ItemBlockStainedFragileGlass(stainedFragileGlass)
                .setRegistryName("ftstainedfragileglass");
        iStainedFragilePane = new ItemBlockStainedFragilePane(stainedFragilePane)
                .setRegistryName("ftstainedfragilepane");
        iSugarBlock = new ItemBlock(sugarBlock)
                .setRegistryName("ftsugarblock");
        iThinIce = new ItemBlock(thinIce)
                .setRegistryName("ftthinice");
        iSugarCauldron = new ItemBlock(sugarCauldron)
                .setRegistryName("ftsugarcauldron");

        proxy.doStateMappings();
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        event.getRegistry().registerAll(fragileGlass, fragilePane, stainedFragileGlass, stainedFragilePane, sugarBlock, thinIce, sugarCauldron);
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().registerAll(iFragileGlass, iFragilePane, iStainedFragileGlass, iStainedFragilePane, iSugarBlock, iThinIce, iSugarCauldron);
    }
        
    @Mod.EventHandler
    public void load(FMLInitializationEvent event)
    {
    	GameRegistry.registerTileEntity(TileEntityFragile.class, "glassTE");

        OreDictionary.registerOre("blockSugar", sugarBlock);

        if(genThinIce) GameRegistry.registerWorldGenerator(patchGen, 1);

        proxy.registerModels();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        iceBlocks = new ArrayList<>();
        iceBlocks.addAll(OreDictionary.getOres("blockIce").stream().map(ItemStack::getItem).collect(Collectors.toList()));
    }

    ////////////////
    //CAPABILITIES//
    ////////////////
    /**
     * For entities that are able to break fragile blocks.
     */
    @CapabilityInject(IBreakCapability.class)
    public static Capability<IBreakCapability> BREAKCAP = null;
    @CapabilityInject(IBreakCapability.class)
    public static Capability<IBreakCapability> PLAYERBREAKCAP = null;
    @CapabilityInject(IFragileCapability.class)
    public static Capability<IFragileCapability> FRAGILECAP = null;

    @SubscribeEvent
    public void onBreakerConstruct(AttachCapabilitiesEvent<Entity> evt)
    {
        Entity e = evt.getObject();
        if(e instanceof EntityPlayer)
        {
            //May need to change
            if(!e.world.isRemote)
            {
                evt.addCapability(DataReference.PLAYER_BREAK_LOCATION,
                        new ICapabilityProvider() {
                            IBreakCapability inst = PLAYERBREAKCAP.getDefaultInstance();

                            @Override
                            public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
                                return capability == PLAYERBREAKCAP;
                            }

                            @Override
                            public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
                                return capability == PLAYERBREAKCAP ? PLAYERBREAKCAP.<T>cast(inst) : null;
                            }
                        }
                );
            }
        }
        else
        {
            if(!e.world.isRemote)
            {
                if(e instanceof EntityLivingBase
                        || e instanceof EntityArrow
                        || e instanceof EntityFireball
                        || e instanceof EntityMinecart
                        || e instanceof EntityFireworkRocket
                        || e instanceof EntityBoat
                        || e instanceof EntityTNTPrimed
                        || e instanceof EntityFallingBlock) {
                    ICapabilityProvider icp = new ICapabilityProvider() {
                        IBreakCapability inst = BREAKCAP.getDefaultInstance();

                        @Override
                        public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
                            return capability == BREAKCAP;
                        }

                        @Override
                        public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
                            return capability == BREAKCAP ? BREAKCAP.<T>cast(inst) : null;
                        }
                    };
                    evt.addCapability(DataReference.BREAK_LOCATION, icp);
                }
            }
        }
    }

    @SubscribeEvent
    public void onTileConstructed(AttachCapabilitiesEvent<TileEntity> evt)
    {
        TileEntity te = evt.getObject();
        if(te instanceof TileEntityFragile)
        {
            evt.addCapability(DataReference.FRAGILE_CAP_LOCATION,
                    new ICapabilityProvider() {
                        IFragileCapability inst = FRAGILECAP.getDefaultInstance();

                        @Override
                        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
                            return capability == FRAGILECAP;
                        }

                        @Nullable
                        @Override
                        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
                            return capability == FRAGILECAP ? FRAGILECAP.<T>cast(inst) : null;
                        }
                    }
            );
        }
    }

    @SubscribeEvent
    public void registerBreakerCap(EntityJoinWorldEvent ejwe)
    {
        Entity e = ejwe.getEntity();
        if(e.hasCapability(PLAYERBREAKCAP, null))
        {
            MinecraftForge.EVENT_BUS.register(e.getCapability(PLAYERBREAKCAP, null));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void loadSystem(WorldEvent.Load event)
    {
        World w = event.getWorld();
        if(!w.isRemote)
        {
            breakSystem = new BreakSystem();
            breakSystem.init(w);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void stopSystem(WorldEvent.Unload event)
    {
        World w = event.getWorld();
        if(!w.isRemote)
        {
            if(breakSystem != null) {
                breakSystem.end(w);
            }
        }
    }
}