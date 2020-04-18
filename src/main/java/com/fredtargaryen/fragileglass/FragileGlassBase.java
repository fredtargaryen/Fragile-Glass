package com.fredtargaryen.fragileglass;

import com.fredtargaryen.fragileglass.block.*;
import com.fredtargaryen.fragileglass.client.particle.MyBubbleParticle;
import com.fredtargaryen.fragileglass.command.CommandsBase;
import com.fredtargaryen.fragileglass.config.Config;
import com.fredtargaryen.fragileglass.config.WorldgenConfig;
import com.fredtargaryen.fragileglass.config.behaviour.datamanager.BlockDataManager;
import com.fredtargaryen.fragileglass.config.behaviour.datamanager.EntityDataManager;
import com.fredtargaryen.fragileglass.config.behaviour.datamanager.TileEntityDataManager;
import com.fredtargaryen.fragileglass.entity.capability.*;
import com.fredtargaryen.fragileglass.network.MessageBreakerMovement;
import com.fredtargaryen.fragileglass.network.PacketHandler;
import com.fredtargaryen.fragileglass.proxy.ClientProxy;
import com.fredtargaryen.fragileglass.proxy.IProxy;
import com.fredtargaryen.fragileglass.proxy.ServerProxy;
import com.fredtargaryen.fragileglass.tileentity.TileEntityWeakStone;
import com.fredtargaryen.fragileglass.tileentity.capability.FragileCapFactory;
import com.fredtargaryen.fragileglass.tileentity.capability.FragileCapStorage;
import com.fredtargaryen.fragileglass.tileentity.capability.IFragileCapability;
import com.fredtargaryen.fragileglass.world.BreakSystem;
import com.fredtargaryen.fragileglass.worldgen.FeatureManager;
import com.fredtargaryen.fragileglass.worldgen.IcePatchGenConfig;
import com.fredtargaryen.fragileglass.worldgen.StonePatchGenConfig;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.Tag;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.Feature;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

