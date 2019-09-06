package com.fredtargaryen.fragileglass.config.behaviour.datamanager;

import com.fredtargaryen.fragileglass.config.behaviour.configloader.BlockConfigLoader;
import com.fredtargaryen.fragileglass.config.behaviour.configloader.ConfigLoader;
import com.fredtargaryen.fragileglass.config.behaviour.data.FragilityData;
import net.minecraft.block.BlockState;

import javax.annotation.Nullable;
import java.util.ArrayList;

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
            this.blockConfigLoader.parseArbitraryString("#fragileglassft:fragile_glass BREAK 0.165");
            this.blockConfigLoader.parseArbitraryString("fragileglassft:thinice BREAK 0.0");
        }
        catch(ConfigLoader.ConfigLoadException cle) {
            System.out.println("FredTargaryen is an idiot; please let him know you saw this");
        }
    }

    @Override
    public void parseConfigLine(String configLine) throws ConfigLoader.ConfigLoadException {
        this.blockConfigLoader.parseArbitraryString(configLine);
    }

    @Override
    public void removeBehaviour(BlockState key, @Nullable FragilityData.FragileBehaviour behaviour) {
        if(behaviour == null) {
            this.data.remove(key);
        }
        else {
            ArrayList<FragilityData> list = this.data.get(key);
            if (list != null) {
                list.removeIf(fd -> fd.getBehaviour() == behaviour);
            }
        }
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
            "@* Custom mod crash behaviours are currently not supported; however existing behaviours can be added \n",
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
            "@  - update: a block update is triggered.\n",
            "@  - change: the block changes into a specified blockstate.\n",
            "@  - fall: the block falls immediately.\n",
            "@  - mod: for mod tile entities with custom behaviours ONLY.\n",
            "@* The update behaviour requires one extra value: the number of ticks to wait before updating (a tick\n",
            "@  is 1/20 of a second).\n",
            "@* The change behaviour requires one extra value: the state the block will change into. It must have\n",
            "@  the format of a block or blockstate; you can see examples below. Leaving a - here will make the\n",
            "@  block transform into the same block it was before, with any properties you specify. Any unspecified\n",
            "@  properties are carried over from the previous state.\n",
            "@* breakSpeed is a minimum speed (must be decimal). The breaker must be moving above their\n",
            "@  breaking speed, AND above this speed, to trigger the crash behaviour. Speed is measured in blocks\n",
            "@  per tick, which is metres per second divided by 20.\n",
            "@* A block state can have multiple behaviours using multiple lines, which will trigger in the order\n",
            "@  they are listed. However, only the first of each behaviour type will trigger.\n",
            "\n@--Fun example lines you may wish to uncomment--\n",
            "@Make vanilla glass and ice fragile too\n",
            "@minecraft:ice break 0.0\n",
            "@#minecraft:impermeable break 0.165\n",
            "@#fragileglassft:glass_panes break 0.165\n",
            "@Make obsidian as fragile as it is IRL\n",
            "@minecraft:obsidian break 0.165\n",
            "@Weak sandstone\n",
            "@minecraft:sandstone fall 0.0\n",
            "@minecraft:red_sandstone fall 0.0\n",
            "@Burst through doors when sprinting into them\n",
            "@#minecraft:wooden_doors[open=false] change 0.165 -[open=true]\n",
            "@Cause suspended sand to fall when you are near it\n",
            "@#minecraft:sand update 0.0 10\n",
            "@Safe lava that turns into slime at the last minute\n",
            "@minecraft:lava change 0.0 minecraft:slime_block\n",
            "\n@--Default values, in case you break something--\n",
            "@Fragile Glass blocks and panes:\n",
            "@#fragileglassft:fragile_glass break 0.165\n",
            "@Thin ice:\n",
            "@fragileglassft:thinice break 0.0\n\n",
            "#fragileglassft:fragile_glass break 0.165\n",
            "fragileglassft:thinice break 0.0\n"
    };
}
