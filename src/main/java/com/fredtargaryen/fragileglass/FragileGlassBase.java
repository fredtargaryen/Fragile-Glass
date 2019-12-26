package com.fredtargaryen.fragileglass;

import com.fredtargaryen.fragileglass.block.*;
import com.fredtargaryen.fragileglass.entity.capability.*;
import com.fredtargaryen.fragileglass.item.ItemBlockStainedFragileGlass;
import com.fredtargaryen.fragileglass.item.ItemBlockStainedFragilePane;
import com.fredtargaryen.fragileglass.network.MessageBreakerMovement;
import com.fredtargaryen.fragileglass.network.PacketHandler;
import com.fredtargaryen.fragileglass.proxy.CommonProxy;
import com.fredtargaryen.fragileglass.tileentity.TileEntityFragileGlass;
import com.fredtargaryen.fragileglass.tileentity.TileEntityWeakStone;
import com.fredtargaryen.fragileglass.tileentity.capability.FragileCapFactory;
import com.fredtargaryen.fragileglass.tileentity.capability.FragileCapStorage;
import com.fredtargaryen.fragileglass.tileentity.capability.IFragileCapability;
import com.fredtargaryen.fragileglass.world.BreakSystem;
import com.fredtargaryen.fragileglass.world.BreakerDataManager;
import com.fredtargaryen.fragileglass.world.FragilityDataManager;
import com.fredtargaryen.fragileglass.worldgen.PatchGen;
import com.fredtargaryen.fragileglass.worldgen.PatchGenIce;
import com.fredtargaryen.fragileglass.worldgen.PatchGenStone;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@Mod(modid = DataReference.MODID, version = DataReference.VERSION, name=DataReference.MODNAME)
@Mod.EventBusSubscriber
@ObjectHolder(DataReference.MODID)
public class FragileGlassBase {
	// The instance of your mod that Forge uses.
    @Mod.Instance(DataReference.MODID)
    public static FragileGlassBase instance;

    public static ArrayList<Item> iceBlocks;

    //Config vars - worldgen
    private static boolean genThinIce;
    public static int avePatchSizeIce;
    public static int genChanceIce;
    private static boolean genWeakStone;
    public static int avePatchSizeStone;
    public static int genChanceStone;
    //Config var - Glass Shards compatibility
    public static boolean glassShards;
    //Config var - debug settings
    public static boolean showSuccessMessage;

    private static BreakerDataManager breakerDataManager;
    private static FragilityDataManager fragDataManager;

    private static PatchGen patchGenIce;
    private static PatchGen patchGenStone;

    public static BreakSystem breakSystem;

    /**
     * For compatibility with the Repose mod.
     */
    public static boolean reposeInstalled;

    //Declare all blocks here
    @ObjectHolder("fragileglass")
    public static Block fragileGlass;
    @ObjectHolder("fragilepane")
	public static Block fragilePane;
    @ObjectHolder("stainedfragileglass")
    public static Block stainedFragileGlass;
    @ObjectHolder("stainedfragilepane")
    public static Block stainedFragilePane;
    @ObjectHolder("sugarblock")
	public static Block sugarBlock;
    @ObjectHolder("thinice")
    public static Block thinIce;
    @ObjectHolder("sugarcauldron")
    public static Block sugarCauldron;
    @ObjectHolder("weakstone")
    public static Block weakStone;

    //Declare all items here
    @ObjectHolder("fragileglass")
    public static Item iFragileGlass;
    @ObjectHolder("fragilepane")
    public static Item iFragilePane;
    @ObjectHolder("stainedfragileglass")
    public static Item iStainedFragileGlass;
    @ObjectHolder("stainedfragilepane")
    public static Item iStainedFragilePane;
    @ObjectHolder("sugarblock")
    public static Item iSugarBlock;
    @ObjectHolder("thinice")
    public static Item iThinIce;
    @ObjectHolder("sugarcauldron")
    public static Item iSugarCauldron;
    @ObjectHolder("weakstone")
    public static Item iWeakStone;

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
        genThinIce = config.getBoolean("genThinIce", "Worldgen - Thin Ice", true, "If true, thin ice patches will generate on frozen bodies of water");
        avePatchSizeIce = config.getInt("avePatchSizeIce", "Worldgen - Thin Ice", 5, 1, 14, "Average patch diameter");
        genChanceIce = config.getInt("genChanceIce", "Worldgen - Thin Ice", 3, 1, 5, "1 in x chance of patch appearing");
        genWeakStone = config.getBoolean("genWeakStone", "Worldgen - Weak Stone", false, "If true, weak stone patches will generate. Expect falls into lava!");
        avePatchSizeStone = config.getInt("avePatchSizeStone", "Worldgen - Weak Stone", 5, 1, 14, "Average patch diameter");
        genChanceStone = config.getInt("genChanceStone", "Worldgen - Weak Stone", 3, 1, 5, "1 in x chance of patch appearing");
        glassShards = config.getBoolean("glassShards", "Mod Compatibility", false, "If true, broken fragile glass blocks will drop Glass Shards");
        showSuccessMessage = config.getBoolean("showSuccessMessage", "Other", true, "If true, show a message on login if config files loaded successfully");
        config.save();
        breakerDataManager = BreakerDataManager.getInstance();
        breakerDataManager.setupDirsAndFiles(event.getModConfigurationDirectory());
        fragDataManager = FragilityDataManager.getInstance();
        fragDataManager.setupDirsAndFiles(event.getModConfigurationDirectory());

