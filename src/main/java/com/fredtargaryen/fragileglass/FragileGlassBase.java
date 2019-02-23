/**
 * TODO
 * Need to flatten items
 */
package com.fredtargaryen.fragileglass;

import com.fredtargaryen.fragileglass.block.*;
import com.fredtargaryen.fragileglass.entity.capability.*;
import com.fredtargaryen.fragileglass.network.MessageBreakerMovement;
import com.fredtargaryen.fragileglass.network.PacketHandler;
import com.fredtargaryen.fragileglass.proxy.ClientProxy;
import com.fredtargaryen.fragileglass.proxy.IProxy;
import com.fredtargaryen.fragileglass.proxy.ServerProxy;
import com.fredtargaryen.fragileglass.tileentity.TileEntityFragileGlass;
import com.fredtargaryen.fragileglass.tileentity.TileEntityWeakStone;
import com.fredtargaryen.fragileglass.tileentity.capability.FragileCapFactory;
import com.fredtargaryen.fragileglass.tileentity.capability.FragileCapStorage;
import com.fredtargaryen.fragileglass.tileentity.capability.IFragileCapability;
import com.fredtargaryen.fragileglass.world.*;
import com.fredtargaryen.fragileglass.worldgen.PatchGen;
import com.fredtargaryen.fragileglass.worldgen.PatchGenIce;
import com.fredtargaryen.fragileglass.worldgen.PatchGenStone;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.resources.IResourceManager;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Predicate;

