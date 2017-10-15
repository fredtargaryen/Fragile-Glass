package com.fredtargaryen.fragileglass;

import com.fredtargaryen.fragileglass.block.*;
import com.fredtargaryen.fragileglass.entity.capability.*;
import com.fredtargaryen.fragileglass.item.ItemBlockStainedFragileGlass;
import com.fredtargaryen.fragileglass.item.ItemBlockStainedFragilePane;
import com.fredtargaryen.fragileglass.network.MessageBreakerMovement;
import com.fredtargaryen.fragileglass.network.PacketHandler;
import com.fredtargaryen.fragileglass.proxy.CommonProxy;
import com.fredtargaryen.fragileglass.tileentity.TileEntityFragile;
import com.fredtargaryen.fragileglass.tileentity.TileEntityFragileGlass;
import com.fredtargaryen.fragileglass.tileentity.TileEntityThinIce;
import com.fredtargaryen.fragileglass.tileentity.TileEntityWeakStone;
import com.fredtargaryen.fragileglass.tileentity.capability.FragileCapFactory;
import com.fredtargaryen.fragileglass.tileentity.capability.FragileCapStorage;
import com.fredtargaryen.fragileglass.tileentity.capability.IFragileCapability;
import com.fredtargaryen.fragileglass.world.BreakSystem;
import com.fredtargaryen.fragileglass.worldgen.PatchGen;
import com.fredtargaryen.fragileglass.worldgen.PatchGenIce;
import com.fredtargaryen.fragileglass.worldgen.PatchGenStone;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
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
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;

@Mod(modid = DataReference.MODID, version = DataReference.VERSION, name=DataReference.MODNAME)
public class FragileGlassBase
{
	// The instance of your mod that Forge uses.
    @Mod.Instance(value = "ftfragileglass")
    public static FragileGlassBase instance;

    public static ArrayList<Item> iceBlocks;

    //Config vars
    public static boolean genThinIce;
    public static int avePatchSizeIce;
    public static int genChanceIce;
    private static boolean genWeakStone;
    public static int avePatchSizeStone;
    public static int genChanceStone;

    public static PatchGen patchGenIce;
    public static PatchGen patchGenStone;

    public static BreakSystem breakSystem;

    //Declare all blocks here
    public static Block fragileGlass;
	public static Block fragilePane;
    public static Block stainedFragileGlass;
    public static Block stainedFragilePane;
	public static Block sugarBlock;
    public static Block thinIce;
    public static Block sugarCauldron;
    public static Block weakStone;

    //Declare all items here
    public static Item iFragileGlass;
    public static Item iFragilePane;
    public static Item iStainedFragileGlass;
    public static Item iStainedFragilePane;
    public static Item iSugarBlock;
    public static Item iThinIce;
    public static Item iSugarCauldron;
    public static Item iWeakStone;
    
    // Says where the client and server 'proxy' code is loaded.
    @SidedProxy(clientSide=DataReference.CLIENTPROXYPATH, serverSide=DataReference.SERVERPROXYPATH)
    public static CommonProxy proxy;
        
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
        genThinIce = config.getBoolean("genThinIce", "Worldgen - Thin Ice", true, "If true, thin ice patches will generate on frozen bodies of water");
        avePatchSizeIce = config.getInt("avePatchSizeIce", "Worldgen - Thin Ice", 5, 1, 14, "Average patch diameter");
        genChanceIce = config.getInt("genChanceIce", "Worldgen - Thin Ice", 3, 1, 5, "1 in x chance of patch appearing");
        genWeakStone = config.getBoolean("genWeakStone", "Worldgen - Weak Stone", false, "If true, weak stone patches will generate. Expect falls into lava!");
        avePatchSizeStone = config.getInt("avePatchSizeStone", "Worldgen - Weak Stone", 5, 1, 14, "Average patch diameter");
        genChanceStone = config.getInt("genChanceStone", "Worldgen - Weak Stone", 3, 1, 5, "1 in x chance of patch appearing");
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
        weakStone = new BlockWeakStone()
                .setUnlocalizedName("ftweakstone")
                .setRegistryName("ftweakstone");

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
        iWeakStone = new ItemBlock(weakStone)
                .setRegistryName("ftweakstone");

