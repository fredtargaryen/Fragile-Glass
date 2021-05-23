package com.fredtargaryen.fragileglass.config.behaviour.datamanager;

import com.fredtargaryen.fragileglass.config.behaviour.configloader.BlockConfigLoader;
import com.fredtargaryen.fragileglass.config.behaviour.configloader.ConfigLoader;
import com.fredtargaryen.fragileglass.config.behaviour.configloader.KeyParser;
import com.fredtargaryen.fragileglass.config.behaviour.data.FragilityData;
import net.minecraft.block.BlockState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Responsible for everything to do with block fragility data from fragileglassft_blocks.cfg.
 */
public class BlockDataManager extends DataManager<BlockState, ArrayList<FragilityData>> {

    /**
     * Processes config lines from files, code or commands
     */
    private BlockConfigLoader blockConfigLoader;

    public BlockDataManager() {
        super("blocks");
        this.blockConfigLoader = new BlockConfigLoader(this, this.data);
    }

    @Override
    protected String[] getDefaultConfigFileText() { return defaultFileData; }

    @Override
    public boolean loadData() {
        return this.loadDataFromConfigDir(this.blockConfigLoader);
    }

    @Override
    protected void loadDefaultData() {
        super.loadDefaultData();
        try {
            this.blockConfigLoader.parseArbitraryString("#fragileglassft:fragile_glass break 0.165", true, -1);
            this.blockConfigLoader.parseArbitraryString("fragileglassft:thinice break 0.0", true, -1);
            this.blockConfigLoader.parseArbitraryString("fragileglassft:weakstone update 0.0 20", true, -1);
        }
        catch(ConfigLoader.ConfigLoadException cle) {
            System.out.println("FredTargaryen is an idiot; please let him know you saw this");
        }
    }

    @Override
    public void parseConfigLine(String configLine, boolean add, int changeIndex) throws ConfigLoader.ConfigLoadException {
        this.blockConfigLoader.parseArbitraryString(configLine, add, changeIndex);
    }