@Mod(value = DataReference.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(DataReference.MODID)
public class FragileGlassBase {
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public static Tag<Block> ICE_BLOCKS;

    //Config vars - worldgen
    private static boolean genThinIce;
    public static int avePatchSizeIce;
    public static int genChanceIce;
    private static boolean genWeakStone;
    public static int avePatchSizeStone;
    public static int genChanceStone;

    private static BreakerDataManager breakerDataManager;
    private static FragilityDataManager fragDataManager;

    private static PatchGen patchGenIce;
    private static PatchGen patchGenStone;

    public static BreakSystem breakSystem;

    //Declare all blocks here
    @ObjectHolder("fragileglass")
    public static Block FRAGILE_GLASS;
    @ObjectHolder("fragilepane")
	public static Block FRAGILE_PANE;
    //Stained glass
    @ObjectHolder("whitestainedfragileglass")
    public static Block WHITE_STAINED_FRAGILE_GLASS;
    @ObjectHolder("orangestainedfragileglass")
    public static Block ORANGE_STAINED_FRAGILE_GLASS;
    @ObjectHolder("magentastainedfragileglass")
    public static Block MAGENTA_STAINED_FRAGILE_GLASS;
    @ObjectHolder("lightbluestainedfragileglass")
    public static Block LIGHT_BLUE_STAINED_FRAGILE_GLASS;
    @ObjectHolder("yellowstainedfragileglass")
    public static Block YELLOW_STAINED_FRAGILE_GLASS;
    @ObjectHolder("limestainedfragileglass")
    public static Block LIME_STAINED_FRAGILE_GLASS;
    @ObjectHolder("pinkstainedfragileglass")
    public static Block PINK_STAINED_FRAGILE_GLASS;
    @ObjectHolder("graystainedfragileglass")
    public static Block GRAY_STAINED_FRAGILE_GLASS;
    @ObjectHolder("lightgraystainedfragileglass")
    public static Block LIGHT_GRAY_STAINED_FRAGILE_GLASS;
    @ObjectHolder("cyanstainedfragileglass")
    public static Block CYAN_STAINED_FRAGILE_GLASS;
    @ObjectHolder("purplestainedfragileglass")
    public static Block PURPLE_STAINED_FRAGILE_GLASS;
    @ObjectHolder("bluestainedfragileglass")
    public static Block BLUE_STAINED_FRAGILE_GLASS;
    @ObjectHolder("brownstainedfragileglass")
    public static Block BROWN_STAINED_FRAGILE_GLASS;
    @ObjectHolder("greenstainedfragileglass")
    public static Block GREEN_STAINED_FRAGILE_GLASS;
    @ObjectHolder("redstainedfragileglass")
    public static Block RED_STAINED_FRAGILE_GLASS;
    @ObjectHolder("blackstainedfragileglass")
    public static Block BLACK_STAINED_FRAGILE_GLASS;
    //Stained glass panes
    @ObjectHolder("whitestainedfragileglasspane")
    public static Block WHITE_STAINED_FRAGILE_GLASS_PANE;
    @ObjectHolder("orangestainedfragileglasspane")
    public static Block ORANGE_STAINED_FRAGILE_GLASS_PANE;
    @ObjectHolder("magentastainedfragileglasspane")
    public static Block MAGENTA_STAINED_FRAGILE_GLASS_PANE;
    @ObjectHolder("lightbluestainedfragileglasspane")
    public static Block LIGHT_BLUE_STAINED_FRAGILE_GLASS_PANE;
    @ObjectHolder("yellowstainedfragileglasspane")
    public static Block YELLOW_STAINED_FRAGILE_GLASS_PANE;
    @ObjectHolder("limestainedfragileglasspane")
    public static Block LIME_STAINED_FRAGILE_GLASS_PANE;
    @ObjectHolder("pinkstainedfragileglasspane")
    public static Block PINK_STAINED_FRAGILE_GLASS_PANE;
    @ObjectHolder("graystainedfragileglasspane")
    public static Block GRAY_STAINED_FRAGILE_GLASS_PANE;
    @ObjectHolder("lightgraystainedfragileglasspane")
    public static Block LIGHT_GRAY_STAINED_FRAGILE_GLASS_PANE;
    @ObjectHolder("cyanstainedfragileglasspane")
    public static Block CYAN_STAINED_FRAGILE_GLASS_PANE;
    @ObjectHolder("purplestainedfragileglasspane")
    public static Block PURPLE_STAINED_FRAGILE_GLASS_PANE;
    @ObjectHolder("bluestainedfragileglasspane")
    public static Block BLUE_STAINED_FRAGILE_GLASS_PANE;
    @ObjectHolder("brownstainedfragileglasspane")
    public static Block BROWN_STAINED_FRAGILE_GLASS_PANE;
    @ObjectHolder("greenstainedfragileglasspane")
    public static Block GREEN_STAINED_FRAGILE_GLASS_PANE;
    @ObjectHolder("redstainedfragileglasspane")
    public static Block RED_STAINED_FRAGILE_GLASS_PANE;
    @ObjectHolder("blackstainedfragileglasspane")
    public static Block BLACK_STAINED_FRAGILE_GLASS_PANE;
    //Other blocks
    @ObjectHolder("sugarblock")
	public static Block SUGAR_BLOCK;
    @ObjectHolder("thinice")
    public static Block THIN_ICE;
    @ObjectHolder("sugarcauldron")
    public static Block SUGAR_CAULDRON;
    @ObjectHolder("weakstone")
    public static Block WEAK_STONE;

    //Declare all items here
    @ObjectHolder("fragileglass")
    public static Item ITEM_FRAGILE_GLASS;
    @ObjectHolder("fragilepane")
    public static Item ITEM_FRAGILE_PANE;
    //Stained glass
    @ObjectHolder("whitestainedfragileglass")
    public static Item ITEM_WHITE_STAINED_FRAGILE_GLASS;
    @ObjectHolder("orangestainedfragileglass")
    public static Item ITEM_ORANGE_STAINED_FRAGILE_GLASS;
    @ObjectHolder("magentastainedfragileglass")
    public static Item ITEM_MAGENTA_STAINED_FRAGILE_GLASS;
    @ObjectHolder("lightbluestainedfragileglass")
    public static Item ITEM_LIGHT_BLUE_STAINED_FRAGILE_GLASS;
    @ObjectHolder("yellowstainedfragileglass")
    public static Item ITEM_YELLOW_STAINED_FRAGILE_GLASS;
    @ObjectHolder("limestainedfragileglass")
    public static Item ITEM_LIME_STAINED_FRAGILE_GLASS;
    @ObjectHolder("pinkstainedfragileglass")
    public static Item ITEM_PINK_STAINED_FRAGILE_GLASS;
    @ObjectHolder("graystainedfragileglass")
    public static Item ITEM_GRAY_STAINED_FRAGILE_GLASS;
    @ObjectHolder("lightgraystainedfragileglass")
    public static Item ITEM_LIGHT_GRAY_STAINED_FRAGILE_GLASS;
    @ObjectHolder("cyanstainedfragileglass")
    public static Item ITEM_CYAN_STAINED_FRAGILE_GLASS;
    @ObjectHolder("purplestainedfragileglass")
    public static Item ITEM_PURPLE_STAINED_FRAGILE_GLASS;
    @ObjectHolder("bluestainedfragileglass")
    public static Item ITEM_BLUE_STAINED_FRAGILE_GLASS;
    @ObjectHolder("brownstainedfragileglass")
    public static Item ITEM_BROWN_STAINED_FRAGILE_GLASS;
    @ObjectHolder("greenstainedfragileglass")
    public static Item ITEM_GREEN_STAINED_FRAGILE_GLASS;
    @ObjectHolder("redstainedfragileglass")
    public static Item ITEM_RED_STAINED_FRAGILE_GLASS;
    @ObjectHolder("blackstainedfragileglass")
    public static Item ITEM_BLACK_STAINED_FRAGILE_GLASS;
    //Stained glass panes
    @ObjectHolder("whitestainedfragileglasspane")
    public static Item ITEM_WHITE_STAINED_FRAGILE_GLASS_PANE;
    @ObjectHolder("orangestainedfragileglasspane")
    public static Item ITEM_ORANGE_STAINED_FRAGILE_GLASS_PANE;
    @ObjectHolder("magentastainedfragileglasspane")
    public static Item ITEM_MAGENTA_STAINED_FRAGILE_GLASS_PANE;
    @ObjectHolder("lightbluestainedfragileglasspane")
    public static Item ITEM_LIGHT_BLUE_STAINED_FRAGILE_GLASS_PANE;
    @ObjectHolder("yellowstainedfragileglasspane")
    public static Item ITEM_YELLOW_STAINED_FRAGILE_GLASS_PANE;
    @ObjectHolder("limestainedfragileglasspane")
    public static Item ITEM_LIME_STAINED_FRAGILE_GLASS_PANE;
    @ObjectHolder("pinkstainedfragileglasspane")
    public static Item ITEM_PINK_STAINED_FRAGILE_GLASS_PANE;
    @ObjectHolder("graystainedfragileglasspane")
    public static Item ITEM_GRAY_STAINED_FRAGILE_GLASS_PANE;
    @ObjectHolder("lightgraystainedfragileglasspane")
    public static Item ITEM_LIGHT_GRAY_STAINED_FRAGILE_GLASS_PANE;
    @ObjectHolder("cyanstainedfragileglasspane")
    public static Item ITEM_CYAN_STAINED_FRAGILE_GLASS_PANE;
    @ObjectHolder("purplestainedfragileglasspane")
    public static Item ITEM_PURPLE_STAINED_FRAGILE_GLASS_PANE;
    @ObjectHolder("bluestainedfragileglasspane")
    public static Item ITEM_BLUE_STAINED_FRAGILE_GLASS_PANE;
    @ObjectHolder("brownstainedfragileglasspane")
    public static Item ITEM_BROWN_STAINED_FRAGILE_GLASS_PANE;
    @ObjectHolder("greenstainedfragileglasspane")
    public static Item ITEM_GREEN_STAINED_FRAGILE_GLASS_PANE;
    @ObjectHolder("redstainedfragileglasspane")
    public static Item ITEM_RED_STAINED_FRAGILE_GLASS_PANE;
    @ObjectHolder("blackstainedfragileglasspane")
    public static Item ITEM_BLACK_STAINED_FRAGILE_GLASS_PANE;
    @ObjectHolder("sugarblock")
    public static Item ITEM_SUGAR_BLOCK;
    @ObjectHolder("thinice")
    public static Item ITEM_THIN_ICE;
    @ObjectHolder("sugarcauldron")
    public static Item ITEM_SUGAR_CAULDRON;
    @ObjectHolder("weakstone")
    public static Item ITEM_WEAK_STONE;

    //Declare TileEntityTypes here
    public static TileEntityType TEFG_TYPE = TileEntityType.Builder.create(TileEntityFragileGlass::new)
            .build(null).setRegistryName(new ResourceLocation(DataReference.MODID, "tefg"));
    public static TileEntityType TEWS_TYPE = TileEntityType.Builder.create(TileEntityWeakStone::new)
            .build(null).setRegistryName(new ResourceLocation(DataReference.MODID, "tews"));

    // Says where the client and server 'proxy' code is loaded.
    private static IProxy proxy = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());

    public FragileGlassBase() {
        IEventBus loadingBus = FMLJavaModLoadingContext.get().getModEventBus();
        // Register the setup method for modloading
        loadingBus.addListener(this::postRegistration);

        // Register ourselves for server, registry and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        event.getRegistry().registerAll(
                FRAGILE_GLASS,
                FRAGILE_PANE,
                WHITE_STAINED_FRAGILE_GLASS,
                ORANGE_STAINED_FRAGILE_GLASS,
                MAGENTA_STAINED_FRAGILE_GLASS,
                LIGHT_BLUE_STAINED_FRAGILE_GLASS,
                YELLOW_STAINED_FRAGILE_GLASS,
                LIME_STAINED_FRAGILE_GLASS,
                PINK_STAINED_FRAGILE_GLASS,
                GRAY_STAINED_FRAGILE_GLASS,
                LIGHT_GRAY_STAINED_FRAGILE_GLASS,
                CYAN_STAINED_FRAGILE_GLASS,
                PURPLE_STAINED_FRAGILE_GLASS,
                BLUE_STAINED_FRAGILE_GLASS,
                BROWN_STAINED_FRAGILE_GLASS,
                GREEN_STAINED_FRAGILE_GLASS,
                RED_STAINED_FRAGILE_GLASS,
                BLACK_STAINED_FRAGILE_GLASS,
                WHITE_STAINED_FRAGILE_GLASS_PANE,
                ORANGE_STAINED_FRAGILE_GLASS_PANE,
                MAGENTA_STAINED_FRAGILE_GLASS_PANE,
                LIGHT_BLUE_STAINED_FRAGILE_GLASS_PANE,
                YELLOW_STAINED_FRAGILE_GLASS_PANE,
                LIME_STAINED_FRAGILE_GLASS_PANE,
                PINK_STAINED_FRAGILE_GLASS_PANE,
                GRAY_STAINED_FRAGILE_GLASS_PANE,
                LIGHT_GRAY_STAINED_FRAGILE_GLASS_PANE,
                CYAN_STAINED_FRAGILE_GLASS_PANE,
                PURPLE_STAINED_FRAGILE_GLASS_PANE,
                BLUE_STAINED_FRAGILE_GLASS_PANE,
                BROWN_STAINED_FRAGILE_GLASS_PANE,
                GREEN_STAINED_FRAGILE_GLASS_PANE,
                RED_STAINED_FRAGILE_GLASS_PANE,
                BLACK_STAINED_FRAGILE_GLASS_PANE,
                SUGAR_BLOCK,
                THIN_ICE,
                SUGAR_CAULDRON,
                WEAK_STONE);
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().registerAll(
                ITEM_FRAGILE_GLASS,
                ITEM_FRAGILE_PANE,
                ITEM_WHITE_STAINED_FRAGILE_GLASS,
                ITEM_ORANGE_STAINED_FRAGILE_GLASS,
                ITEM_MAGENTA_STAINED_FRAGILE_GLASS,
                ITEM_LIGHT_BLUE_STAINED_FRAGILE_GLASS,
                ITEM_YELLOW_STAINED_FRAGILE_GLASS,
                ITEM_LIME_STAINED_FRAGILE_GLASS,
                ITEM_PINK_STAINED_FRAGILE_GLASS,
                ITEM_GRAY_STAINED_FRAGILE_GLASS,
                ITEM_LIGHT_GRAY_STAINED_FRAGILE_GLASS,
                ITEM_CYAN_STAINED_FRAGILE_GLASS,
                ITEM_PURPLE_STAINED_FRAGILE_GLASS,
                ITEM_BLUE_STAINED_FRAGILE_GLASS,
                ITEM_BROWN_STAINED_FRAGILE_GLASS,
                ITEM_GREEN_STAINED_FRAGILE_GLASS,
                ITEM_RED_STAINED_FRAGILE_GLASS,
                ITEM_BLACK_STAINED_FRAGILE_GLASS,
                ITEM_WHITE_STAINED_FRAGILE_GLASS_PANE,
                ITEM_ORANGE_STAINED_FRAGILE_GLASS_PANE,
                ITEM_MAGENTA_STAINED_FRAGILE_GLASS_PANE,
                ITEM_LIGHT_BLUE_STAINED_FRAGILE_GLASS_PANE,
                ITEM_YELLOW_STAINED_FRAGILE_GLASS_PANE,
                ITEM_LIME_STAINED_FRAGILE_GLASS_PANE,
                ITEM_PINK_STAINED_FRAGILE_GLASS_PANE,
                ITEM_GRAY_STAINED_FRAGILE_GLASS_PANE,
                ITEM_LIGHT_GRAY_STAINED_FRAGILE_GLASS_PANE,
                ITEM_CYAN_STAINED_FRAGILE_GLASS_PANE,
                ITEM_PURPLE_STAINED_FRAGILE_GLASS_PANE,
                ITEM_BLUE_STAINED_FRAGILE_GLASS_PANE,
                ITEM_BROWN_STAINED_FRAGILE_GLASS_PANE,
                ITEM_GREEN_STAINED_FRAGILE_GLASS_PANE,
                ITEM_RED_STAINED_FRAGILE_GLASS_PANE,
                ITEM_BLACK_STAINED_FRAGILE_GLASS_PANE,
                ITEM_SUGAR_BLOCK,
                ITEM_THIN_ICE,
                ITEM_SUGAR_CAULDRON,
                ITEM_WEAK_STONE);
    }

    @SubscribeEvent
    public static void registerTileEntities(IForgeRegistry<TileEntityType<?>> registry)
    {
        registry.register(TEFG_TYPE);
        registry.register(TEWS_TYPE);
    }

    /**
     * Called after all registry events. Runs in parallel with other SetupEvent handlers.
     * @param event
     */
    public void postRegistration(FMLCommonSetupEvent event)
    {
        PacketHandler.init();

        //Capability
        CapabilityManager.INSTANCE.register(IBreakCapability.class, new BreakCapStorage(), new BreakCapFactory());
        CapabilityManager.INSTANCE.register(IPlayerBreakCapability.class, new PlayerBreakStorage(), new PlayerBreakFactory());
        CapabilityManager.INSTANCE.register(IFragileCapability.class, new FragileCapStorage(), new FragileCapFactory());

        //TODO CONFIG SETUP
//        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
//        config.load();
//        genThinIce = config.getBoolean("genThinIce", "Worldgen - Thin Ice", true, "If true, thin ice patches will generate on frozen bodies of water");
//        avePatchSizeIce = config.getInt("avePatchSizeIce", "Worldgen - Thin Ice", 5, 1, 14, "Average patch diameter");
//        genChanceIce = config.getInt("genChanceIce", "Worldgen - Thin Ice", 3, 1, 5, "1 in x chance of patch appearing");
//        genWeakStone = config.getBoolean("genWeakStone", "Worldgen - Weak Stone", false, "If true, weak stone patches will generate. Expect falls into lava!");
//        avePatchSizeStone = config.getInt("avePatchSizeStone", "Worldgen - Weak Stone", 5, 1, 14, "Average patch diameter");
//        genChanceStone = config.getInt("genChanceStone", "Worldgen - Weak Stone", 3, 1, 5, "1 in x chance of patch appearing");
//        config.save();
//        breakerDataManager = BreakerDataManager.getInstance();
//        breakerDataManager.setupDirsAndFiles(event.getModConfigurationDirectory());
//        fragDataManager = FragilityDataManager.getInstance();
//        fragDataManager.setupDirsAndFiles(event.getModConfigurationDirectory());

        //BLOCK SETUP
    	FRAGILE_GLASS = new BlockFragileGlass()
                .setRegistryName("fragileglass");
    	FRAGILE_PANE = new BlockFragilePane()
                .setRegistryName("fragilepane");

    	WHITE_STAINED_FRAGILE_GLASS = new BlockStainedFragileGlass()
                .setRegistryName("whitestainedfragileglass");
        ORANGE_STAINED_FRAGILE_GLASS = new BlockStainedFragileGlass()
                .setRegistryName("whitestainedfragileglass");
        MAGENTA_STAINED_FRAGILE_GLASS = new BlockStainedFragileGlass()
                .setRegistryName("whitestainedfragileglass");
        LIGHT_BLUE_STAINED_FRAGILE_GLASS = new BlockStainedFragileGlass()
                .setRegistryName("whitestainedfragileglass");
        YELLOW_STAINED_FRAGILE_GLASS = new BlockStainedFragileGlass()
                .setRegistryName("whitestainedfragileglass");
        LIME_STAINED_FRAGILE_GLASS = new BlockStainedFragileGlass()
                .setRegistryName("whitestainedfragileglass");
        PINK_STAINED_FRAGILE_GLASS = new BlockStainedFragileGlass()
                .setRegistryName("whitestainedfragileglass");
        GRAY_STAINED_FRAGILE_GLASS = new BlockStainedFragileGlass()
                .setRegistryName("whitestainedfragileglass");
        LIGHT_GRAY_STAINED_FRAGILE_GLASS = new BlockStainedFragileGlass()
                .setRegistryName("whitestainedfragileglass");
        CYAN_STAINED_FRAGILE_GLASS = new BlockStainedFragileGlass()
                .setRegistryName("whitestainedfragileglass");
        PURPLE_STAINED_FRAGILE_GLASS = new BlockStainedFragileGlass()
                .setRegistryName("whitestainedfragileglass");
        BLUE_STAINED_FRAGILE_GLASS = new BlockStainedFragileGlass()
                .setRegistryName("whitestainedfragileglass");
        BROWN_STAINED_FRAGILE_GLASS = new BlockStainedFragileGlass()
                .setRegistryName("whitestainedfragileglass");
        GREEN_STAINED_FRAGILE_GLASS = new BlockStainedFragileGlass()
                .setRegistryName("whitestainedfragileglass");
        RED_STAINED_FRAGILE_GLASS = new BlockStainedFragileGlass()
                .setRegistryName("whitestainedfragileglass");
        BLACK_STAINED_FRAGILE_GLASS = new BlockStainedFragileGlass()
                .setRegistryName("whitestainedfragileglass");

        WHITE_STAINED_FRAGILE_GLASS_PANE = new BlockStainedFragilePane()
                .setRegistryName("stainedfragilepane");
        ORANGE_STAINED_FRAGILE_GLASS_PANE = new BlockStainedFragilePane()
                .setRegistryName("stainedfragilepane");
        MAGENTA_STAINED_FRAGILE_GLASS_PANE = new BlockStainedFragilePane()
                .setRegistryName("stainedfragilepane");
        LIGHT_BLUE_STAINED_FRAGILE_GLASS_PANE = new BlockStainedFragilePane()
                .setRegistryName("stainedfragilepane");
        YELLOW_STAINED_FRAGILE_GLASS_PANE = new BlockStainedFragilePane()
                .setRegistryName("stainedfragilepane");
        LIME_STAINED_FRAGILE_GLASS_PANE = new BlockStainedFragilePane()
                .setRegistryName("stainedfragilepane");
        PINK_STAINED_FRAGILE_GLASS_PANE = new BlockStainedFragilePane()
                .setRegistryName("stainedfragilepane");
        GRAY_STAINED_FRAGILE_GLASS_PANE = new BlockStainedFragilePane()
                .setRegistryName("stainedfragilepane");
        LIGHT_GRAY_STAINED_FRAGILE_GLASS_PANE = new BlockStainedFragilePane()
                .setRegistryName("stainedfragilepane");
        CYAN_STAINED_FRAGILE_GLASS_PANE = new BlockStainedFragilePane()
                .setRegistryName("stainedfragilepane");
        PURPLE_STAINED_FRAGILE_GLASS_PANE = new BlockStainedFragilePane()
                .setRegistryName("stainedfragilepane");
        BLUE_STAINED_FRAGILE_GLASS_PANE = new BlockStainedFragilePane()
                .setRegistryName("stainedfragilepane");
        BROWN_STAINED_FRAGILE_GLASS_PANE = new BlockStainedFragilePane()
                .setRegistryName("stainedfragilepane");
        GREEN_STAINED_FRAGILE_GLASS_PANE = new BlockStainedFragilePane()
                .setRegistryName("stainedfragilepane");
        RED_STAINED_FRAGILE_GLASS_PANE = new BlockStainedFragilePane()
                .setRegistryName("stainedfragilepane");
        BLACK_STAINED_FRAGILE_GLASS_PANE = new BlockStainedFragilePane()
                .setRegistryName("stainedfragilepane");

    	SUGAR_BLOCK = new SugarBlock()
                .setRegistryName("sugarblock");
        THIN_ICE = new BlockThinIce()
                .setRegistryName("thinice");
        SUGAR_CAULDRON = new BlockSugarCauldron()
                .setRegistryName("sugarcauldron");
        WEAK_STONE = new BlockWeakStone()
                .setRegistryName("weakstone");

        //ITEM SETUP
        ITEM_FRAGILE_GLASS = new ItemBlock(FRAGILE_GLASS, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS))
                .setRegistryName("fragileglass");
        ITEM_FRAGILE_PANE = new ItemBlock(FRAGILE_PANE, new Item.Properties().group(ItemGroup.DECORATIONS))
                .setRegistryName("fragilepane");

        ITEM_WHITE_STAINED_FRAGILE_GLASS = new ItemBlock(WHITE_STAINED_FRAGILE_GLASS, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS))
                .setRegistryName("whitestainedfragileglass");
        ITEM_ORANGE_STAINED_FRAGILE_GLASS = new ItemBlock(ORANGE_STAINED_FRAGILE_GLASS, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS))
                .setRegistryName("orangestainedfragileglass");
        ITEM_MAGENTA_STAINED_FRAGILE_GLASS = new ItemBlock(MAGENTA_STAINED_FRAGILE_GLASS, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS))
                .setRegistryName("magentastainedfragileglass");
        ITEM_LIGHT_BLUE_STAINED_FRAGILE_GLASS = new ItemBlock(LIGHT_BLUE_STAINED_FRAGILE_GLASS, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS))
                .setRegistryName("lightbluestainedfragileglass");
        ITEM_YELLOW_STAINED_FRAGILE_GLASS = new ItemBlock(YELLOW_STAINED_FRAGILE_GLASS, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS))
                .setRegistryName("yellowstainedfragileglass");
        ITEM_LIME_STAINED_FRAGILE_GLASS = new ItemBlock(LIME_STAINED_FRAGILE_GLASS, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS))
                .setRegistryName("limestainedfragileglass");
        ITEM_PINK_STAINED_FRAGILE_GLASS = new ItemBlock(PINK_STAINED_FRAGILE_GLASS, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS))
                .setRegistryName("pinkstainedfragileglass");
        ITEM_GRAY_STAINED_FRAGILE_GLASS = new ItemBlock(GRAY_STAINED_FRAGILE_GLASS, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS))
                .setRegistryName("graystainedfragileglass");
        ITEM_LIGHT_GRAY_STAINED_FRAGILE_GLASS = new ItemBlock(LIGHT_GRAY_STAINED_FRAGILE_GLASS, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS))
                .setRegistryName("lightgraystainedfragileglass");
        ITEM_CYAN_STAINED_FRAGILE_GLASS = new ItemBlock(CYAN_STAINED_FRAGILE_GLASS, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS))
                .setRegistryName("cyanstainedfragileglass");
        ITEM_PURPLE_STAINED_FRAGILE_GLASS = new ItemBlock(PURPLE_STAINED_FRAGILE_GLASS, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS))
                .setRegistryName("purplestainedfragileglass");
        ITEM_BLUE_STAINED_FRAGILE_GLASS = new ItemBlock(BLUE_STAINED_FRAGILE_GLASS, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS))
                .setRegistryName("bluestainedfragileglass");
        ITEM_BROWN_STAINED_FRAGILE_GLASS = new ItemBlock(BROWN_STAINED_FRAGILE_GLASS, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS))
                .setRegistryName("brownstainedfragileglass");
        ITEM_GREEN_STAINED_FRAGILE_GLASS = new ItemBlock(GREEN_STAINED_FRAGILE_GLASS, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS))
                .setRegistryName("greenstainedfragileglass");
        ITEM_RED_STAINED_FRAGILE_GLASS = new ItemBlock(RED_STAINED_FRAGILE_GLASS, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS))
                .setRegistryName("redstainedfragileglass");
        ITEM_BLACK_STAINED_FRAGILE_GLASS = new ItemBlock(BLACK_STAINED_FRAGILE_GLASS, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS))
                .setRegistryName("blackstainedfragileglass");

        ITEM_WHITE_STAINED_FRAGILE_GLASS_PANE = new ItemBlock(WHITE_STAINED_FRAGILE_GLASS_PANE, new Item.Properties().group(ItemGroup.DECORATIONS))
                .setRegistryName("whitestainedfragilepane");
        ITEM_ORANGE_STAINED_FRAGILE_GLASS_PANE = new ItemBlock(ORANGE_STAINED_FRAGILE_GLASS_PANE, new Item.Properties().group(ItemGroup.DECORATIONS))
                .setRegistryName("orangestainedfragileglass");
        ITEM_MAGENTA_STAINED_FRAGILE_GLASS_PANE = new ItemBlock(MAGENTA_STAINED_FRAGILE_GLASS_PANE, new Item.Properties().group(ItemGroup.DECORATIONS))
                .setRegistryName("magentastainedfragileglass");
        ITEM_LIGHT_BLUE_STAINED_FRAGILE_GLASS_PANE = new ItemBlock(LIGHT_BLUE_STAINED_FRAGILE_GLASS_PANE, new Item.Properties().group(ItemGroup.DECORATIONS))
                .setRegistryName("lightbluestainedfragileglass");
        ITEM_YELLOW_STAINED_FRAGILE_GLASS_PANE = new ItemBlock(YELLOW_STAINED_FRAGILE_GLASS_PANE, new Item.Properties().group(ItemGroup.DECORATIONS))
                .setRegistryName("yellowstainedfragileglass");
        ITEM_LIME_STAINED_FRAGILE_GLASS_PANE = new ItemBlock(LIME_STAINED_FRAGILE_GLASS_PANE, new Item.Properties().group(ItemGroup.DECORATIONS))
                .setRegistryName("limestainedfragileglass");
        ITEM_PINK_STAINED_FRAGILE_GLASS_PANE = new ItemBlock(PINK_STAINED_FRAGILE_GLASS_PANE, new Item.Properties().group(ItemGroup.DECORATIONS))
                .setRegistryName("pinkstainedfragileglass");
        ITEM_GRAY_STAINED_FRAGILE_GLASS_PANE = new ItemBlock(GRAY_STAINED_FRAGILE_GLASS_PANE, new Item.Properties().group(ItemGroup.DECORATIONS))
                .setRegistryName("graystainedfragileglass");
        ITEM_LIGHT_GRAY_STAINED_FRAGILE_GLASS_PANE = new ItemBlock(LIGHT_GRAY_STAINED_FRAGILE_GLASS_PANE, new Item.Properties().group(ItemGroup.DECORATIONS))
                .setRegistryName("lightgraystainedfragileglass");
        ITEM_CYAN_STAINED_FRAGILE_GLASS_PANE = new ItemBlock(CYAN_STAINED_FRAGILE_GLASS_PANE, new Item.Properties().group(ItemGroup.DECORATIONS))
                .setRegistryName("cyanstainedfragileglass");
        ITEM_PURPLE_STAINED_FRAGILE_GLASS_PANE = new ItemBlock(PURPLE_STAINED_FRAGILE_GLASS_PANE, new Item.Properties().group(ItemGroup.DECORATIONS))
                .setRegistryName("purplestainedfragileglass");
        ITEM_BLUE_STAINED_FRAGILE_GLASS_PANE = new ItemBlock(BLUE_STAINED_FRAGILE_GLASS_PANE, new Item.Properties().group(ItemGroup.DECORATIONS))
                .setRegistryName("bluestainedfragileglass");
        ITEM_BROWN_STAINED_FRAGILE_GLASS_PANE = new ItemBlock(BROWN_STAINED_FRAGILE_GLASS_PANE, new Item.Properties().group(ItemGroup.DECORATIONS))
                .setRegistryName("brownstainedfragileglass");
        ITEM_GREEN_STAINED_FRAGILE_GLASS_PANE = new ItemBlock(GREEN_STAINED_FRAGILE_GLASS_PANE, new Item.Properties().group(ItemGroup.DECORATIONS))
                .setRegistryName("greenstainedfragileglass");
        ITEM_RED_STAINED_FRAGILE_GLASS_PANE = new ItemBlock(RED_STAINED_FRAGILE_GLASS_PANE, new Item.Properties().group(ItemGroup.DECORATIONS))
                .setRegistryName("redstainedfragileglass");
        ITEM_BLACK_STAINED_FRAGILE_GLASS_PANE = new ItemBlock(BLACK_STAINED_FRAGILE_GLASS_PANE, new Item.Properties().group(ItemGroup.DECORATIONS))
                .setRegistryName("blackstainedfragileglass");

        ITEM_SUGAR_BLOCK = new ItemBlock(SUGAR_BLOCK, new Item.Properties().group(ItemGroup.FOOD))
                .setRegistryName("sugarblock");
        ITEM_THIN_ICE = new ItemBlock(THIN_ICE, new Item.Properties().group(ItemGroup.MISC))
                .setRegistryName("thinice");
        ITEM_SUGAR_CAULDRON = new ItemBlock(SUGAR_CAULDRON, new Item.Properties().group(ItemGroup.TOOLS))
                .setRegistryName("sugarcauldron");
        ITEM_WEAK_STONE = new ItemBlock(WEAK_STONE, new Item.Properties().group(ItemGroup.MISC))
                .setRegistryName("weakstone");

        //WORLDGEN SETUP
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

        //TAGS
        MinecraftForge.EVENT_BUS.register(new ISelectiveResourceReloadListener() {
            private final ResourceLocation ICE_BLOCK_GROUP = new ResourceLocation(DataReference.MODID, "ice");

            @Override
            public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate) {
                ICE_BLOCKS = BlockTags.getCollection().getOrCreate(ICE_BLOCK_GROUP);
            }
        });

        //LOAD FRAGILITY AND BREAKER CONFIGS
        breakerDataManager.loadEntityData();
        fragDataManager.loadBlockData();
    }

    public void onBlocksRefresh(ISelectiveResourceReloadListener vent)

    ////////////////////////
    //FOR THE MODID CHANGE//
    ////////////////////////
    @SubscribeEvent
    public void handleMissingMappings(RegistryEvent.MissingMappings evt) {
        String fullName = evt.getName().toString();
        if(fullName.equals("minecraft:blocks")) {
            for(Object mapping : evt.getAllMappings()) {
                RegistryEvent.MissingMappings.Mapping trueMapping = (RegistryEvent.MissingMappings.Mapping) mapping;
                if(trueMapping.key.getNamespace().equals("ftfragileglass")) {
                    switch (trueMapping.key.getPath()) {
                        case "ftfragileglass":
                            trueMapping.remap(FRAGILE_GLASS);
                            break;
                        case "ftfragilepane":
                            trueMapping.remap(FRAGILE_PANE);
                            break;
                        case "ftstainedfragileglass":
                            trueMapping.remap(WHITE_STAINED_FRAGILE_GLASS);
                            break;
                        case "ftstainedfragilepane":
                            trueMapping.remap(WHITE_STAINED_FRAGILE_GLASS_PANE);
                            break;
                        case "ftthinice":
                            trueMapping.remap(THIN_ICE);
                            break;
                        case "ftweakstone":
                            trueMapping.remap(WEAK_STONE);
                            break;
                        case "ftsugarcauldron":
                            trueMapping.remap(SUGAR_CAULDRON);
                            break;
                        case "ftsugarblock":
                            trueMapping.remap(SUGAR_BLOCK);
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
                if (trueMapping.key.getNamespace().equals("ftfragileglass")) {
                    switch (trueMapping.key.getPath()) {
                        case "ftfragileglass":
                            trueMapping.remap(ITEM_FRAGILE_GLASS);
                            break;
                        case "ftfragilepane":
                            trueMapping.remap(ITEM_FRAGILE_PANE);
                            break;
                        case "ftstainedfragileglass":
                            trueMapping.remap(ITEM_WHITE_STAINED_FRAGILE_GLASS);
                            break;
                        case "ftstainedfragilepane":
                            trueMapping.remap(ITEM_WHITE_STAINED_FRAGILE_GLASS_PANE);
                            break;
                        case "ftthinice":
                            trueMapping.remap(ITEM_THIN_ICE);
                            break;
                        case "ftweakstone":
                            trueMapping.remap(ITEM_WEAK_STONE);
                            break;
                        case "ftsugarcauldron":
                            trueMapping.remap(ITEM_SUGAR_CAULDRON);
                            break;
                        case "ftsugarblock":
                            trueMapping.remap(ITEM_SUGAR_BLOCK);
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
                                if (ep.removed) {
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
                                public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
                                    if (capability == PLAYERBREAKCAP || capability == BREAKCAP) {
                                        return LazyOptional.of(() -> (T) inst);
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
        e.getCapability(PLAYERBREAKCAP).ifPresent(pbc -> pbc.init(e));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void loadSystem(WorldEvent.Load event) {
        World w = (World) event.getWorld();
        if(!w.isRemote) {
            breakSystem = new BreakSystem();
            breakSystem.init(w);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void stopSystem(WorldEvent.Unload event) {
        World w = (World) event.getWorld();
        if(!w.isSer) {
            if(breakSystem != null) {
                breakSystem.end(w);
            }
        }
    }

    //////////////////
    //LOGGER METHODS//
    //////////////////
    public static void warn(String message) {
        LOGGER.warn(message);
    }
}