@Mod(value = DataReference.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(DataReference.MODID)
public class FragileGlassBase {
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public static Tag<Block> ICE_BLOCKS;

    private static BlockDataManager blockDataManager;
    private static EntityDataManager entityDataManager;
    private static TileEntityDataManager tileEntityDataManager;

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

    //Declare Features here
    @ObjectHolder("icepatchgen")
    public static Feature<IcePatchGenConfig> ICE_FEATURE;
    @ObjectHolder("stonepatchgen")
    public static Feature<StonePatchGenConfig> STONE_FEATURE;

    //Declare ParticleTypes here
    @ObjectHolder("bubble")
    public static BasicParticleType BUBBLE;

    //Declare TileEntityTypes here
    @ObjectHolder("tews")
    public static TileEntityType TEWS_TYPE;

    public static FeatureManager FEATURE_MANAGER;

    // Says where the client and server 'proxy' code is loaded.
    private static IProxy proxy = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());

    public FragileGlassBase() {
        //Register the config
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SERVER_CONFIG_SPEC);

        //Event bus
        IEventBus loadingBus = FMLJavaModLoadingContext.get().getModEventBus();
        // Register the setup method for modloading
        loadingBus.addListener(this::postRegistration);
        loadingBus.addListener(this::clientSetup);

        // Register ourselves for server, registry and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        //Load the config
        Config.loadConfig(FMLPaths.CONFIGDIR.get().resolve(DataReference.MODID + ".toml"));
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(
                //Non-stained blocks
                new FragileGlassBlock()
                        .setRegistryName("fragileglass"),
                new FragilePaneBlock()
                        .setRegistryName("fragilepane"),
                new SugarBlock()
                        .setRegistryName("sugarblock"),
                new ThinIceBlock()
                        .setRegistryName("thinice"),
                new SugarCauldronBlock()
                        .setRegistryName("sugarcauldron"),
                new WeakStoneBlock()
                        .setRegistryName("weakstone"),
                //Stained Fragile Glass blocks
                new StainedFragileGlassBlock(DyeColor.WHITE)
                        .setRegistryName("whitestainedfragileglass"),
                new StainedFragileGlassBlock(DyeColor.ORANGE)
                        .setRegistryName("orangestainedfragileglass"),
                new StainedFragileGlassBlock(DyeColor.MAGENTA)
                        .setRegistryName("magentastainedfragileglass"),
                new StainedFragileGlassBlock(DyeColor.LIGHT_BLUE)
                        .setRegistryName("lightbluestainedfragileglass"),
                new StainedFragileGlassBlock(DyeColor.YELLOW)
                        .setRegistryName("yellowstainedfragileglass"),
                new StainedFragileGlassBlock(DyeColor.LIME)
                        .setRegistryName("limestainedfragileglass"),
                new StainedFragileGlassBlock(DyeColor.PINK)
                        .setRegistryName("pinkstainedfragileglass"),
                new StainedFragileGlassBlock(DyeColor.GRAY)
                        .setRegistryName("graystainedfragileglass"),
                new StainedFragileGlassBlock(DyeColor.LIGHT_GRAY)
                        .setRegistryName("lightgraystainedfragileglass"),
                new StainedFragileGlassBlock(DyeColor.CYAN)
                        .setRegistryName("cyanstainedfragileglass"),
                new StainedFragileGlassBlock(DyeColor.PURPLE)
                        .setRegistryName("purplestainedfragileglass"),
                new StainedFragileGlassBlock(DyeColor.BLUE)
                        .setRegistryName("bluestainedfragileglass"),
                new StainedFragileGlassBlock(DyeColor.BROWN)
                        .setRegistryName("brownstainedfragileglass"),
                new StainedFragileGlassBlock(DyeColor.GREEN)
                        .setRegistryName("greenstainedfragileglass"),
                new StainedFragileGlassBlock(DyeColor.RED)
                        .setRegistryName("redstainedfragileglass"),
                new StainedFragileGlassBlock(DyeColor.BLACK)
                        .setRegistryName("blackstainedfragileglass"),
                //Stained fragile glass panes
                new StainedFragilePaneBlock(DyeColor.WHITE)
                        .setRegistryName("whitestainedfragileglasspane"),
                new StainedFragilePaneBlock(DyeColor.ORANGE)
                        .setRegistryName("orangestainedfragileglasspane"),
                new StainedFragilePaneBlock(DyeColor.MAGENTA)
                        .setRegistryName("magentastainedfragileglasspane"),
                new StainedFragilePaneBlock(DyeColor.LIGHT_BLUE)
                        .setRegistryName("lightbluestainedfragileglasspane"),
                new StainedFragilePaneBlock(DyeColor.YELLOW)
                        .setRegistryName("yellowstainedfragileglasspane"),
                new StainedFragilePaneBlock(DyeColor.LIME)
                        .setRegistryName("limestainedfragileglasspane"),
                new StainedFragilePaneBlock(DyeColor.PINK)
                        .setRegistryName("pinkstainedfragileglasspane"),
                new StainedFragilePaneBlock(DyeColor.GRAY)
                        .setRegistryName("graystainedfragileglasspane"),
                new StainedFragilePaneBlock(DyeColor.LIGHT_GRAY)
                        .setRegistryName("lightgraystainedfragileglasspane"),
                new StainedFragilePaneBlock(DyeColor.CYAN)
                        .setRegistryName("cyanstainedfragileglasspane"),
                new StainedFragilePaneBlock(DyeColor.PURPLE)
                        .setRegistryName("purplestainedfragileglasspane"),
                new StainedFragilePaneBlock(DyeColor.BLUE)
                        .setRegistryName("bluestainedfragileglasspane"),
                new StainedFragilePaneBlock(DyeColor.BROWN)
                        .setRegistryName("brownstainedfragileglasspane"),
                new StainedFragilePaneBlock(DyeColor.GREEN)
                        .setRegistryName("greenstainedfragileglasspane"),
                new StainedFragilePaneBlock(DyeColor.RED)
                        .setRegistryName("redstainedfragileglasspane"),
                new StainedFragilePaneBlock(DyeColor.BLACK)
                        .setRegistryName("blackstainedfragileglasspane")
        );
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                //Non-stained blocks
                new BlockItem(FRAGILE_GLASS, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS))
                        .setRegistryName("fragileglass"),
                new BlockItem(FRAGILE_PANE, new Item.Properties().group(ItemGroup.DECORATIONS))
                        .setRegistryName("fragilepane"),
                new BlockItem(SUGAR_BLOCK, new Item.Properties().group(ItemGroup.FOOD))
                        .setRegistryName("sugarblock"),
                new BlockItem(THIN_ICE, new Item.Properties().group(ItemGroup.MISC))
                        .setRegistryName("thinice"),
                new BlockItem(SUGAR_CAULDRON, new Item.Properties().group(ItemGroup.TOOLS))
                        .setRegistryName("sugarcauldron"),
                new BlockItem(WEAK_STONE, new Item.Properties().group(ItemGroup.MISC))
                        .setRegistryName("weakstone"),
                //Stained Fragile Glass blocks
                new BlockItem(WHITE_STAINED_FRAGILE_GLASS, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS))
                        .setRegistryName("whitestainedfragileglass"),
                new BlockItem(ORANGE_STAINED_FRAGILE_GLASS, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS))
                        .setRegistryName("orangestainedfragileglass"),
                new BlockItem(MAGENTA_STAINED_FRAGILE_GLASS, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS))
                        .setRegistryName("magentastainedfragileglass"),
                new BlockItem(LIGHT_BLUE_STAINED_FRAGILE_GLASS, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS))
                        .setRegistryName("lightbluestainedfragileglass"),
                new BlockItem(YELLOW_STAINED_FRAGILE_GLASS, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS))
                        .setRegistryName("yellowstainedfragileglass"),
                new BlockItem(LIME_STAINED_FRAGILE_GLASS, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS))
                        .setRegistryName("limestainedfragileglass"),
                new BlockItem(PINK_STAINED_FRAGILE_GLASS, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS))
                        .setRegistryName("pinkstainedfragileglass"),
                new BlockItem(GRAY_STAINED_FRAGILE_GLASS, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS))
                        .setRegistryName("graystainedfragileglass"),
                new BlockItem(LIGHT_GRAY_STAINED_FRAGILE_GLASS, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS))
                        .setRegistryName("lightgraystainedfragileglass"),
                new BlockItem(CYAN_STAINED_FRAGILE_GLASS, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS))
                        .setRegistryName("cyanstainedfragileglass"),
                new BlockItem(PURPLE_STAINED_FRAGILE_GLASS, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS))
                        .setRegistryName("purplestainedfragileglass"),
                new BlockItem(BLUE_STAINED_FRAGILE_GLASS, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS))
                        .setRegistryName("bluestainedfragileglass"),
                new BlockItem(BROWN_STAINED_FRAGILE_GLASS, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS))
                        .setRegistryName("brownstainedfragileglass"),
                new BlockItem(GREEN_STAINED_FRAGILE_GLASS, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS))
                        .setRegistryName("greenstainedfragileglass"),
                new BlockItem(RED_STAINED_FRAGILE_GLASS, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS))
                        .setRegistryName("redstainedfragileglass"),
                new BlockItem(BLACK_STAINED_FRAGILE_GLASS, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS))
                        .setRegistryName("blackstainedfragileglass"),
                //Stained Fragile Glass panes
                new BlockItem(WHITE_STAINED_FRAGILE_GLASS_PANE, new Item.Properties().group(ItemGroup.DECORATIONS))
                        .setRegistryName("whitestainedfragileglasspane"),
                new BlockItem(ORANGE_STAINED_FRAGILE_GLASS_PANE, new Item.Properties().group(ItemGroup.DECORATIONS))
                        .setRegistryName("orangestainedfragileglasspane"),
                new BlockItem(MAGENTA_STAINED_FRAGILE_GLASS_PANE, new Item.Properties().group(ItemGroup.DECORATIONS))
                        .setRegistryName("magentastainedfragileglasspane"),
                new BlockItem(LIGHT_BLUE_STAINED_FRAGILE_GLASS_PANE, new Item.Properties().group(ItemGroup.DECORATIONS))
                        .setRegistryName("lightbluestainedfragileglasspane"),
                new BlockItem(YELLOW_STAINED_FRAGILE_GLASS_PANE, new Item.Properties().group(ItemGroup.DECORATIONS))
                        .setRegistryName("yellowstainedfragileglasspane"),
                new BlockItem(LIME_STAINED_FRAGILE_GLASS_PANE, new Item.Properties().group(ItemGroup.DECORATIONS))
                        .setRegistryName("limestainedfragileglasspane"),
                new BlockItem(PINK_STAINED_FRAGILE_GLASS_PANE, new Item.Properties().group(ItemGroup.DECORATIONS))
                        .setRegistryName("pinkstainedfragileglasspane"),
                new BlockItem(GRAY_STAINED_FRAGILE_GLASS_PANE, new Item.Properties().group(ItemGroup.DECORATIONS))
                        .setRegistryName("graystainedfragileglasspane"),
                new BlockItem(LIGHT_GRAY_STAINED_FRAGILE_GLASS_PANE, new Item.Properties().group(ItemGroup.DECORATIONS))
                        .setRegistryName("lightgraystainedfragileglasspane"),
                new BlockItem(CYAN_STAINED_FRAGILE_GLASS_PANE, new Item.Properties().group(ItemGroup.DECORATIONS))
                        .setRegistryName("cyanstainedfragileglasspane"),
                new BlockItem(PURPLE_STAINED_FRAGILE_GLASS_PANE, new Item.Properties().group(ItemGroup.DECORATIONS))
                        .setRegistryName("purplestainedfragileglasspane"),
                new BlockItem(BLUE_STAINED_FRAGILE_GLASS_PANE, new Item.Properties().group(ItemGroup.DECORATIONS))
                        .setRegistryName("bluestainedfragileglasspane"),
                new BlockItem(BROWN_STAINED_FRAGILE_GLASS_PANE, new Item.Properties().group(ItemGroup.DECORATIONS))
                        .setRegistryName("brownstainedfragileglasspane"),
                new BlockItem(GREEN_STAINED_FRAGILE_GLASS_PANE, new Item.Properties().group(ItemGroup.DECORATIONS))
                        .setRegistryName("greenstainedfragileglasspane"),
                new BlockItem(RED_STAINED_FRAGILE_GLASS_PANE, new Item.Properties().group(ItemGroup.DECORATIONS))
                        .setRegistryName("redstainedfragileglasspane"),
                new BlockItem(BLACK_STAINED_FRAGILE_GLASS_PANE, new Item.Properties().group(ItemGroup.DECORATIONS))
                        .setRegistryName("blackstainedfragileglasspane")
        );
    }

    @SubscribeEvent
    public static void registerFeatures(RegistryEvent.Register<Feature<?>> event) {
        FEATURE_MANAGER = new FeatureManager();
        FEATURE_MANAGER.registerFeatures(event);
    }

    @SubscribeEvent
    public static void registerParticleTypes(RegistryEvent.Register<ParticleType<?>> event) {
        event.getRegistry().register(new BasicParticleType(false).setRegistryName("bubble"));
    }

    @SubscribeEvent
    public static void registerParticleFactories(ParticleFactoryRegisterEvent event) {
        Minecraft.getInstance().particles.registerFactory(BUBBLE, MyBubbleParticle.Factory::new);
    }

    @SubscribeEvent
    public static void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> event) {
        event.getRegistry().registerAll(
                TileEntityType.Builder.create(TileEntityWeakStone::new)
                        .build(null)
                        .setRegistryName(new ResourceLocation(DataReference.MODID, "tews"))
        );
    }

    public void clientSetup(FMLClientSetupEvent event)
    {
        proxy.setupRenderTypes();
    }

    /**
     * Called after all registry events. Runs in parallel with other SetupEvent handlers.
     * @param event
     */
    public void postRegistration(FMLCommonSetupEvent event) {
        PacketHandler.init();

        //Capability
        CapabilityManager.INSTANCE.register(IBreakCapability.class, new BreakCapStorage(), new BreakCapFactory());
        CapabilityManager.INSTANCE.register(IPlayerBreakCapability.class, new PlayerBreakStorage(), new PlayerBreakFactory());
        CapabilityManager.INSTANCE.register(IFragileCapability.class, new FragileCapStorage(), new FragileCapFactory());

        blockDataManager = new BlockDataManager();
        entityDataManager = new EntityDataManager();
        tileEntityDataManager = new TileEntityDataManager();
        FEATURE_MANAGER.registerGenerators();
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

    ///////////////////////
    //DATA MANAGER ACCESS//
    ///////////////////////
    public static BlockDataManager getBlockDataManager() { return blockDataManager; }
    public static EntityDataManager getEntityDataManager() { return entityDataManager; }
    public static TileEntityDataManager getTileEntityDataManager() { return tileEntityDataManager; }

    /**
     * Clear all DataManager data and reload it from the config files.
     * @return true if no errors were found; false otherwise
     */
    public static boolean reloadDataManagers() {
        FragileGlassBase.blockDataManager.clearData();
        FragileGlassBase.entityDataManager.clearData();
        FragileGlassBase.tileEntityDataManager.clearData();
        boolean blocksOK = FragileGlassBase.blockDataManager.loadData();
        boolean entitiesOK = FragileGlassBase.entityDataManager.loadData();
        boolean tilesOK = FragileGlassBase.tileEntityDataManager.loadData();
        return blocksOK && entitiesOK && tilesOK;
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
                if (e instanceof ClientPlayerEntity) {
                    MinecraftForge.EVENT_BUS.register(new Object() {
                        private PlayerEntity ep = (PlayerEntity) e;
                        private double lastSpeedSq;
                        private final Minecraft game = Minecraft.getInstance();

                        @SubscribeEvent(priority = EventPriority.HIGHEST)
                        public void speedUpdate(TickEvent.ClientTickEvent event) {
                            if(ep == game.player) {
                                if (event.phase == TickEvent.Phase.START) {
                                    Vec3d motion = ep.getMotion();
                                    double speedSq = Math.max(motion.x * motion.x + motion.y * motion.y + motion.z * motion.z, 0.0);
                                    if (Math.abs(speedSq - this.lastSpeedSq) > 0.001) {
                                        MessageBreakerMovement mbm = new MessageBreakerMovement();
                                        mbm.motionx = motion.x;
                                        mbm.motiony = motion.y;
                                        mbm.motionz = motion.z;
                                        mbm.speedSq = speedSq;
                                        PacketHandler.INSTANCE.sendToServer(mbm);
                                        this.lastSpeedSq = speedSq;
                                    }
                                    if (ep.removed) {
                                        MinecraftForge.EVENT_BUS.unregister(this);
                                    }
                                }
                            }
                        }

                        @SubscribeEvent
                        public void killObject(ClientPlayerNetworkEvent.LoggedOutEvent event) {
                            MinecraftForge.EVENT_BUS.unregister(this);
                        }
                    });
                }
            } else {
                if (e instanceof PlayerEntity) {
                    evt.addCapability(DataReference.PLAYER_BREAK_LOCATION,
                            new ICapabilityProvider() {
                                IPlayerBreakCapability inst = PLAYERBREAKCAP.getDefaultInstance();

                                @Override
                                @Nonnull
                                public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
                                    if (capability == PLAYERBREAKCAP || capability == BREAKCAP) {
                                        return LazyOptional.of(() -> (T) inst);
                                    }
                                    return LazyOptional.empty();
                                }
                            }
                    );
                } else {
                    entityDataManager.addCapabilityIfPossible(e, evt);
                }
            }
        }
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
            BreakSystem.forWorld(w).init(w);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void stopSystem(WorldEvent.Unload event) {
        World w = (World) event.getWorld();
        if(!w.isRemote) {
            BreakSystem.forWorld(w).end(w);
        }
    }

    //////////////////
    //LOGGER METHODS//
    //////////////////
    public static void error(String message) {
        LOGGER.error(message);
    }

    public static void warn(String message) {
        LOGGER.error(message);
    }

    //////////////////////////////
    //DATA MANAGER ERROR LOGGING//
    //////////////////////////////
    private static final ITextComponent SUCCESS_MESSAGE = new StringTextComponent("[FRAGILE GLASS] Data reloaded without errors!").applyTextStyle(TextFormatting.GREEN);
    private static final ITextComponent FAILURE_MESSAGE = new StringTextComponent("[FRAGILE GLASS] Errors found in config files; please check config folder for more information.").applyTextStyle(TextFormatting.RED);
    private static ITextComponent STATUS;
    private static CommandSource cachedCommandSource = null;

    public static ITextComponent setReloadStatus(boolean ok) {
        STATUS = ok ? SUCCESS_MESSAGE : FAILURE_MESSAGE;
        return STATUS;
    }

    /**
     * Sets the directory the config managers will be working from.
     * Adds a listener which refreshes DataManager data whenever Tags are reloaded.
     * @param event
     */
    @SubscribeEvent
    public void handleServerAboutToStart(FMLServerAboutToStartEvent event) {
        MinecraftServer ms = event.getServer();
        blockDataManager.setupDirsAndFiles(ms);
        entityDataManager.setupDirsAndFiles(ms);
        tileEntityDataManager.setupDirsAndFiles(ms);
        ms.getResourceManager().addReloadListener(new ReloadListener<Map<ResourceLocation, Tag.Builder<EntityType<?>>>>() {
            @Override
            protected Map<ResourceLocation, Tag.Builder<EntityType<?>>> prepare(IResourceManager iResourceManager, IProfiler iProfiler) {
                return null;
            }

            @Override
            protected void apply(Map<ResourceLocation, Tag.Builder<EntityType<?>>> resourceLocationBuilderMap, IResourceManager iResourceManager, IProfiler iProfiler) {
                setReloadStatus(FragileGlassBase.reloadDataManagers());
                if(cachedCommandSource != null) {
                    cachedCommandSource.sendFeedback(STATUS, true);
                    cachedCommandSource = null;
                }
            }
        });
    }

    /**
     * Register the mod's commands.
     */
    @SubscribeEvent
    public void registerCommands(FMLServerStartingEvent event) {
        CommandsBase.registerCommands(event.getCommandDispatcher());
    }

    /**
     * When a player logs in, if they are in single player or if they are an op, they should know whether it loaded successfully last time.
     * @param event
     */
    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerEntity pe = event.getPlayer();
        //Only ops or single players can see the message
        if(pe.hasPermissionLevel(2) || pe.world.getServer().isSinglePlayer()) {
            //Always show the failure message
            if(STATUS == FAILURE_MESSAGE) {
                pe.sendStatusMessage(STATUS, false);
            }
            //Only show the success message if enabled in config
            else if(WorldgenConfig.SHOW_SUCCESS_MESSAGE.get()) {
                pe.sendStatusMessage(STATUS, false);
            }
        }
    }

    @SubscribeEvent
    public void onReloadCommand(CommandEvent ce) {
        if(ce.getParseResults().getReader().getString().equals("/reload")) {
            cachedCommandSource = ce.getParseResults().getContext().getSource();
        }
    }
}