    	//Register blocks and items
    	GameRegistry.register(fragileGlass);
        GameRegistry.register(iFragileGlass);

    	GameRegistry.register(fragilePane);
        GameRegistry.register(iFragilePane);

        GameRegistry.register(stainedFragileGlass);
        GameRegistry.register(iStainedFragileGlass);

        GameRegistry.register(stainedFragilePane);
        GameRegistry.register(iStainedFragilePane);

    	GameRegistry.register(sugarBlock);
        GameRegistry.register(iSugarBlock);

        GameRegistry.register(thinIce);
        GameRegistry.register(iThinIce);

        GameRegistry.register(sugarCauldron);
        GameRegistry.register(iSugarCauldron);

        GameRegistry.register(weakStone);
        GameRegistry.register(iWeakStone);

        OreDictionary.registerOre("blockSugar", sugarBlock);
        proxy.registerModels();
        proxy.doStateMappings();
    }
        
    @Mod.EventHandler
    public void load(FMLInitializationEvent event)
    {
    	//Recipes
    	GameRegistry.addRecipe(new ItemStack(sugarBlock, 1), "xxx", "xxx", "xxx",
    			'x', Items.SUGAR);
    	GameRegistry.addShapelessRecipe(new ItemStack(Items.SUGAR, 9), new ItemStack(sugarBlock));
    	GameRegistry.addShapelessRecipe(new ItemStack(sugarCauldron), Items.SUGAR, Items.CAULDRON);
    	GameRegistry.addRecipe(new ItemStack(fragilePane, 16), "xxx", "xxx",
    	        'x', fragileGlass);
    	GameRegistry.addRecipe(new ItemStack(weakStone, 2), "xx", "xx",
                'x', Blocks.STONE_SLAB);
        for(int meta = 0; meta < 16; meta++)
        {
            GameRegistry.addRecipe(new ItemStack(stainedFragileGlass, 8, meta), "xxx", "xox", "xxx",
                    'x', new ItemStack(fragileGlass), 'o', new ItemStack(Items.DYE, 1, 15 - meta));
            GameRegistry.addRecipe(new ItemStack(stainedFragilePane, 16, meta), "xxx", "xxx",
                    'x', new ItemStack(stainedFragileGlass, 1, meta));
        }

        GameRegistry.registerTileEntity(TileEntityFragileGlass.class, "TEFG");
        GameRegistry.registerTileEntity(TileEntityThinIce.class, "TETI");
        GameRegistry.registerTileEntity(TileEntityWeakStone.class, "TEWS");

        if(genThinIce)
        {
            patchGenIce = new PatchGenIce();
            GameRegistry.registerWorldGenerator(patchGenIce, 1);
        }
        if(genWeakStone)
        {
            patchGenStone = new PatchGenStone();
            GameRegistry.registerWorldGenerator(patchGenStone, 1);
        }

        //proxy.registerModels();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        iceBlocks = new ArrayList<Item>();
        for(ItemStack is : OreDictionary.getOres("blockIce")) {
            iceBlocks.add(is.getItem());
        }
    }

    ////////////////
    //CAPABILITIES//
    ////////////////
    /**
     * For entities that are able to break fragile blocks.
     */
    @CapabilityInject(IBreakCapability.class)
    public static Capability<IBreakCapability> BREAKCAP = null;
    @CapabilityInject(IPlayerBreakCapability.class)
    public static Capability<IPlayerBreakCapability> PLAYERBREAKCAP = null;
    @CapabilityInject(IFragileCapability.class)
    public static Capability<IFragileCapability> FRAGILECAP = null;

    @SubscribeEvent
    public void onBreakerConstruct(AttachCapabilitiesEvent<Entity> evt) {
        final Entity e = evt.getObject();
        if (e.world != null) {
            if (e.world.isRemote) {
                if (e instanceof EntityPlayer) {
                    MinecraftForge.EVENT_BUS.register(new Object() {
                        private EntityPlayer ep = (EntityPlayer) e;
                        private double lastSpeed;

                        @SubscribeEvent(priority = EventPriority.HIGHEST)
                        public void speedUpdate(TickEvent.ClientTickEvent event) {
                            if (event.phase == TickEvent.Phase.START) {
                                double speed = Math.sqrt(ep.motionX * ep.motionX + ep.motionY * ep.motionY + ep.motionZ * ep.motionZ);
                                if (Math.abs(speed - this.lastSpeed) > 0.01) {
                                    MessageBreakerMovement mbm = new MessageBreakerMovement();
                                    mbm.motionx = ep.motionX;
                                    mbm.motiony = ep.motionY;
                                    mbm.motionz = ep.motionZ;
                                    mbm.speed = speed;
                                    PacketHandler.INSTANCE.sendToServer(mbm);
                                    this.lastSpeed = speed;
                                }
                                if (ep.isDead) {
                                    MinecraftForge.EVENT_BUS.unregister(this);
                                }
                            }
                        }

                        @SubscribeEvent
                        public void killObject(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
                            MinecraftForge.EVENT_BUS.unregister(this);
                        }
                    });
                }
            } else {
                if (e instanceof EntityPlayer) {
                    evt.addCapability(DataReference.PLAYER_BREAK_LOCATION,
                            new ICapabilityProvider() {
                                IPlayerBreakCapability inst = PLAYERBREAKCAP.getDefaultInstance();

                                @Override
                                public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
                                    return capability == PLAYERBREAKCAP || capability == BREAKCAP;
                                }

                                @Override
                                public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
                                    if (capability == PLAYERBREAKCAP || capability == BREAKCAP) {
                                        return PLAYERBREAKCAP.<T>cast(inst);
                                    }
                                    return null;
                                }
                            }
                    );
                } else {
                    if (e instanceof EntityLivingBase
                            || e instanceof EntityArrow
                            || e instanceof EntityFireball
                            || e instanceof EntityMinecart
                            || e instanceof EntityFireworkRocket
                            || e instanceof EntityBoat
                            || e instanceof EntityTNTPrimed
                            || e instanceof EntityFallingBlock) {
                        evt.addCapability(DataReference.BREAK_LOCATION,
                                new ICapabilityProvider() {

                                    IBreakCapability inst = BREAKCAP.getDefaultInstance();

                                    @Override
                                    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
                                        return capability == BREAKCAP;
                                    }

                                    @Override
                                    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
                                        return capability == FragileGlassBase.BREAKCAP ? FragileGlassBase.BREAKCAP.<T>cast(inst) : null;
                                    }
                                });
                    }
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
            //Now easy to fall through when walking. #SneakOrSink
            if(te instanceof TileEntityThinIce) {
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
            else if(te instanceof TileEntityFragileGlass)
            {
                evt.addCapability(DataReference.FRAGILE_CAP_LOCATION,
                        new ICapabilityProvider() {
                            IFragileCapability inst = new IFragileCapability() {
                                @Override
                                public void onCrash(IBlockState state, TileEntity te, Entity crasher, double speed) {
                                    if(speed > DataReference.PLAYER_SPRINT_SPEED)
                                    {
                                        te.getWorld().destroyBlock(te.getPos(), false);
                                    }
                                }
                            };

                            @Override
                            public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
                                return capability == FRAGILECAP;
                            }

                            @Nullable
                            @Override
                            public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
                                return capability == FRAGILECAP ? FRAGILECAP.<T>cast(inst) : null;
                            }
                        });
            }
            else if(te instanceof TileEntityWeakStone)
            {
                evt.addCapability(DataReference.FRAGILE_CAP_LOCATION,
                        new ICapabilityProvider() {
                            IFragileCapability inst = new IFragileCapability() {
                                @Override
                                public void onCrash(IBlockState state, TileEntity te, Entity crasher, double speed) {
                                    World w = te.getWorld();
                                    w.scheduleUpdate(te.getPos(), FragileGlassBase.weakStone, FragileGlassBase.weakStone.tickRate(w));
                                }
                            };
                            @Override
                            public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
                                return capability == FRAGILECAP;
                            }

                            @Nullable
                            @Override
                            public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
                                return capability == FRAGILECAP ? FRAGILECAP.<T>cast(inst) : null;
                            }
                        });
            }
        }
    }

    @SubscribeEvent
    public void initPlayerBreakerCap(EntityJoinWorldEvent ejwe)
    {
        Entity e = ejwe.getEntity();
        if(e.hasCapability(PLAYERBREAKCAP, null))
        {
            e.getCapability(PLAYERBREAKCAP, null).init(e);
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