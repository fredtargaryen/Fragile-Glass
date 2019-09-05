package com.fredtargaryen.fragileglass.config.behaviour.datamanager;

import com.fredtargaryen.fragileglass.DataReference;
import com.fredtargaryen.fragileglass.FragileGlassBase;
import com.fredtargaryen.fragileglass.config.behaviour.configloader.ConfigLoader;
import com.fredtargaryen.fragileglass.config.behaviour.configloader.TileEntityConfigLoader;
import com.fredtargaryen.fragileglass.config.behaviour.data.FragilityData;
import com.fredtargaryen.fragileglass.tileentity.capability.IFragileCapability;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;

public class TileEntityDataManager extends DataManager<TileEntityType, ArrayList<FragilityData>> {
    /**
     * Processes config lines from files, code or commands
     */
    private TileEntityConfigLoader tileEntityConfigLoader;

    public TileEntityDataManager() {
        super("tileentities");
        this.tileEntityConfigLoader = new TileEntityConfigLoader(this, this.data);
    }

    public void addCapabilityIfPossible(TileEntity te, AttachCapabilitiesEvent<TileEntity> evt) {
        ArrayList<FragilityData> fragDataList = this.data.get(te.getType());
        if (fragDataList != null) {
            if (!evt.getCapabilities().containsKey(DataReference.FRAGILE_CAP_LOCATION)) {
                ICapabilityProvider iCapProv = new ICapabilityProvider() {
                    IFragileCapability inst = new IFragileCapability() {
                        @Override
                        public void onCrash(BlockState state, TileEntity te, Entity crasher, double speedSq) {
                            for (FragilityData fragData : fragDataList) {
                                fragData.onCrash(state, te, te.getPos(), crasher, speedSq);
                            }
                        }
                    };

                    @Nonnull
                    @Override
                    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
                        return cap == FragileGlassBase.FRAGILECAP ? LazyOptional.of(() -> (T) inst) : LazyOptional.empty();
                    }
                };
                evt.addCapability(DataReference.FRAGILE_CAP_LOCATION, iCapProv);
            }
        }
    }

    @Override
    protected String[] getDefaultConfigFileText() { return defaultFileData; }

    @Override
    protected void loadDefaultData() {
        super.loadDefaultData();
        try {
            this.tileEntityConfigLoader.parseArbitraryString("fragileglassft:tews UPDATE 0.0 10");
        }
        catch(ConfigLoader.ConfigLoadException cle) {
            System.out.println("FredTargaryen is an idiot; please let him know you saw this");
        }
    }

    @Override
    public boolean loadData() {
        return this.loadDataFromConfigDir(this.tileEntityConfigLoader);
    }

    @Override
    public void parseConfigLine(String configLine) throws ConfigLoader.ConfigLoadException {
        this.tileEntityConfigLoader.parseArbitraryString(configLine);
    }

    @Override
    public void removeBehaviour(TileEntityType key, @Nullable FragilityData.FragileBehaviour behaviour) {
        if(behaviour != null) {
            ArrayList<FragilityData> list = this.data.get(key);
            if (list != null) {
                list.removeIf(fd -> fd.getBehaviour() == behaviour);
            }
        }
    }

    //Doesn't look like I can read from assets so sadly all this is needed for now
    private static final String[] defaultFileData = new String[] {
            "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n",
            "@FRAGILE GLASS AND THIN ICE CONFIG FILE - TILE ENTITIES@\n",
            "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n",
            "@THINK VERY CAREFULLY AND BACK UP YOUR WORLDS BEFORE ADDING ENTRIES HERE!\n",
            "@(You probably don't really want to make ENDER CHESTS fragile, for example.)\n",
            "@Here is where you can configure which tile entities are fragile and which are not, and modify basic behaviour.\n",
            "@Using tile entities has some advantages over using blocks:\n",
            "@* All blocks which have the tile entity will have the same behaviour, so less lines are needed.\n",
            "@* Tile entities from other mods are able to use code to achieve more complex crash behaviours.\n",
            "\n@--Limitations--\n",
            "@* This will not work for blocks which are basically air blocks, e.g. Air blocks and 'logic' blocks.\n",
            "@* Custom mod crash behaviours are currently not supported; however existing behaviours can be added \n",
            "@  to mod blocks here.\n",
            "\n@--How to customise--\n",
            "@To add a comment to the file, start the line with a @ symbol.\n",
            "@EVERY line has at least three parameters, separated by a single space character:\n",
            "@modid:ID[properties] <behaviour> <breakSpeed>\n",
            "@* 'modid:ID' is the ResourceLocation string used to register with Forge.\n",
            "@  - modid can be found by looking in the 'modId' entry of the mod's mods.toml file. For vanilla\n",
            "@    Minecraft this is just 'minecraft'. Finding the ID may need some investigation of the mod's code.\n",
            "@* The current list of possible behaviours is:\n",
            "@  - break: the block breaks immediately.\n",
            "@  - update: a block update is triggered.\n",
            "@  - change: the block changes into a specified blockstate.\n",
            "@  - fall: the block falls immediately.\n",
            "@  - mod: for mod tile entities with custom behaviours ONLY.\n",
            "@  - 'BREAK': the block breaks immediately.\n",
            "@  - 'UPDATE': a block update is triggered.\n",
            "@  - 'CHANGE': the block changes into a specified blockstate.\n",
            "@  - 'FALL': the block falls immediately.\n",
            "@  - 'MOD': for mod tile entities with custom behaviours ONLY. Modders should make custom tile\n",
            "@           entities and implement IFragileCapability with the behaviour they want. The mod receives all\n",
            "@           the extra values and it is up to the modder how they are used. NOTE: If a tile entity has a\n",
            "@           custom behaviour it will be used regardless of the behaviour value.\n",
            "@* The update behaviour requires one extra value: the number of ticks to wait before updating (a tick\n",
            "@  is 1/20 of a second).\n",
            "@* The change behaviour requires one extra value: the state the block will change into. It must have\n",
            "@  the format of a block or blockstate; you can see examples below. You should not leave a - here. Any\n",
            "@  unspecified properties will have the default value for the blockstate.\n",
            "@* breakSpeed is a minimum speed (must be decimal). The breaker must be moving above their\n",
            "@  breaking speed, AND above this speed, to trigger the crash behaviour. Speed is measured in blocks\n",
            "@  per tick, which is metres per second divided by 20.\n",
            "@* A tile entity can have multiple behaviours using multiple lines, which will trigger in the order\n",
            "@  they are listed. However, only the first of each behaviour type will trigger.\n",
            "\n@--Fun example lines you may wish to uncomment--\n",
            "@Currently none, but I am open to suggestions!\n",
            "\n@--Default values, in case you break something--\n",
            "@Weak stone:\n",
            "@fragileglassft:tews update 0.0 10\n\n",
            "fragileglassft:tews update 0.0 10\n"
    };
}
