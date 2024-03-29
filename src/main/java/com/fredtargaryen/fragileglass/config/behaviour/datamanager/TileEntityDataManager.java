package com.fredtargaryen.fragileglass.config.behaviour.datamanager;

import com.fredtargaryen.fragileglass.config.behaviour.configloader.ConfigLoader;
import com.fredtargaryen.fragileglass.config.behaviour.configloader.TileEntityConfigLoader;
import com.fredtargaryen.fragileglass.config.behaviour.data.FragilityData;
import net.minecraft.tileentity.TileEntityType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TileEntityDataManager extends DataManager<TileEntityType, ArrayList<FragilityData>> {
    /**
     * Processes config lines from files, code or commands
     */
    private TileEntityConfigLoader tileEntityConfigLoader;

    public TileEntityDataManager() {
        super("tileentities");
        this.tileEntityConfigLoader = new TileEntityConfigLoader(this, this.data);
    }

    @Override
    protected String[] getDefaultConfigFileText() { return defaultFileData; }

    @Override
    public boolean loadData() {
        return this.loadDataFromConfigDir(this.tileEntityConfigLoader);
    }

    @Override
    public void parseConfigLine(String configLine, boolean add, int changeIndex) throws ConfigLoader.ConfigLoadException {
        this.tileEntityConfigLoader.parseArbitraryString(configLine, add, changeIndex);
    }

    @Override
    public void removeBehaviour(TileEntityType key, @Nullable FragilityData.FragileBehaviour behaviour) {
        if(behaviour == null) {
            this.data.remove(key);
        }
        else {
            ArrayList<FragilityData> list = this.data.get(key);
            if(list == null) {
                this.data.remove(key);
            }
            else {
                list.removeIf(fd -> fd.getBehaviour() == behaviour);
                if(list.isEmpty())
                {
                    this.data.remove(key);
                }
            }
        }
    }

    @Override
    public String stringifyBehaviours(TileEntityType key, @Nullable FragilityData.FragileBehaviour behaviour, boolean showNumbers) {
        StringBuilder sb = new StringBuilder();
        List<FragilityData> list = this.data.get(key);
        for(int i = 0; i < list.size(); i++) {
            FragilityData fd = list.get(i);
            if(behaviour == null || behaviour == fd.getBehaviour()) {
                if(showNumbers) {
                    sb.append('[');
                    sb.append(i);
                    sb.append("] ");
                }
                sb.append(key.getRegistryName());
                sb.append(" ");
                sb.append(fd.toString());
                sb.append("\n");
            }
        }
        return sb.toString();
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
            "@* All blocks which have the tile entity will have the same behaviour, so less config lines are needed.\n",
            "@* Tile entities from other mods are able to use code to achieve more complex crash behaviours.\n",

            "\n@--Limitations--\n",
            "@* This will not work for blocks which are basically air blocks, e.g. Air blocks and 'logic' blocks.\n",

            "\n@--How to customise--\n",
            "@To add a comment to the file, start the line with a @ symbol.\n",
            "@EVERY line has at least three parameters, separated by a single space character:\n",
            "@modid:ID[properties] <behaviour> <breakSpeed>\n",
            "@* 'modid:ID' is the ResourceLocation string used to register with Forge.\n",
            "@  - modid can be found by looking in the 'modId' entry of the mod's mods.toml file. For vanilla\n",
            "@    Minecraft this is just 'minecraft'. Finding the ID may need some investigation of the mod's code.\n",
            "@* The current list of possible behaviours is:\n",
            "@  - break: the block breaks immediately.\n",
            "@  - change: the block changes into a specified blockstate.\n",
            "@  - command: the block executes a server command.\n",
            "@  - damage: the block damages the entity that crashed into it.\n",
            "@  - explode: the block produces an explosion.\n",
            "@  - fall: the block falls immediately.\n",
            "@  - mod: for tile entities from mods with custom behaviours. Consult the mod source code or developer\n",
            "@    for what extra values are needed.\n",
            "@  - update: a block update is triggered.\n",
            "@  - wait: further behaviours will trigger after a certain number of ticks.\n",
            "@* breakSpeed is a minimum speed (must be decimal). The breaker must be moving above their\n",
            "@  breaking speed, AND above this speed, to trigger the crash behaviour. Speed is measured in blocks\n",
            "@  per tick, which is metres per second divided by 20.\n",
            "@* The change behaviour requires one extra value: the state the block will change into. It must have\n",
            "@  the format of a block or blockstate; you can see examples below.\n",
            "@* The command behaviour requires the word block or entity, specifying whether the command should be\n",
            "@  executed relative to the block being crashed into, or the entity crashing into the block. The command\n",
            "@  it executes is typed after this value. Commands are not validated by the mod; you have to get them right.\n",
            "@* The damage behaviour requires three values: the type of damage to deal (most likely just 'fall');\n",
            "@  the amount of damage to deal; whether to scale this amount by the speed of the crasher.\n",
            "@* The explode behaviour requires a value for the strength of the explosion. This must be between 1.0\n",
            "@  (equivalent to a Ghast fireball) and 100.0. The Wither's starting explosion is 7.0, and 8.0 will\n",
            "@  break any block breakable in survival.\n",
            "@* The update behaviour requires one extra value: the number of ticks to wait before updating (a tick\n",
            "@  is 1/20 of a second).\n",
            "@* The wait behaviour requires one extra value: the number of ticks to wait before doing the rest of\n",
            "@  the behaviours set for this tile entity.\n",
            "@* A tile entity can have multiple behaviours using multiple lines, which will trigger in the order\n",
            "@  they are listed.\n",
            "@* If a tile entity has a non-break behaviour but no break behaviour, the non-break behaviour will\n",
            "@  execute multiple times. To ensure a behaviour triggers only once, add a behaviour that gets rid of\n",
            "@  the tile entity, such as break or change.\n",

            "\n@--The wait behaviour--\n",
            "@Certain behaviours cannot be added after a wait behaviour. These behaviours are:\n",
            "@* command, if the word is entity: this depends on an entity which may not exist when the wait is over.\n",
            "@* damage: this depends on an entity which may not exist when the wait is over.\n",
            "@* mod: this MAY depend on an entity or tile entity which may not exist when the wait is over.\n",

            "\n@--Fun example lines you may wish to uncomment--\n",
            "@Currently none, but I am open to suggestions! You can look in fragileglassft_blocks.cfg for block examples.\n",
    };
}