    @Override
    public void removeBehaviour(BlockState key, @Nullable FragilityData.FragileBehaviour behaviour) {
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
    public String stringifyBehaviours(BlockState key, @Nullable FragilityData.FragileBehaviour behaviour, boolean showNumbers) {
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
                sb.append(KeyParser.cleanBlockStateString(key.toString()));
                sb.append(" ");
                sb.append(fd.toString());
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    //Doesn't look like I can read from assets so sadly all this is needed for now
    private static final String[] defaultFileData = new String[] {
            "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n",
            "@FRAGILE GLASS AND THIN ICE CONFIG FILE - BLOCKS@\n",
            "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n",
            "@THINK VERY CAREFULLY AND BACK UP YOUR WORLDS BEFORE ADDING ENTRIES HERE!\n",
            "@(You probably don't really want to make ALL DIRT BLOCKS fragile, for example.)\n",
            "@Here is where you can configure which blocks are fragile and which are not, and modify basic behaviour.\n",

            "\n@--Limitations--\n",
            "@* This will not work for blocks which are basically air blocks, e.g. Air blocks and 'logic' blocks.\n",
            "@* Custom mod crash behaviours are not supported for blocks; however existing behaviours can be added \n",
            "@  to mod blocks here.\n",

            "\n@--How to customise--\n",
            "@To add a comment to the file, start the line with a @ symbol.\n",
            "@EVERY line has at least three parameters, separated by a single space character:\n",
            "@modid:ID[properties] <behaviour> <breakSpeed>\n",
            "@* 'modid:ID' is the ResourceLocation string used to register with Forge.\n",
            "@  You can usually find this by looking at the block in-game with the F3 menu on - below it are the\n",
            "@  blockstate properties.\n",
            "@  - Only add the properties if you are specifying behaviour for specific blockstates.\n",
            "@    Unspecified properties carry over from the block being changed; see the door example below.\n",
            "@  - Any specified properties that the block doesn't have will be quietly ignored.\n",
            "@* The current list of possible behaviours is:\n",
            "@  - break: the block breaks immediately.\n",
            "@  - change: the block changes into a specified blockstate.\n",
            "@  - command: the block executes a server command.\n",
            "@  - damage: the block damages the entity that crashed into it.\n",
            "@  - explode: the block produces an explosion.\n",
            "@  - fall: the block falls immediately.\n",
            "@  - update: a block update is triggered.\n",
            "@  - wait: further behaviours will trigger after a certain number of ticks.\n",
            "@* breakSpeed is a minimum speed (must be decimal). The breaker must be moving above their\n",
            "@  breaking speed, AND above this speed, to trigger the crash behaviour. Speed is measured in blocks\n",
            "@  per tick, which is metres per second divided by 20.\n",
            "@* The change behaviour requires one extra value: the state the block will change into. It must have\n",
            "@  the format of a block or blockstate; you can see examples below. Leaving a - here will make the\n",
            "@  block transform into the same block it was before, with any properties you specify. Any unspecified\n",
            "@  properties are carried over from the previous state.\n",
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
            "@  the behaviours set for this blockstate.\n",
            "@* A block state can have multiple behaviours using multiple lines, which will trigger in the order\n",
            "@  they are listed.\n",
            "@* If a blockstate has a non-break behaviour but no break behaviour, the non-break behaviour will\n",
            "@  execute multiple times. To ensure a behaviour triggers only once, add a behaviour that gets rid of\n",
            "@  the blockstate, such as break or change.\n",

            "\n@--The wait behaviour--\n",
            "@Certain behaviours cannot be added after a wait behaviour. These behaviours are:\n",
            "@* command, if the word is entity: this depends on an entity which may not exist when the wait is over.\n",
            "@* damage: this depends on an entity which may not exist when the wait is over.\n",

            "\n@--Fun example lines you may wish to uncomment--\n",
            "@Make vanilla glass and ice fragile too\n",
            "@minecraft:ice break 0.0\n",
            "@#minecraft:impermeable break 0.165\n",
            "@#fragileglassft:glass_panes break 0.165\n",
            "@Burst through doors when sprinting into them\n",
            "@#minecraft:wooden_doors[open=false] change 0.165 -[open=true]\n",
            "@And have them close behind you after a second\n",
            "@#minecraft:wooden_doors[open=false] wait 0.165 20\n",
            "@#minecraft:wooden_doors[open=false] change 0.165 -\n",
            "@Magma cube pops out of smashed magma block\n",
            "@minecraft:magma_block break 0.165\n",
            "@minecraft:magma_block command 0.165 block /summon minecraft:magma_cube\n",
            "@Make it hurt when you break through glass\n",
            "@#minecraft:impermeable break 0.165\n",
            "@#minecraft:impermeable damage 0.165 fall 1.0 false\n",
            "@TNT explodes immediately if bumped\n",
            "@minecraft:tnt change 0.05 minecraft:air\n",
            "@minecraft:tnt explode 0.05 4.0\n",
            "@Weak sandstone\n",
            "@minecraft:sandstone fall 0.0\n",
            "@minecraft:red_sandstone fall 0.0\n",
            "@Cause suspended sand to fall when you are near it\n",
            "@#minecraft:sand update 0.0 10\n",
            "\n@--Default values, in case you break something--\n",
            "@Fragile Glass blocks and panes:\n",
            "@#fragileglassft:fragile_glass break 0.165\n",
            "@Thin ice:\n",
            "@fragileglassft:thinice break 0.0\n",
            "@Weak stone:\n",
            "@fragileglassft:weakstone update 0.0 20\n\n",

            "#fragileglassft:fragile_glass break 0.165\n",
            "fragileglassft:thinice break 0.0\n",
            "fragileglassft:weakstone update 0.0 20\n",
    };
}
