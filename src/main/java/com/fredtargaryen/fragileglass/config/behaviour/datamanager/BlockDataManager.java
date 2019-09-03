package com.fredtargaryen.fragileglass.config.behaviour.datamanager;

import com.fredtargaryen.fragileglass.config.behaviour.data.FragilityData;
import com.fredtargaryen.fragileglass.config.behaviour.configloader.BlockConfigLoader;
import com.fredtargaryen.fragileglass.config.behaviour.configloader.ConfigLoader;
import net.minecraft.block.BlockState;

import java.util.ArrayList;

/**
 * Responsible for everything to do with block fragility data from fragileglassft_blocks.cfg.
 */
public class BlockDataManager extends DataManager<BlockState, ArrayList<FragilityData>> {

    /**
     * Processes config lines from files or code - maybe commands in the future
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
            this.blockConfigLoader.parseArbitraryString("#fragileglassft:fragile_glass BREAK 0.165 0 -");
            this.blockConfigLoader.parseArbitraryString("fragileglassft:thinice BREAK 0.0 0 -");
        }
        catch(ConfigLoader.ConfigLoadException cle) {
            System.out.println("FredTargaryen is an idiot; please let him know you saw this");
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
            "@* If you specify block states you should be as specific as possible; if you leave out a property it\n",
            "@  will only work for blocks with the properties you specified, and the default for everything else.\n",
            "\n@--How to customise--\n",
            "@To add a comment to the file, start the line with a @ symbol.\n",
            "@To make a block fragile, add a new row in this file following this format:\n",
            "@modid:ID[properties] BREAK/UPDATE/CHANGE/FALL/MOD minSpeed updateDelay new state extraValues\n",
            "@* 'modid:ID' is the ResourceLocation string used to register with Forge.\n",
            "@  You can usually find this by looking at the block in-game with the F3 menu on - below it are the\n",
            "@  blockstate properties.\n",
            "@  - Only add the properties if you are specifying behaviour for specific blockstates.\n",
            "@    Unspecified properties carry over from the block being changed; see the door example below.\n",
            "@  - Any specified properties that the block doesn't have will be quietly ignored.\n",
            "@* For all crash behaviours, the 'breaker' entity must be travelling above its minimum speed. If so,\n",
            "@  it must then be above the speed defined for the block. Meeting both these conditions causes the\n",
            "@  crash behaviour to trigger.\n",
            "@  - 'BREAK': the block breaks immediately.\n",
            "@  - 'UPDATE': a block update is triggered.\n",
            "@  - 'CHANGE': the block changes into a specified blockstate.\n",
            "@  - 'FALL': the block falls immediately.\n",
            "@  - 'MOD': for mod tile entities with custom behaviours ONLY.\n",
            "@* Crash behaviours can be combined, and will trigger (if fast enough) in the order they are listed in\n",
            "@  the config messages. However, only the first of each behaviour type will trigger.\n",
            "@* minSpeed is a minimum speed (must be decimal). The breaker must be moving above their\n",
            "@  breaking speed, AND above this speed, to trigger the crash behaviour. Speed is measured in blocks\n",
            "@  per tick, which is metres per second divided by 20.\n",
            "@* updateDelay is only used by the UPDATE behaviour. It must be an integer. It specifies the delay\n",
            "@  between the collision and the block update. Delays are measured in ticks and there are 20 ticks per\n",
            "@  second.\n",
            "@* newState is only used by the CHANGE behaviour. It must have the format of a block or blockstate; you\n",
            "@  can see examples below. This is the state the block will change into. Leaving a - here will make the\n",
            "@  block transform into the same block it was before, with any properties you specify.\n",
            "@* You can add extra values of any format, separated by spaces, for any mod blocks that might require\n",
            "@  them.\n",
            "\n@--Fun example lines you may wish to uncomment--\n",
            "@Make vanilla glass and ice fragile too\n",
            "@minecraft:ice BREAK 0.0 0 -\n",
            "@#minecraft:impermeable BREAK 0.165 0 -\n",
            "@#fragileglassft:glass_panes BREAK 0.165 0 -\n",
            "@Make obsidian as fragile as it is IRL\n",
            "@minecraft:obsidian BREAK 0.165 0 -\n",
            "@Weak sandstone\n",
            "@minecraft:sandstone FALL 0.0 0 minecraft:sandstone\n",
            "@minecraft:red_sandstone FALL 0.0 0 minecraft:red_sandstone\n",
            "@Burst through doors when sprinting into them\n",
            "@#minecraft:wooden_doors[open=false] CHANGE 0.165 0 -[open=true]\n",
            "@Cause suspended sand to fall when you are near it\n",
            "@#minecraft:sand UPDATE 0.0 10 -\n",
            "@Safe lava that turns into slime at the last minute\n",
            "@minecraft:lava CHANGE 0.0 0 minecraft:slime_block\n",
            "\n@--Default values, in case you break something--\n",
            "@Fragile Glass blocks and panes:\n",
            "@#fragileglassft:fragile_glass BREAK 0.165 0 -\n",
            "@Thin ice:\n",
            "@fragileglassft:thinice BREAK 0.0 0 -\n\n",
            "#fragileglassft:fragile_glass BREAK 0.165 0 -\n",
            "fragileglassft:thinice BREAK 0.0 0 -\n"
    };
}