        //BLOCK SETUP
    	fragileGlass = new BlockFragileGlass()
                .setUnlocalizedName("fragileglass")
                .setRegistryName("fragileglass");
    	fragilePane = new BlockFragilePane()
    			.setUnlocalizedName("fragilepane")
                .setRegistryName("fragilepane");
        stainedFragileGlass = new BlockStainedFragileGlass()
                .setUnlocalizedName("stainedfragileglass")
                .setRegistryName("stainedfragileglass");
        stainedFragilePane = new BlockStainedFragilePane()
                .setUnlocalizedName("stainedfragilepane")
                .setRegistryName("stainedfragilepane");
    	sugarBlock = new SugarBlock()
                .setUnlocalizedName("sugarblock")
                .setRegistryName("sugarblock");
        thinIce = new BlockThinIce()
                .setUnlocalizedName("thinice")
                .setRegistryName("thinice");
        sugarCauldron = new BlockSugarCauldron()
                .setUnlocalizedName("sugarcauldron")
                .setHardness(5.0F)
                .setResistance(10.0F)
                .setRegistryName("sugarcauldron");
        weakStone = new BlockWeakStone()
                .setUnlocalizedName("weakstone")
                .setRegistryName("weakstone");

        //ITEM SETUP
        iFragileGlass = new ItemBlock(fragileGlass)
                .setRegistryName("fragileglass");
        iFragilePane = new ItemBlock(fragilePane)
                .setRegistryName("fragilepane");
        iStainedFragileGlass = new ItemBlockStainedFragileGlass(stainedFragileGlass)
                .setRegistryName("stainedfragileglass");
        iStainedFragilePane = new ItemBlockStainedFragilePane(stainedFragilePane)
                .setRegistryName("stainedfragilepane");
        iSugarBlock = new ItemBlock(sugarBlock)
                .setRegistryName("sugarblock");
        iThinIce = new ItemBlock(thinIce)
                .setRegistryName("thinice");
        iSugarCauldron = new ItemBlock(sugarCauldron)
                .setRegistryName("sugarcauldron");
        iWeakStone = new ItemBlock(weakStone)
                .setRegistryName("weakstone");
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        event.getRegistry().registerAll(fragileGlass, fragilePane, stainedFragileGlass, stainedFragilePane, sugarBlock,
                thinIce, sugarCauldron, weakStone);
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().registerAll(iFragileGlass, iFragilePane, iStainedFragileGlass, iStainedFragilePane,
                iSugarBlock, iThinIce, iSugarCauldron, iWeakStone);
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event)
    {
        proxy.registerModels();
        proxy.doStateMappings();
    }

    @Mod.EventHandler
    public void load(FMLInitializationEvent event)
    {
    	GameRegistry.registerTileEntity(TileEntityFragileGlass.class, new ResourceLocation(DataReference.MODID+":tefg"));
    	GameRegistry.registerTileEntity(TileEntityWeakStone.class, new ResourceLocation(DataReference.MODID+":tews"));

        OreDictionary.registerOre("blockSugar", sugarBlock);

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
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        iceBlocks = new ArrayList<>();
        iceBlocks.addAll(OreDictionary.getOres("blockIce").stream().map(ItemStack::getItem).collect(Collectors.toList()));
        reposeInstalled = Loader.isModLoaded("repose");
    }

    /**
     * Clear all DataManager data and reload it from the config files.
     * @return true if no errors were found; false otherwise
     */
    public static boolean reloadDataManagers() {
        breakerDataManager.clearData();
        boolean breaksOK = breakerDataManager.loadEntityData();
        fragDataManager.clearData();
        boolean fragsOK = fragDataManager.loadBlockData();
        return breaksOK && fragsOK;
    }

    ////////////////////////
    //FOR THE MODID CHANGE//
    ////////////////////////
    @SubscribeEvent
    public void handleMissingMappings(RegistryEvent.MissingMappings evt) {
        String fullName = evt.getName().toString();
        if(fullName.equals("minecraft:blocks")) {
            for(Object mapping : evt.getAllMappings()) {
                RegistryEvent.MissingMappings.Mapping trueMapping = (RegistryEvent.MissingMappings.Mapping) mapping;
                if(trueMapping.key.getResourceDomain().equals("ftfragileglass")) {
                    switch (trueMapping.key.getResourcePath()) {
                        case "ftfragileglass":
                            trueMapping.remap(fragileGlass);
                            break;
                        case "ftfragilepane":
                            trueMapping.remap(fragilePane);
                            break;
                        case "ftstainedfragileglass":
                            trueMapping.remap(stainedFragileGlass);
                            break;
                        case "ftstainedfragilepane":
                            trueMapping.remap(stainedFragilePane);
                            break;
                        case "ftthinice":
                            trueMapping.remap(thinIce);
                            break;
                        case "ftweakstone":
                            trueMapping.remap(weakStone);
                            break;
                        case "ftsugarcauldron":
                            trueMapping.remap(sugarCauldron);
                            break;
                        case "ftsugarblock":
                            trueMapping.remap(sugarBlock);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        else if(fullName.equals("minecraft:items")) {
            for(Object mapping : evt.getAllMappings()) {
                RegistryEvent.MissingMappings.Mapping trueMapping = (RegistryEvent.MissingMappings.Mapping) mapping;
                if (trueMapping.key.getResourceDomain().equals("ftfragileglass")) {
                    switch (trueMapping.key.getResourcePath()) {
                        case "ftfragileglass":
                            trueMapping.remap(iFragileGlass);
                            break;
                        case "ftfragilepane":
                            trueMapping.remap(iFragilePane);
                            break;
                        case "ftstainedfragileglass":
                            trueMapping.remap(iStainedFragileGlass);
                            break;
                        case "ftstainedfragilepane":
                            trueMapping.remap(iStainedFragilePane);
                            break;
                        case "ftthinice":
                            trueMapping.remap(iThinIce);
                            break;
                        case "ftweakstone":
                            trueMapping.remap(iWeakStone);
                            break;
                        case "ftsugarcauldron":
                            trueMapping.remap(iSugarCauldron);
                            break;
                        case "ftsugarblock":
                            trueMapping.remap(iSugarBlock);
                            break;
                        default:
                            break;
                    }
                }
            }
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
                    breakerDataManager.addCapabilityIfPossible(e, evt);
                }
            }
        }
    }

    @SubscribeEvent
    public void onTileConstructed(AttachCapabilitiesEvent<TileEntity> evt) {
        TileEntity te = evt.getObject();
        fragDataManager.addCapabilityIfPossible(te, evt);
    }

    @SubscribeEvent
    public void initPlayerBreakerCap(EntityJoinWorldEvent ejwe) {
        Entity e = ejwe.getEntity();
        if(e.hasCapability(PLAYERBREAKCAP, null)) {
            e.getCapability(PLAYERBREAKCAP, null).init(e);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void loadSystem(WorldEvent.Load event) {
        STATUS = reloadDataManagers() ? SUCCESS_MESSAGE : FAILURE_MESSAGE;
        World w = event.getWorld();
        if(!w.isRemote) {
            breakSystem = new BreakSystem();
            breakSystem.init(w);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void stopSystem(WorldEvent.Unload event) {
        World w = event.getWorld();
        if(!w.isRemote) {
            if(breakSystem != null) {
                breakSystem.end(w);
            }
        }
    }

    /**
     * Only load stuff on world start, so that if there's an invalid config line, players can be told via chat
     */
    @Mod.EventHandler
    public void onWorldStart(FMLServerAboutToStartEvent event) {
        breakerDataManager.clearData();
        breakerDataManager.loadEntityData();
        fragDataManager.clearData();
        fragDataManager.loadBlockData();
    }

    //////////////////////////////
    //DATA MANAGER ERROR LOGGING//
    //////////////////////////////
    private static final ITextComponent SUCCESS_MESSAGE = new TextComponentString("[FRAGILE GLASS] Data reloaded without errors!").setStyle(new Style().setColor(TextFormatting.GREEN));
    private static final ITextComponent FAILURE_MESSAGE = new TextComponentString("[FRAGILE GLASS] Errors found in config files; please check config folder for more information.").setStyle(new Style().setColor(TextFormatting.RED));
    private static ITextComponent STATUS;

    @SubscribeEvent
    public void reload(CommandEvent event) {
        if(event.getCommand().getName().equals("reload")) {
            STATUS = reloadDataManagers() ? SUCCESS_MESSAGE : FAILURE_MESSAGE;
            event.getSender().sendMessage(STATUS);
        }
    }

    /**
     * When a player logs in, if they are in single player or if they are an op, they should know whether it loaded successfully last time.
     * @param event
     */
    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        EntityPlayer ep = event.player;
        //Only ops or single players can see the message
        if(ep.canUseCommand(2, "") || ep.world.getMinecraftServer().isSinglePlayer()) {
            //Always show the failure message
            if(STATUS == FAILURE_MESSAGE) {
                ep.sendStatusMessage(STATUS, false);
            }
            //Only show the success message if enabled in config
            else if(FragileGlassBase.showSuccessMessage) {
                ep.sendStatusMessage(STATUS, false);
            }
        }
    }
}
